package org.legion.aegis.general.service;

import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.ArrayUtils;
import org.legion.aegis.common.utils.ConfigUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
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
        String isEmailEnabled = ConfigUtils.get("server.smtp.enabled");
        if (!StringUtils.parseBoolean(isEmailEnabled)) {
            return;
        }
        EmailArchive emailArchive = new EmailArchive();
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            if (StringUtils.isNotBlank(sentFrom)) {
                mimeMessageHelper.setFrom(sentFrom);
            } else {
                mimeMessageHelper.setFrom(ConfigUtils.get("legion.server.mail.host"));
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
            mailSender.send(mimeMessage);
            emailArchive.setStatus(AppConsts.EMIAL_STATUS_SENT);
        } catch (Exception e) {
            log.error("Email sent FAILED", e);
            emailArchive.setStatus(AppConsts.EMIAL_STATUS_SENT_FAILED);
            //throw e; DO NOT throw it, Email Failure should not trigger rollback
        } finally {
            emailArchive.setSubject(subject);
            emailArchive.setAttachment(attachment);
            emailArchive.setSentFrom(sentFrom);
            emailArchive.setContent(content.getBytes(StandardCharsets.UTF_8));
            emailArchive.setSentTo(ArrayUtils.toString(sentTo, ";"));
            emailArchive.setCc(ArrayUtils.toString(cc, ";"));
            JPAExecutor.save(emailArchive);
        }

    }

    public void sendEmail(String[] sentTo, String[] cc, String subject, String content) throws Exception {
        sendEmail(ConfigUtils.get("server.smtp.username"), sentTo, cc, subject, content, null, null);
    }

    public void sendEmail(EmailEntity emailEntity) throws Exception {
        if (emailEntity != null) {
            String[] sentTo = emailEntity.getSentTo().split(";");
            String[] ccTo = emailEntity.getCc().split(";");
            sendEmail(emailEntity.getSentFrom(), sentTo, ccTo, emailEntity.getSubject(),
                    new String(emailEntity.getContent()), emailEntity.getAttachFileName(), emailEntity.getAttachment());
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

}
