package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "GNL_EMAIL_REPLY_INBOX")
public class EmailReplyInbox extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long emailReplyId;
    private String recipient;
    private String recipientType;
    private String isRead;
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailReplyInbox that = (EmailReplyInbox) o;
        return emailReplyId.equals(that.emailReplyId) &&
                recipient.equals(that.recipient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailReplyId, recipient);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmailReplyId() {
        return emailReplyId;
    }

    public void setEmailReplyId(Long emailReplyId) {
        this.emailReplyId = emailReplyId;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(String recipientType) {
        this.recipientType = recipientType;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
