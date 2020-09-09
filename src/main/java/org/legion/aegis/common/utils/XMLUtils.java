package org.legion.aegis.common.utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class XMLUtils {

    public static OutputFormat getDefaultFormat() {
        OutputFormat outputFormat = OutputFormat.createPrettyPrint();
        outputFormat.setEncoding("UTF-8");
        outputFormat.setSuppressDeclaration(false);
        outputFormat.setIndent(true);
        outputFormat.setIndent("    ");
        outputFormat.setNewlines(true);
        return outputFormat;
    }

    public static void write(XMLWriter xmlWriter, Document document) throws Exception {
        if (xmlWriter != null) {
            try {
                xmlWriter.write(document);
            } finally {
                xmlWriter.close();
            }
        }
    }

    public static void mapping(List<Element> nodes, Object entity) throws Exception {
        if (nodes != null && entity != null) {
            Class<?> type = entity.getClass();
            for (Element node : nodes) {
                Field field = type.getDeclaredField(node.getName());
                Reflections.setValue(field, type, entity, node.getText().trim());
            }
        }
    }
}
