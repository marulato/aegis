package org.legion.aegis.common.jpa;


import org.legion.aegis.common.utils.StringUtils;

public class NameConvertor {

    private NameConvertor(){}

    public static String getProperty(String column) {
        StringBuilder property = new StringBuilder();
        if (!StringUtils.isEmpty(column)) {
            if (StringUtils.hasUnderline(column)) {
                String[] props = column.split("_");
                property.append(props[0].toLowerCase());
                for (int i = 1; i < props.length; i++) {
                    property.append(StringUtils.toTitleCase(props[i].toLowerCase()));
                }
            } else {
                property.append(column.toLowerCase());
            }
        }
        return property.toString();
    }

    public static String getColumn(String property) {
        StringBuilder column = new StringBuilder();
        if (!StringUtils.isEmpty(property)) {
            if (StringUtils.hasTitleCase(property)) {
                String[] props = StringUtils.splitByUpperCase(property);
                for (String prop : props) {
                    column.append(prop.toUpperCase()).append("_");
                }
                column.deleteCharAt(column.length() -1);
            } else {
                column.append(property.toUpperCase());
            }
        }
        return column.toString();
    }
}
