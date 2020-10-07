package org.legion.aegis.general.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.base.Stopwatch;
import org.apache.commons.text.StringEscapeUtils;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.consts.ContentConsts;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.CollectionUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.dao.EmailDAO;
import org.legion.aegis.general.dto.EmailDto;
import org.legion.aegis.general.dto.Recipient;
import org.legion.aegis.general.entity.*;
import org.legion.aegis.general.vo.EmailAttachmentVO;
import org.legion.aegis.general.vo.EmailReplyVO;
import org.legion.aegis.general.vo.EmailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private final FileNetService fileNetService;
    private final EmailDAO emailDAO;
    private final UserAccountService userAccountService;

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public EmailService (FileNetService fileNetService, EmailDAO emailDAO, UserAccountService userAccountService) {
        this.fileNetService = fileNetService;
        this.emailDAO = emailDAO;
        this.userAccountService = userAccountService;
    }

    @Transactional
    public void sendEmail(EmailDto dto) throws Exception {
        if (dto != null) {
            AppContext context = AppContext.getFromWebThread();
            Email email = saveEmail(dto, false);
            String[] sendTos = trim(dto.getSentTo()).split(";");
            String[] ccs = trim(dto.getCc()).split(";");
            sendToInbox(email, sendTos, ccs);
        }
    }

    public SearchResult<EmailVO> searchInbox(SearchParam param) {
        AppContext context = AppContext.getFromWebThread();
        if (param != null) {
            param.addParam("sentTo", context.getLoginId());
        }
        List<EmailVO> results = emailDAO.searchMailInbox(param);
        for (EmailVO emailVO : results) {
            emailVO.setContentString(formatLength(new String(emailVO.getContent(), StandardCharsets.UTF_8), 50));
            emailVO.setSubject(formatLength(emailVO.getSubject(), 20));
            List<EmailReplyInbox> replyInboxList = emailDAO.retrieveReplyInboxByEmailId(emailVO.getEmailId());
            replyInboxList.removeIf(var -> !context.getLoginId().equals(var.getRecipient()));
            if (!replyInboxList.isEmpty()) {
                emailVO.setIsReplyRead(AppConsts.YES);
                for (EmailReplyInbox replyInbox : replyInboxList) {
                    if (AppConsts.NO.equals(emailVO.getIsHasAttachment())) {
                        EmailReply reply = emailDAO.retrieveEmailReplyById(replyInbox.getEmailReplyId());
                        emailVO.setIsHasAttachment(reply.getIsHasAttachment());
                    }
                    if (AppConsts.YES.equals(emailVO.getIsReplyRead())) {
                        emailVO.setIsReplyRead(replyInbox.getIsRead());
                    }
                }
                setReplyPeriod(replyInboxList.get(0).getCreatedAt(), emailVO);
            } else {
                setReplyPeriod(DateUtils.parseDatetime(emailVO.getCreatedAt()), emailVO);
            }
        }
        results.sort(Comparator.comparing(EmailVO::getLastReplyOn, Comparator.reverseOrder()));
        SearchResult<EmailVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(emailDAO.searchMailInBoxCount(param));
        return searchResult;
    }

    public SearchResult<EmailVO> searchOutbox(SearchParam param) {
        AppContext context = AppContext.getFromWebThread();
        if (param != null) {
            param.addParam("sender", context.getLoginId());
        }
        List<EmailVO> results = emailDAO.searchMailOutbox(param);
        for (EmailVO emailVO : results) {
            emailVO.setContentString(formatLength(new String(emailVO.getContent(), StandardCharsets.UTF_8), 50));
            emailVO.setSubject(formatLength(emailVO.getSubject(), 20));
            List<EmailReplyInbox> replyInboxList = emailDAO.retrieveReplyInboxByEmailId(emailVO.getEmailId());
            replyInboxList.removeIf(var -> !context.getLoginId().equals(var.getRecipient()));
            if (!replyInboxList.isEmpty()) {
                setReplyPeriod(replyInboxList.get(0).getCreatedAt(), emailVO);
            } else {
                setReplyPeriod(DateUtils.parseDatetime(emailVO.getUpdatedAt()), emailVO);
            }
        }
        results.sort(Comparator.comparing(EmailVO::getLastReplyOn, Comparator.reverseOrder()));
        SearchResult<EmailVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(emailDAO.searchMailOutboxCount(param));
        return searchResult;
    }

    public SearchResult<EmailVO> searchDraftBox(SearchParam param) {
        AppContext context = AppContext.getFromWebThread();
        if (param != null) {
            param.addParam("sender", context.getLoginId());
        }
        List<EmailVO> results = emailDAO.searchMailDraftBox(param);
        for (EmailVO emailVO : results) {
            emailVO.setContentString(formatLength(new String(emailVO.getContent(), StandardCharsets.UTF_8), 50));
            emailVO.setSubject(formatLength(emailVO.getSubject(), 20));
            setReplyPeriod(DateUtils.parseDatetime(emailVO.getUpdatedAt()), emailVO);
        }
        results.sort(Comparator.comparing(EmailVO::getLastReplyOn, Comparator.reverseOrder()));
        SearchResult<EmailVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(emailDAO.searchMailDraftBoxCount(param));
        return searchResult;
    }
    public SearchResult<EmailVO> searchRecycleBox(SearchParam param) {
        AppContext context = AppContext.getFromWebThread();
        if (param != null) {
            param.addParam("sentTo", context.getLoginId());
        }
        List<EmailVO> results = emailDAO.searchMailRecycleBox(param);

        for (EmailVO emailVO : results) {
            emailVO.setSubject(formatLength(emailVO.getSubject(), 20));
            emailVO.setContentString(formatLength(new String(emailVO.getContent(), StandardCharsets.UTF_8), 50));
            setReplyPeriod(DateUtils.parseDatetime(emailVO.getUpdatedAt()), emailVO);
        }
        results.sort(Comparator.comparing(EmailVO::getLastReplyOn, Comparator.reverseOrder()));
        SearchResult<EmailVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(emailDAO.searchMailRecycleBoxCount(param));
        return searchResult;
    }


    @Transactional
    public EmailVO readEmail(Long inboxId) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        EmailInbox inbox = emailDAO.retrieveEmailInboxById(inboxId);
        AppContext context = AppContext.getFromWebThread();
        ObjectMapper objectMapper = new ObjectMapper();
        if (inbox != null) {
            EmailOutbox outbox = emailDAO.retrieveEmailOutboxByEmailId(inbox.getEmailId());
            Email email = emailDAO.retrieveEmailById(inbox.getEmailId());
            List<EmailInbox> recipientsInboxes = emailDAO.retrieveAllRecipientsInbox(inbox.getEmailId());
            recipientsInboxes.removeIf(var -> var.getStatus().equals(AppConsts.EMAIL_STATUS_OUTBOX));
            EmailVO vo = new EmailVO();
            vo.setEmailId(inbox.getEmailId());
            vo.setInboxId(inbox.getId());
            vo.setOriginalTo(outbox.getSender());
            vo.setStatus(inbox.getStatus());
            List<String> recipientListForReply = new ArrayList<>();
            recipientListForReply.add(outbox.getSender());
            for (EmailInbox inbox1 : recipientsInboxes) {
                recipientListForReply.add(inbox1.getRecipient());
            }
            vo.setRecipientListForReply(recipientListForReply);
            UserAccount from = userAccountService.getUserByLoginId(outbox.getSender());
            if (from != null) {
                vo.setSentFrom("发件人：" + from.getName() + " (" + from.getLoginId() + ")");
            }
            List<Recipient> recipientList = objectMapper.readValue(outbox.getRecipient(),
                     TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Recipient.class));

            vo.setSentTo("收件人：" + formatRecipients(recipientList, AppConsts.EMAIL_RECIPIENT_RECIPIENT));
            vo.setCc("抄送：" + formatRecipients(recipientList, AppConsts.EMAIL_RECIPIENT_CC));
            vo.setCreatedAt("发送时间：" + DateUtils.getDateString(outbox.getCreatedAt(), DateUtils.STD_FORMAT_2));

            vo.setIsHasAttachment(email.getIsHasAttachment());
            vo.setSubject(email.getSubject());
            vo.setContentString(new String(email.getContent(), StandardCharsets.UTF_8));

            List<EmailAttachmentVO> attachmentVOS = emailDAO.retrieveEmailAttachmentByEmailId(email.getId());
            for (EmailAttachmentVO att : attachmentVOS) {
                att.setIconClass(ContentConsts.getIconClass(att.getExtension()));
                att.setSizeString(ContentConsts.getSize(att.getSize()));
            }
            vo.setAttachmentVOS(attachmentVOS);

            List<EmailReplyInbox> replyInboxList = emailDAO.retrieveReplyInboxByEmailId(email.getId());
            //replies to me
            replyInboxList.removeIf(var -> !context.getLoginId().equals(var.getRecipient()));

            vo.setReplyVOList(new ArrayList<>());
            for (EmailReplyInbox replyInbox : replyInboxList) {
                EmailReply reply = emailDAO.retrieveEmailReplyById(replyInbox.getEmailReplyId());
                EmailReplyOutbox replyOutbox = emailDAO.retrieveReplyOutboxByReplyId(reply.getId());
                EmailReplyVO replyVO = new EmailReplyVO();
                replyVO.setSender(userAccountService.getUserByLoginId(replyOutbox.getSender()).getName()
                        + " (" + replyOutbox.getSender() + ")");
                replyVO.setContent(new String(reply.getContent(), StandardCharsets.UTF_8));
                replyVO.setCreatedAt(DateUtils.getDateString(replyInbox.getCreatedAt(), DateUtils.STD_FORMAT_2));
                replyVO.setIsHasAttachment(reply.getIsHasAttachment());

                List<Recipient> recipientsUnderReplyList = objectMapper.readValue(replyOutbox.getRecipient(),
                        TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Recipient.class));
                replyVO.setRecipients("收件人：" + formatRecipients(recipientsUnderReplyList, AppConsts.EMAIL_RECIPIENT_RECIPIENT));

                List<EmailAttachmentVO> replyAttachments = emailDAO.retrieveReplyAttachmentByReplyId(reply.getId());
                for (EmailAttachmentVO att : replyAttachments) {
                    att.setIconClass(ContentConsts.getIconClass(att.getExtension()));
                    att.setSizeString(ContentConsts.getSize(att.getSize()));
                }
                replyVO.setAttachments(replyAttachments);
                vo.getReplyVOList().add(replyVO);
                replyInbox.setIsRead(AppConsts.YES);
                JPAExecutor.update(replyInbox);
            }

            inbox.setIsRead(AppConsts.YES);
            JPAExecutor.update(inbox);
            log.info("Read Email Time Cost -> " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");
            return vo;
        }
        return null;
    }

    public EmailVO readOutbox(Long outboxId) throws Exception {
        EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(outboxId);
        if (outbox != null) {
            AppContext context = AppContext.getFromWebThread();
            //if the email I sent has reply(inbox) to me, show like inbox do
            List<EmailInbox> emailInboxList = emailDAO.retrieveAllRecipientsInbox(outbox.getEmailId());
            emailInboxList.sort(Comparator.comparing(EmailInbox::getCreatedAt, Comparator.reverseOrder()));
            for (EmailInbox emailInbox : emailInboxList) {
                if (emailInbox.getRecipient().equals(context.getLoginId())
                        && emailInbox.getRecipientType().equals(AppConsts.EMAIL_RECIPIENT_SENDER)) {
                    return readEmail(emailInbox.getId());
                }
            }
        }
        return null;
    }

    public EmailVO readDraft(Long outboxId) throws Exception {
        EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(outboxId);
        if (outbox != null) {
            EmailVO vo = new EmailVO();
            Email email = emailDAO.retrieveEmailById(outbox.getEmailId());
            vo.setOutboxId(outbox.getId());
            vo.setSubject(email.getSubject());
            vo.setContentString(new String(email.getContent(), StandardCharsets.UTF_8));
            vo.setIsHasAttachment(email.getIsHasAttachment());
            List<Recipient> recipientList = new ObjectMapper().readValue(outbox.getRecipient(),
                    TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Recipient.class));
            StringBuilder sendTo = new StringBuilder();
            StringBuilder cc = new StringBuilder();
            for (Recipient recipient : recipientList) {
                if (AppConsts.EMAIL_RECIPIENT_RECIPIENT.equals(recipient.getRecipientType())) {
                    sendTo.append(recipient.getLoginId()).append(";");
                } else {
                    cc.append(recipient.getLoginId()).append(";");
                }
            }
            if (sendTo.length() > 0) {
                sendTo.delete(sendTo.lastIndexOf(";"), sendTo.length());
            }
            if (cc.length() > 0) {
                cc.delete(cc.lastIndexOf(";"), cc.length());
            }
            vo.setSentTo(sendTo.toString());
            vo.setCc(cc.toString());
            List<EmailAttachmentVO> attachmentVOS = emailDAO.retrieveEmailAttachmentByEmailId(email.getId());
            for (EmailAttachmentVO att : attachmentVOS) {
                att.setIconClass(ContentConsts.getIconClass(att.getExtension()));
                att.setSizeString(ContentConsts.getSize(att.getSize()));
            }
            vo.setAttachmentVOS(attachmentVOS);
            return vo;
        }
        return null;
    }

    @Transactional
    public void reply(EmailDto dto, Long emailId) throws Exception {
        if (dto != null) {
            AppContext context = AppContext.getFromWebThread();
            EmailReply reply = saveReply(dto, emailId, false);
            List<EmailInbox> recipientsInboxes = emailDAO.retrieveAllRecipientsInbox(reply.getEmailId());
            String[] recipients = trim(dto.getSentTo()).split(";");
            List<String> recipientList = Arrays.asList(recipients);
            for (EmailInbox inbox : recipientsInboxes) {
                if (recipientList.contains(inbox.getRecipient())) {
                    inbox.setStatus(AppConsts.EMAIL_STATUS_INBOX);
                    JPAExecutor.update(inbox);
                }
            }
            //reply myself if sent from me
            if (!recipientList.contains(context.getLoginId())) {
                EmailReplyInbox replyInbox = new EmailReplyInbox();
                replyInbox.setEmailReplyId(reply.getId());
                replyInbox.setRecipient(context.getLoginId());
                replyInbox.setRecipientType(AppConsts.EMAIL_RECIPIENT_SENDER);
                replyInbox.setStatus(AppConsts.EMAIL_STATUS_OUTBOX);
                replyInbox.setIsRead(AppConsts.NO);
                JPAExecutor.save(replyInbox);
            }
            for (String replyTo : recipientList) {
                EmailReplyInbox replyInbox = new EmailReplyInbox();
                replyInbox.setEmailReplyId(reply.getId());
                replyInbox.setRecipient(replyTo);
                replyInbox.setRecipientType(AppConsts.EMAIL_RECIPIENT_RECIPIENT);
                replyInbox.setStatus(AppConsts.EMAIL_STATUS_INBOX);
                replyInbox.setIsRead(AppConsts.NO);
                JPAExecutor.save(replyInbox);
            }
        }
    }

    @Transactional
    public void saveEmailDraft(EmailDto dto) throws Exception {
        if (StringUtils.isBlank(dto.getOutboxId())) {
            saveEmail(dto, true);
        } else {
            EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(StringUtils.parseIfIsLong(dto.getOutboxId()));
            if (outbox != null) {
                Email email = emailDAO.retrieveEmailById(outbox.getEmailId());
                email.setSubject(dto.getSubject());
                email.setContent(dto.getContent().getBytes(StandardCharsets.UTF_8));
                if (!CollectionUtils.isEmpty(dto.getAttachments())) {
                    email.setIsHasAttachment(AppConsts.YES);
                } else {
                    email.setIsHasAttachment(AppConsts.NO);
                }
                JPAExecutor.update(email);
                String[] sendTos = trim(dto.getSentTo()).split(";");
                String[] ccs = trim(dto.getCc()).split(";");
                outbox.setRecipient(getRecipientsJson(sendTos, ccs));
                JPAExecutor.update(outbox);
                saveEmailAttachments(dto.getAttachments(), email);
            }
        }
    }

    @Transactional
    public void deleteAttachmentInDraft(Long outboxId, Long attachmentId) {
        EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(outboxId);
        if (outbox != null) {
            Email email = emailDAO.retrieveEmailById(outbox.getEmailId());
            AppContext context = AppContext.getFromWebThread();
            List<EmailAttachmentVO> attachments = emailDAO.retrieveEmailAttachmentByEmailId(email.getId());
            if (AppConsts.YES.equals(email.getIsHasAttachment()) && !attachments.isEmpty()) {
                EmailAttachment attachment = emailDAO.getEmailAttachmentById(attachmentId);
                FileNet fileNet = fileNetService.getFileNetById(attachment.getFileNetId());
                JPAExecutor.delete(fileNet);
                JPAExecutor.delete(attachment);
                deleteFile(fileNet);
                if (attachments.size() - 1 == 0) {
                    email.setIsHasAttachment(AppConsts.NO);
                }
            }
            JPAExecutor.update(email);
            JPAExecutor.update(outbox);
        }
    }

    @Transactional
    public void sendDraft(EmailDto dto) throws Exception {
        if (dto != null && StringUtils.isNotBlank(dto.getOutboxId())) {
            saveEmailDraft(dto);
            EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(StringUtils.parseIfIsLong(dto.getOutboxId()));
            if (outbox != null) {
                Email email = emailDAO.retrieveEmailById(outbox.getEmailId());
                String[] sendTos = trim(dto.getSentTo()).split(";");
                String[] ccs = trim(dto.getCc()).split(";");
                sendToInbox(email, sendTos, ccs);
                outbox.setStatus(AppConsts.EMAIL_STATUS_SENT);
                JPAExecutor.update(outbox);
            }
        }
    }

    @Transactional
    public void deleteDraft(Long outboxId) {
        EmailOutbox outbox = emailDAO.retrieveEmailOutboxById(outboxId);
        if (outbox != null) {
            Email email = emailDAO.retrieveEmailById(outbox.getEmailId());
            List<EmailAttachmentVO> attachments = emailDAO.retrieveEmailAttachmentByEmailId(email.getId());
            for (EmailAttachmentVO attachment : attachments) {
                EmailAttachment att = emailDAO.getEmailAttachmentById(attachment.getId());
                FileNet fileNet = fileNetService.getFileNetById(att.getFileNetId());
                JPAExecutor.delete(fileNet);
                JPAExecutor.delete(att);
                deleteFile(fileNet);
            }
            JPAExecutor.delete(outbox);
            JPAExecutor.delete(email);
        }
    }

    public void moveToRecycleBin(Long inboxId) {
        EmailInbox inbox = emailDAO.retrieveEmailInboxById(inboxId);
        if (inbox != null) {
            inbox.setStatus(AppConsts.EMAIL_STATUS_DELETED);
            if (AppConsts.EMAIL_RECIPIENT_SENDER.equals(inbox.getRecipientType())) {
                EmailOutbox outbox = emailDAO.retrieveEmailOutboxByEmailId(inbox.getEmailId());
                outbox.setStatus(AppConsts.EMAIL_STATUS_DELETED);
                JPAExecutor.update(outbox);
            }
            JPAExecutor.update(inbox);
        }
    }

    public void recover(Long inboxId) {
        EmailInbox inbox = emailDAO.retrieveEmailInboxById(inboxId);
        if (inbox != null) {
            AppContext context = AppContext.getFromWebThread();
            List<EmailReplyInbox> replyInboxList = emailDAO.retrieveReplyInboxByEmailId(inbox.getEmailId());
            replyInboxList.removeIf(var -> !context.getLoginId().equals(var.getRecipient()));
            if (replyInboxList.isEmpty()) {
                inbox.setStatus(AppConsts.EMAIL_STATUS_OUTBOX);
            } else {
                inbox.setStatus(AppConsts.EMAIL_STATUS_INBOX);
            }
            if (AppConsts.EMAIL_RECIPIENT_SENDER.equals(inbox.getRecipientType())) {
                EmailOutbox outbox = emailDAO.retrieveEmailOutboxByEmailId(inbox.getEmailId());
                outbox.setStatus(AppConsts.EMAIL_STATUS_SENT);
                JPAExecutor.update(outbox);
            }
            JPAExecutor.update(inbox);
        }
    }

    private String formatLength(String value, int length) {
        if (StringUtils.isNotBlank(value) && value.length() > length) {
            return StringEscapeUtils.escapeHtml4(value.substring(0, length + 1) + "...");
        }
        return value;
    }

    private String formatRecipients(List<Recipient> recipients, String type) {
        StringBuilder builder = new StringBuilder();
        for (Recipient recipient : recipients) {
            if (recipient.getRecipientType().equals(type)) {
                builder.append(recipient.getName()).append(" (").append(recipient.getLoginId()).append(")").append("; ");
            }
        }
        if (builder.length() > 0) {
            builder.delete(builder.lastIndexOf("; "), builder.length());
        }
        return builder.toString();
    }

    private void setReplyPeriod(Date replyDate, EmailVO emailVO) {
        String period = DateUtils.getPeriod(replyDate);
        if (period == null) {
            period = "0s";
        }
        emailVO.setLastReplyOn(replyDate);
        String periodNumber = period.substring(0, period.length() - 1);
        String unit = period.substring(period.length() - 1);
        emailVO.setLastReplyAt(periodNumber + DateUtils.getTimeUnit(unit) + "前");
    }

    private Email saveEmail(EmailDto dto, boolean isDraft) throws Exception {
        AppContext context = AppContext.getFromWebThread();
        Email email= new Email();
        EmailOutbox outbox = new EmailOutbox();
        outbox.setSender(context.getLoginId());
        String[] sendTos = trim(dto.getSentTo()).split(";");
        String[] ccs = trim(dto.getCc()).split(";");
        outbox.setRecipient(getRecipientsJson(sendTos, ccs));
        if (isDraft) {
            outbox.setStatus(AppConsts.EMAIL_STATUS_DRAFT);
        } else {
            outbox.setStatus(AppConsts.EMAIL_STATUS_SENT);
        }
        email.setSubject(StringUtils.isBlank(dto.getSubject()) ? "（无标题）" : dto.getSubject());
        email.setContent(dto.getContent().getBytes(StandardCharsets.UTF_8));
        email.createAuditValues(AppContext.getFromWebThread());
        if (!CollectionUtils.isEmpty(dto.getAttachments())) {
            email.setIsHasAttachment(AppConsts.YES);
        } else {
            email.setIsHasAttachment(AppConsts.NO);
        }
        emailDAO.createEmail(email);
        outbox.setEmailId(email.getId());
        JPAExecutor.save(outbox);
        saveEmailAttachments(dto.getAttachments(), email);
        return email;
    }

    private EmailReply saveReply(EmailDto dto, Long emailId, boolean isDraft) throws Exception {
        Email email = emailDAO.retrieveEmailById(emailId);
        AppContext context = AppContext.getFromWebThread();
        EmailReply reply = new EmailReply();
        if (email != null) {
            reply.setEmailId(email.getId());
            reply.setContent(dto.getContent().getBytes(StandardCharsets.UTF_8));
            if (!CollectionUtils.isEmpty(dto.getAttachments())) {
                reply.setIsHasAttachment(AppConsts.YES);
            } else {
                reply.setIsHasAttachment(AppConsts.NO);
            }
            reply.createAuditValues(context);
            emailDAO.createReply(reply);
            EmailReplyOutbox replyOutbox = new EmailReplyOutbox();
            replyOutbox.setEmailReplyId(reply.getId());
            replyOutbox.setSender(context.getLoginId());
            String[] sendTos = trim(dto.getSentTo()).split(";");
            replyOutbox.setRecipient(getRecipientsJson(sendTos, null));
            if (isDraft) {
                replyOutbox.setStatus(AppConsts.EMAIL_STATUS_DRAFT);
            } else {
                replyOutbox.setStatus(AppConsts.EMAIL_STATUS_SENT);
            }
            JPAExecutor.save(replyOutbox);
            if (dto.getAttachments() != null) {
                for (MultipartFile file : dto.getAttachments()) {
                    FileNet fileNet = fileNetService.saveFileNetLocal(file.getOriginalFilename(),
                            file.getBytes(), SystemConsts.ROOT_EMAIL_PATH + context.getLoginId());
                    if (fileNet != null) {
                        EmailReplyAttachment attachment = new EmailReplyAttachment();
                        attachment.setEmailReplyId(reply.getId());
                        attachment.setFileNetId(fileNet.getId());
                        attachment.setFileName(file.getOriginalFilename());
                        JPAExecutor.save(attachment);
                    }
                }
            }
        }
        return reply;
    }

    private String getRecipientsJson(String[] sendTos, String[] ccs) throws Exception {
        List<Recipient> recipientList = new ArrayList<>();
        for (String sendTo : sendTos) {
            Recipient recipient = new Recipient();
            recipient.setRecipientType(AppConsts.EMAIL_RECIPIENT_RECIPIENT);
            recipient.setLoginId(sendTo);
            UserAccount user = userAccountService.getUserByLoginId(sendTo);
            recipient.setUserId(user.getId());
            recipient.setName(user.getName());
            recipientList.add(recipient);
        }
        if (ccs != null) {
            for (String sendTo : ccs) {
                if (StringUtils.isNotBlank(sendTo)) {
                    Recipient recipient = new Recipient();
                    recipient.setRecipientType(AppConsts.EMAIL_RECIPIENT_CC);
                    recipient.setLoginId(sendTo);
                    UserAccount user = userAccountService.getUserByLoginId(sendTo);
                    recipient.setUserId(user.getId());
                    recipient.setName(user.getName());
                    recipientList.add(recipient);
                }
            }
        }
        return new ObjectMapper().writeValueAsString(recipientList);
    }

    private void saveEmailAttachments(List<MultipartFile> attachments, Email email) throws Exception {
        if (attachments != null && email != null) {
            AppContext context = AppContext.getFromWebThread();
            for (MultipartFile file : attachments) {
                FileNet fileNet = fileNetService.saveFileNetLocal(file.getOriginalFilename(),
                        file.getBytes(), SystemConsts.ROOT_EMAIL_PATH + context.getLoginId());
                if (fileNet != null) {
                    EmailAttachment attachment = new EmailAttachment();
                    attachment.setEmailId(email.getId());
                    attachment.setFileNetId(fileNet.getId());
                    attachment.setFileName(file.getOriginalFilename());
                    JPAExecutor.save(attachment);
                }
            }
        }
    }

    private void sendToInbox(Email email, String[] sendTos, String[] ccs) {
        AppContext context = AppContext.getFromWebThread();
        EmailInbox senderInbox = new EmailInbox();
        senderInbox.setEmailId(email.getId());
        senderInbox.setIsRead(AppConsts.NO);
        senderInbox.setRecipient(context.getLoginId());
        senderInbox.setRecipientType(AppConsts.EMAIL_RECIPIENT_SENDER);
        senderInbox.setStatus(AppConsts.EMAIL_STATUS_OUTBOX);
        JPAExecutor.save(senderInbox);
        for (String sendTo : sendTos) {
            EmailInbox inbox = new EmailInbox();
            inbox.setEmailId(email.getId());
            inbox.setIsRead(AppConsts.NO);
            inbox.setRecipient(sendTo);
            inbox.setRecipientType(AppConsts.EMAIL_RECIPIENT_RECIPIENT);
            inbox.setStatus(AppConsts.EMAIL_STATUS_INBOX);
            JPAExecutor.save(inbox);
        }
        for (String cc : ccs) {
            if (StringUtils.isBlank(cc)) {
                continue;
            }
            EmailInbox inbox = new EmailInbox();
            inbox.setEmailId(email.getId());
            inbox.setIsRead(AppConsts.NO);
            inbox.setRecipient(cc);
            inbox.setRecipientType(AppConsts.EMAIL_RECIPIENT_CC);
            inbox.setStatus(AppConsts.EMAIL_STATUS_INBOX);
            JPAExecutor.save(inbox);
        }
    }

    private void deleteFile(FileNet fileNet) {
        AppContext context = AppContext.getFromWebThread();
        File file = new File(SystemConsts.ROOT_EMAIL_PATH + context.getLoginId(), fileNet.getFileUuid());
        if (file.isFile() && file.exists()) {
            if (file.delete()) {
                log.info("File: [" + fileNet.getFileUuid() + "] DELETED FROM DISK");
            } else {
                log.info("Delete FAILED");
            }
        } else {
            log.warn("File: [" + fileNet.getFileUuid() + "] NOT Found");
        }
    }

    public static String trim(String receivers) {
        if (StringUtils.isNotBlank(receivers)) {
            String[] rec = receivers.split(";");
            StringBuilder builder = new StringBuilder();
            for (String var : rec) {
                if (StringUtils.isNotBlank(var)) {
                    builder.append(var.trim()).append(";");
                }
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.lastIndexOf(";"));
            }
            return builder.toString();
        }
        return receivers;
    }
}
