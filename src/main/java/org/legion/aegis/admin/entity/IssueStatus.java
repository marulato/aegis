package org.legion.aegis.admin.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "CM_ISSUE_STATUS")
public class IssueStatus extends BasePO {

    @PrimaryKey(autoIncrement = false)
    private String statusCode;
    private String displayName;
    private String description;
    private String color;
    private String isSystem;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }
}
