package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "GNL_EMAIL_LABEL")
public class EmailLabel extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long userAcctId;
    private String name;
    private String color;
    private String description;
    private String attachCondition;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAcctId() {
        return userAcctId;
    }

    public void setUserAcctId(Long userAcctId) {
        this.userAcctId = userAcctId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachCondition() {
        return attachCondition;
    }

    public void setAttachCondition(String attachCondition) {
        this.attachCondition = attachCondition;
    }
}
