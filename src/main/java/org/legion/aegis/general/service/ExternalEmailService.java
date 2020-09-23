package org.legion.aegis.general.service;

import com.google.common.base.Stopwatch;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.*;
import org.legion.aegis.general.dao.DocumentDAO;
import org.legion.aegis.general.entity.EmailArchive;
import org.legion.aegis.general.entity.EmailEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ExternalEmailService {

    private final JavaMailSenderImpl mailSender;

    private static final Logger log = LoggerFactory.getLogger(ExternalEmailService.class);

    @Autowired
    public ExternalEmailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public static String getRecipients(String[] address) {
        StringBuilder recipients = new StringBuilder();
        if (address != null) {
            for (String to : address) {
                recipients.append(to).append(";");
            }
            if (recipients.length() > 0) {
                recipients.deleteCharAt(recipients.lastIndexOf(";"));
            }
        }
        return recipients.toString();
    }


    public void sendEmail(String sentFrom, String[] sentTo, String[] cc, String subject,
                          String content, String attachFileName, byte[] attachment) throws Exception {
        if (sentTo == null || sentTo.length == 0 ||
                (StringUtils.isEmpty(subject) && StringUtils.isEmpty(content) && attachment == null)) {
            return;
        }
        AppContext context = AppContext.getFromWebThread();
        String isEmailEnabled = ConfigUtils.get("server.smtp.enabled");
        if (!StringUtils.parseBoolean(isEmailEnabled)) {
            archiveEmail(StringUtils.isBlank(sentFrom) ? ConfigUtils.get("legion.server.mail.host") : sentFrom,
                    sentTo, cc, subject, content, attachment, AppConsts.EMAIL_STATUS_NOT_SENT, context);
            return;
        }
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            if (StringUtils.isNotBlank(sentFrom)) {
                mimeMessageHelper.setFrom(sentFrom);
            } else {
                sentFrom = ConfigUtils.get("legion.server.mail.host");
                mimeMessageHelper.setFrom(sentFrom);
            }
            List<String> sentToList = Arrays.asList(sentTo);
            validateEmail(sentToList);
            mimeMessageHelper.setTo(sentToList.toArray(new String[0]));
            if (cc != null && cc.length > 0) {
                List<String> ccList = Arrays.asList(cc);
                validateEmail(ccList);
                mimeMessageHelper.setCc(ccList.toArray(new String[0]));
            }
            if (StringUtils.isNotBlank(subject)) {
                mimeMessageHelper.setSubject(subject);
            } else {
                mimeMessageHelper.setSubject("(No Subject)");
            }
            if (StringUtils.isNotBlank(content)) {
                mimeMessageHelper.setText(content, true);
            } else {
                mimeMessageHelper.setText("(No Content)");
            }
            if (attachment != null && attachment.length > 0) {
                mimeMessageHelper.addAttachment(StringUtils.isNotBlank(attachFileName) ?
                        attachFileName : "attachment", new ByteArrayResource(attachment));
            }
            //mailSender.send(mimeMessage);
            new Thread(new EmailThread(mimeMessage, sentFrom, sentTo, cc, subject, content, attachment, context)).start();
        } catch (Exception e) {
            log.error("Email sent FAILED", e);
            //throw e; DO NOT throw it, Email Failure should not trigger rollback
        }

    }

    public void sendEmail(String[] sentTo, String[] cc, String subject, String content) throws Exception {
        sendEmail(ConfigUtils.get("legion.server.mail.host"), sentTo, cc, subject, content, null, null);
    }

    public void sendEmail(EmailEntity emailEntity) throws Exception {
        if (emailEntity != null) {
            String[] ccTo = null;
            String[] sentTo = emailEntity.getSentTo().split(";");
            if (StringUtils.isNotBlank(emailEntity.getCc())) {
                ccTo = emailEntity.getCc().split(";");
            }
            sendEmail(emailEntity.getSentFrom(), sentTo, ccTo, emailEntity.getSubject(),
                    new String(emailEntity.getContent(), StandardCharsets.UTF_8), emailEntity.getAttachFileName(), emailEntity.getAttachment());
        }
    }

    private void validateEmail(List<String> emailList) {
        Iterator<String> iterator = emailList.iterator();
        while (iterator.hasNext()) {
            String email = iterator.next();
            if (!ValidationUtils.isValidEmail(email)) {
                iterator.remove();
                log.warn("Invalid email address [" + email + "], will NOT send email to this address.");
            }
        }
    }

    private void archiveEmail(String sentFrom, String[] sentTo, String[] cc, String subject,
                              String content, byte[] attachment, String status, AppContext context) {
        EmailArchive emailArchive = new EmailArchive();
        emailArchive.setSubject(subject);
        emailArchive.setStatus(status);
        emailArchive.setAttachment(attachment);
        emailArchive.setSentFrom(sentFrom);
        emailArchive.setContent(content.getBytes(StandardCharsets.UTF_8));
        emailArchive.setSentTo(ArrayUtils.toString(sentTo, ";"));
        emailArchive.setCc(ArrayUtils.toString(cc, ";"));
        emailArchive.createAuditValues(context);
        SpringUtils.getBean(DocumentDAO.class).createEmailArchive(emailArchive);
    }

    private class EmailThread implements Runnable {

        private final MimeMessage mimeMessage;
        private final String sentFrom;
        private final String[] sentTo;
        private final String[] cc;
        private final String subject;
        private final String content;
        private final byte[] attachment;
        private final AppContext context;

        EmailThread(MimeMessage mimeMessage, String sentFrom, String[] sentTo, String[] cc, String subject,
                    String content, byte[] attachment, AppContext context) {
            this.mimeMessage = mimeMessage;
            this.sentFrom = sentFrom;
            this.sentTo = sentTo;
            this.cc = cc;
            this.subject = subject;
            this.content = content;
            this.attachment = attachment;
            this.context = context;
        }

        @Override
        public void run() {
            Stopwatch stopwatch = Stopwatch.createStarted();
            String status = null;
            try {
                mailSender.send(mimeMessage);
                status = AppConsts.EMAIL_STATUS_SENT;
            } catch (Exception e) {
                log.error("Email Sent FAILED", e);
                status = AppConsts.EMAIL_STATUS_SENT_FAILED;
            } finally {
                archiveEmail(sentFrom, sentTo, cc, subject, content, attachment, status, context);
                log.info("Send Email Time Cost: " + stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) + " ms");
            }
        }
    }

}
