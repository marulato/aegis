package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "GNL_EMAIL_REPLY")
public class EmailReply extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long emailId;
    private byte[] content;
    private String isHasAttachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getIsHasAttachment() {
        return isHasAttachment;
    }

    public void setIsHasAttachment(String isHasAttachment) {
        this.isHasAttachment = isHasAttachment;
    }
}
