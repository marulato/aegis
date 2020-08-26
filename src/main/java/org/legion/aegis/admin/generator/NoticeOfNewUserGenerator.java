package org.legion.aegis.admin.generator;

import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.common.docgen.PdfTemplateGenerator;

import java.util.Map;

public class NoticeOfNewUserGenerator extends PdfTemplateGenerator {

    private final UserAccount account;

    public NoticeOfNewUserGenerator(UserAccount account) {
        super();
        this.account = account;
    }
    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getTemplate() {
        return "newUserNotice.ftl";
    }
}
