package org.legion.aegis.issuetracker.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "ISU_ATTACHMENT")
public class IssueAttachment extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long issueId;
    private String fileName;
    private Long fileNetId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
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
