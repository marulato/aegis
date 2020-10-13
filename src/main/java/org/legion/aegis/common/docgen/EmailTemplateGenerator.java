package org.legion.aegis.common.docgen;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public abstract class EmailTemplateGenerator implements IDocGenerator{

    @Override
    public byte[] generate() throws Exception {
        return getEmailContent().getBytes(StandardCharsets.UTF_8);
    }

    public String getEmailContent() throws Exception {
        String templatePath = this.getClass().getResource("/").getPath();
        templatePath = templatePath.replaceAll("%20", " ") + "emails/";
        String ftl = getTemplate();
        String fileName = getTemplate();
        if (ftl.contains("/")) {
            fileName = ftl.substring(ftl.lastIndexOf("/") + 1);
            templatePath += ftl.substring(0, ftl.lastIndexOf("/"));
        }
        if ("\\".equals(File.separator)) {
            templatePath = templatePath.replaceAll("/", "\\\\");
        }
        if (!templatePath.startsWith(File.separator)) {
            templatePath = File.separator + templatePath;
        }
        Configuration config = new Configuration(Configuration.VERSION_2_3_30);
        config.setDirectoryForTemplateLoading(new File(templatePath));
        Template template = config.getTemplate(fileName, "UTF-8");
        StringWriter writer = new StringWriter();
        template.process(getParameters(), writer);
        return writer.toString();
    }

    public abstract String getSubject();

}
