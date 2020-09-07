package org.legion.aegis.common.jpa;



import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.utils.StringUtils;

import java.util.Set;

public abstract class AbstractSQLGenerator {

    protected String insertClause(ORMEntity ormEntity) {
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        columns.append(" (");
        values.append(" VALUES (");
        Set<String> keySet = ormEntity.getFieldColumnMap().keySet();
        for (String field : keySet) {
            columns.append(ormEntity.getFieldColumnMap().get(field)).append(", ");
            if (ormEntity.getPrimaryKeys().get(field) != null && ormEntity.getPrimaryKeys().get(field)) {
                values.append("NULL, ");
            } else {
                values.append("#{").append(field).append("}").append(", ");
            }
        }
        columns.delete(columns.length() - 2, columns.length());
        values.delete(values.length() - 2, values.length());
        columns.append(")");
        values.append(")");
        return columns.append(values).toString();
    }

    protected String updateClause(ORMEntity ormEntity) {
        StringBuilder update = new StringBuilder();
        update.append(" SET ");
        ormEntity.getFieldColumnMap().forEach((field, column) ->
                update.append(column).append(" = ").append("#{").append(field).append("}").append(", "));
        update.delete(update.length() - 2, update.length());
        return update.append(appendWhereClause(ormEntity)).toString();
    }

    protected String whereClause(ORMEntity ormEntity) {
        return String.valueOf(appendWhereClause(ormEntity));
    }

    private StringBuilder appendWhereClause(ORMEntity ormEntity) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isBlank(ormEntity.getWhereClause())) {
            builder.append(" WHERE ");
            if (!ormEntity.getPrimaryKeys().isEmpty()) {
                int index = 0;
                String param;
                for (String id : ormEntity.getPrimaryKeys().keySet()) {
                    if (ormEntity.getPrimaryKeys().size() == 1) {
                        param = id;
                    } else {
                        param = "param" + ++index;
                    }
                    builder.append(ormEntity.getFieldColumnMap().get(id));
                    builder.append(" = ").append("#{").append(param).append("}");
                    builder.append(" AND ");
                }
                builder.delete(builder.lastIndexOf(" AND "), builder.length());
            } else {
                throw new UnsupportedSQLException("The where clause limit of UPDATE or DELETE statement is missing. " +
                        "For the sake of data security, this operation has been prohibited");
            }
        } else {
            builder.append(" WHERE ").append(formatWhereClause(ormEntity.getWhereClause()));
        }
        return builder;
    }

    private String formatWhereClause(String where) {
        if (StringUtils.isNotBlank(where)) {
            where = where.trim();
            if (where.startsWith("WHERE ")) {
                return where.substring("WHERE ".length());
            }
        }
        return where;
    }

    public abstract String insert(BasePO entity);

    public abstract String update(BasePO entity);

    public abstract String delete(BasePO entity);

    public abstract String select(BasePO entity);
}
