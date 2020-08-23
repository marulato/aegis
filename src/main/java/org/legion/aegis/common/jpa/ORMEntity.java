package org.legion.aegis.common.jpa;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Column;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.NotColumn;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ORMEntity {

    private String tableName;
    private String whereClause;
    private boolean auditColumns;
    private Map<String, String> fieldColumnMap;
    private Map<String, Boolean> primaryKeys;

    public static ORMEntity getInstance(Class<? extends BasePO> entityClass) {
        return new ORMEntity(entityClass);
    }


    public ORMEntity(Class<? extends BasePO> entityClass) {
        fieldColumnMap = new HashMap<>();
        primaryKeys = new HashMap<>();
        if (entityClass != null) {
            Field[] allFields = entityClass.getDeclaredFields();
            if (entityClass.isAnnotationPresent(Entity.class)) {
                Entity entity = entityClass.getAnnotation(Entity.class);
                tableName = entity.tableName().toUpperCase();
                if ("?".equals(tableName)) {
                    tableName = "{0}";
                }
                auditColumns = entity.auditColumns();
                whereClause = entity.whereClause();
                if (auditColumns) {
                    fieldColumnMap.put("createdAt", "CREATED_AT");
                    fieldColumnMap.put("createdBy", "CREATED_BY");
                    fieldColumnMap.put("updatedAt", "UPDATED_AT");
                    fieldColumnMap.put("updatedBy", "UPDATED_BY");
                }
                decompose(entityClass, fieldColumnMap, primaryKeys);
            }
        }
    }

    private void decompose(Class cls, Map<String, String> fieldColumnMap, Map<String, Boolean> primaryKeys) {
        Field[] allFields = cls.getDeclaredFields();
        boolean needPK = primaryKeys.isEmpty();
        for(Field field : allFields) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(NotColumn.class)) {
                if (field.isAnnotationPresent(Column.class)) {
                    Column columnAnno = field.getAnnotation(Column.class);
                    fieldColumnMap.put(field.getName(), columnAnno.columnName().toUpperCase());
                } else {
                    fieldColumnMap.put(field.getName(), NameConvertor.getColumn(field.getName()));
                }
            }
            if (field.isAnnotationPresent(PrimaryKey.class) && needPK) {
                PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                primaryKeys.put(field.getName(), primaryKey.autoIncrement());
            } else if (field.isAnnotationPresent(PrimaryKey.class) && !needPK){
                fieldColumnMap.remove(field.getName());
            }
        }
        if (cls.getSuperclass() != BasePO.class) {
            decompose(cls.getSuperclass(), fieldColumnMap, primaryKeys);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(String whereClause) {
        this.whereClause = whereClause;
    }

    public boolean isAuditColumns() {
        return auditColumns;
    }

    public void setAuditColumns(boolean auditColumns) {
        this.auditColumns = auditColumns;
    }

    public Map<String, String> getFieldColumnMap() {
        return fieldColumnMap;
    }

    public void setFieldColumnMap(Map<String, String> fieldColumnMap) {
        this.fieldColumnMap = fieldColumnMap;
    }

    public Map<String, Boolean> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(Map<String, Boolean> primaryKeys){
        this.primaryKeys = primaryKeys;
    }
}
