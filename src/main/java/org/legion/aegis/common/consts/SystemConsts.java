package org.legion.aegis.common.consts;

public class SystemConsts {

    public static final String CLASSPATH    = SystemConsts.class.getResource("/").
                                            getPath().replace("%20", " ");

    public static final String MODE_DEV = "DEV";
    public static final String MODE_PRD = "PRD";
    public static final String MODE_UAT = "UAT";

    public static final String ROOT_STORAGE_PATH = CLASSPATH.replace("classes", "data");
}
