package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "GNL_EMAIL_REPLY_ATTACHMENT")
public class EmailReplyAttachment extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long emailReplyId;
    private String fileName;
    private Long fileNetId;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileNetId() {
        return fileNetId;
    }

    public void setFileNetId(Long fileNetId) {
        this.fileNetId = fileNetId;
    }
}
