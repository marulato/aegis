package org.legion.aegis.common.utils;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;

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
}
