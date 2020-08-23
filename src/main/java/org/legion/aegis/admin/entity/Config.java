package org.legion.aegis.admin.entity;


import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "CM_CONFIG")
public class Config extends BasePO {

    @PrimaryKey(autoIncrement = false)
    private String configKey;
    @PrimaryKey(autoIncrement = false)
    private String configValue;
    private String type;
    private String profile;
    private String description;
    private String isSystem;
    private String isEditable;
    private String isNeedRestart;

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(String isSystem) {
        this.isSystem = isSystem;
    }

    public String getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(String isEditable) {
        this.isEditable = isEditable;
    }

    public String getIsNeedRestart() {
        return isNeedRestart;
    }

    public void setIsNeedRestart(String isNeedRestart) {
        this.isNeedRestart = isNeedRestart;
    }
}
