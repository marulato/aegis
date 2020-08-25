package org.legion.aegis.admin.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

import java.util.Date;

@Entity(tableName = "PJT_PROJECT")
public class Project extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private String name;
    private String type;
    private String typeOther;
    private Date reqStartDate;
    private Date reqEndDate;
    private Date devStartDate;
    private Date devEndDate;
    private Date sitStartDate;
    private Date sitEndDate;
    private Date uatStartDate;
    private Date uatEndDate;
    private String description;
    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeOther() {
        return typeOther;
    }

    public void setTypeOther(String typeOther) {
        this.typeOther = typeOther;
    }

    public Date getReqStartDate() {
        return reqStartDate;
    }

    public void setReqStartDate(Date reqStartDate) {
        this.reqStartDate = reqStartDate;
    }

    public Date getReqEndDate() {
        return reqEndDate;
    }

    public void setReqEndDate(Date reqEndDate) {
        this.reqEndDate = reqEndDate;
    }

    public Date getDevStartDate() {
        return devStartDate;
    }

    public void setDevStartDate(Date devStartDate) {
        this.devStartDate = devStartDate;
    }

    public Date getDevEndDate() {
        return devEndDate;
    }

    public void setDevEndDate(Date devEndDate) {
        this.devEndDate = devEndDate;
    }

    public Date getSitStartDate() {
        return sitStartDate;
    }

    public void setSitStartDate(Date sitStartDate) {
        this.sitStartDate = sitStartDate;
    }

    public Date getSitEndDate() {
        return sitEndDate;
    }

    public void setSitEndDate(Date sitEndDate) {
        this.sitEndDate = sitEndDate;
    }

    public Date getUatStartDate() {
        return uatStartDate;
    }

    public void setUatStartDate(Date uatStartDate) {
        this.uatStartDate = uatStartDate;
    }

    public Date getUatEndDate() {
        return uatEndDate;
    }

    public void setUatEndDate(Date uatEndDate) {
        this.uatEndDate = uatEndDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
