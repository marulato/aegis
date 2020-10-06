package org.legion.aegis.general.vo;

import org.legion.aegis.common.base.BaseVO;
import java.util.Date;
import java.util.List;

public class EmailVO extends BaseVO {

    private Long emailId;
    private Long inboxId;
    private Long outboxId;
    private String sentFrom;
    private String cc;
    private String sentTo;
    private String subject;
    private byte[] content;
    private String lastReplyAt;
    private String isHasAttachment;
    private long labelId;
    private String labelName;
    private String color;
    private String contentString;
    private String status;
    private String isRead;

    private Date lastReplyOn;
    private List<EmailAttachmentVO> attachmentVOS;

    private String originalTo;
    private String originalCc;

    private List<String> recipientListForReply;

    private String isReplyRead;
    private List<EmailReplyVO> replyVOList;

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public Long getInboxId() {
        return inboxId;
    }

    public void setInboxId(Long inboxId) {
        this.inboxId = inboxId;
    }

    public Long getOutboxId() {
        return outboxId;
    }

    public void setOutboxId(Long outboxId) {
        this.outboxId = outboxId;
    }

    public String getSentFrom() {
        return sentFrom;
    }

    public void setSentFrom(String sentFrom) {
        this.sentFrom = sentFrom;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getSentTo() {
        return sentTo;
    }

    public void setSentTo(String sentTo) {
        this.sentTo = sentTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getLastReplyAt() {
        return lastReplyAt;
    }

    public void setLastReplyAt(String lastReplyAt) {
        this.lastReplyAt = lastReplyAt;
    }

    public String getIsHasAttachment() {
        return isHasAttachment;
    }

    public void setIsHasAttachment(String isHasAttachment) {
        this.isHasAttachment = isHasAttachment;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getContentString() {
        return contentString;
    }

    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public Date getLastReplyOn() {
        return lastReplyOn;
    }

    public void setLastReplyOn(Date lastReplyOn) {
        this.lastReplyOn = lastReplyOn;
    }

    public List<EmailAttachmentVO> getAttachmentVOS() {
        return attachmentVOS;
    }

    public void setAttachmentVOS(List<EmailAttachmentVO> attachmentVOS) {
        this.attachmentVOS = attachmentVOS;
    }

    public String getOriginalTo() {
        return originalTo;
    }

    public void setOriginalTo(String originalTo) {
        this.originalTo = originalTo;
    }

    public String getOriginalCc() {
        return originalCc;
    }

    public void setOriginalCc(String originalCc) {
        this.originalCc = originalCc;
    }

    public String getIsReplyRead() {
        return isReplyRead;
    }

    public void setIsReplyRead(String isReplyRead) {
        this.isReplyRead = isReplyRead;
    }

    public List<EmailReplyVO> getReplyVOList() {
        return replyVOList;
    }

    public void setReplyVOList(List<EmailReplyVO> replyVOList) {
        this.replyVOList = replyVOList;
    }

    public List<String> getRecipientListForReply() {
        return recipientListForReply;
    }

    public void setRecipientListForReply(List<String> recipientListForReply) {
        this.recipientListForReply = recipientListForReply;
    }
}
