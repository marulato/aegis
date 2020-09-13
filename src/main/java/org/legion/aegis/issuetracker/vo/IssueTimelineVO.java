package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;
import org.legion.aegis.common.utils.StringUtils;

import java.util.Date;

public class IssueTimelineVO extends BaseVO {

    private String oldValue;
    private String newValue;
    private boolean isAdded;
    private boolean isDeleted;
    private boolean isUpdated;
    private String by;
    private String at;
    private String desc;
    private String type;
    private Date date;

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        if (StringUtils.isBlank(oldValue)) {
            this.oldValue = "[无内容]";
        } else {
            this.oldValue = oldValue;
        }
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        if (StringUtils.isBlank(newValue)) {
            this.newValue = "[无内容]";
        } else {
            this.newValue = newValue;
        }
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
        this.desc = "添加";
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        this.desc = "删除";
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
        this.desc = "更新";
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getAt() {
        return at;
    }

    public void setAt(String at) {
        this.at = at;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
