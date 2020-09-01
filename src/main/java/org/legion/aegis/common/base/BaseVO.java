package org.legion.aegis.common.base;

import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.DateUtils;

import java.lang.reflect.Field;
import java.util.Date;

public class BaseVO {

    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;

    public BaseVO() {}

    public  BaseVO(BasePO po) {
        if (po != null) {
            Class<?> poClass = po.getClass();
            Class<?> voClass = this.getClass();
            Class<?> basePOClass = poClass.getSuperclass();
            Class<?> baseVOClass = voClass.getSuperclass();
            Field[] poFields = poClass.getDeclaredFields();
            for (Field poField : poFields) {
                try {
                    Field voField = voClass.getDeclaredField(poField.getName());
                    BeanUtils.setValue(voField, voClass, this, BeanUtils.getValue(poField, poClass, po));
                } catch (Exception ignored) {

                }
            }
            Field[] auditFields = basePOClass.getDeclaredFields();
            for (Field auditField : auditFields) {
                try {
                    Field voAuditField = baseVOClass.getDeclaredField(auditField.getName());
                    BeanUtils.setValue(voAuditField, baseVOClass, this,
                            DateUtils.getDateString((Date) BeanUtils.getValue(auditField, basePOClass, po), "yyyy/MM/dd"));
                } catch (Exception ignored) {

                }
            }
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
