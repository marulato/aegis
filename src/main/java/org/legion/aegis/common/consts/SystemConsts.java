package org.legion.aegis.common.consts;

public class SystemConsts {

    public static final String CLASSPATH    = SystemConsts.class.getResource("/").
                                            getPath().replace("%20", " ");

    public static final String MODE_DEV = "DEV";
    public static final String MODE_PRD = "PRD";
    public static final String MODE_UAT = "UAT";

    public static final String FILE_STORAGE_PATH_EMAIL_ATTACHMENT = "/legion/email/{0}/";
    public static final String FILE_STORAGE_PATH_APP_DOC_UPLOAD = "/legion/application/{0}/user_upload/";
    public static final String FILE_STORAGE_PATH_APP_AUTO_GEN = "/legion/application/{0}/auto_generated/";
}
