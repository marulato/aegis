package org.legion.aegis.general.vo;

import org.legion.aegis.common.base.BaseVO;
import java.util.List;

public class EmailReplyVO extends BaseVO {

    private String content;
    private Long emailReplyId;
    private String sender;
    private String recipients;
    private String isHasAttachment;
    private List<EmailAttachmentVO> attachments;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getEmailReplyId() {
        return emailReplyId;
    }

    public void setEmailReplyId(Long emailReplyId) {
        this.emailReplyId = emailReplyId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String getIsHasAttachment() {
        return isHasAttachment;
    }

    public void setIsHasAttachment(String isHasAttachment) {
        this.isHasAttachment = isHasAttachment;
    }

    public List<EmailAttachmentVO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<EmailAttachmentVO> attachments) {
        this.attachments = attachments;
    }
}
