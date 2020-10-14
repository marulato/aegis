package org.legion.aegis.general.dto;

import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.general.controller.EmailBoxController;
import org.legion.aegis.general.service.EmailService;
import org.legion.aegis.general.vo.EmailVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class EmailDto extends BaseDto {

    private String sentFrom;
    @NotBlank(message = "收件人不能为空")
    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validateRecipients", message = "收件人不存在", profile = {"send", "reply"}),
            @ValidateWithMethod(methodName = "validateReplyTo", message = "收件人与原始邮件不一致", profile = {"reply"})
    })
    private String sentTo;

    @ValidateWithMethod(methodName = "validateCc", message = "收件人不存在", profile = {"send"})
    private String cc;
    @Length(max = 100, message = "标题长度不能超过100个字符", profile = {"send", "reply"})
    private String subject;
    @NotBlank(message = "内容不能为空", profile = {"send", "reply"})
    private String content;
    private List<MultipartFile> attachments;

    private String outboxId;

    private boolean validateRecipients(String recipients) {
        String afterTrim = EmailService.trim(recipients);
        if (StringUtils.isNotBlank(afterTrim)) {
            String[] receivers = afterTrim.split(";");
            UserAccountService service = SpringUtils.getBean(UserAccountService.class);
            for (String r : receivers) {
                if (service.getUserByLoginId(r.trim()) == null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean validateCc(String cc) {
        if (StringUtils.isBlank(cc)) {
            return true;
        } else {
            return validateRecipients(cc);
        }
    }

    private boolean validateReplyTo(String sentTo) {
        if (StringUtils.isNotBlank(sentTo)) {
            EmailVO emailVO = (EmailVO) SessionManager.getAttribute(EmailBoxController.SESSION_KEY);
            String[] to = sentTo.split(";");
            for (String t : to) {
                if (!emailVO.getRecipientListForReply().contains(t)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }

    public String getOutboxId() {
        return outboxId;
    }

    public void setOutboxId(String outboxId) {
        this.outboxId = outboxId;
    }
}
