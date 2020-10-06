package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "GNL_EMAIL_REPLY_OUTBOX")
public class EmailReplyOutbox extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long emailReplyId;
    private String sender;
    private String status;
    private String recipient;

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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
