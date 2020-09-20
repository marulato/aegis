package org.legion.aegis.common.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.cli.Digest;
import org.apache.commons.codec.digest.DigestUtils;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.SystemConsts;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileNameGenerator {

    public static String format(String path) {
        if (StringUtils.isNotBlank(path)) {
            if ("\\".equals(File.separator)) {
                path = path.replaceAll("/", "\\\\");
                if (!path.endsWith("\\")) {
                    path += "\\";
                }
            } else {
                path = path.replaceAll("\\\\", "/");
                if (!path.endsWith("/")) {
                    path += "/";
                }
            }
            return path;
        }
        return null;
    }

    public static String getUserTempPath() {
        return SystemConsts.ROOT_TEMP_PATH + AppContext.getFromWebThread().getUserId() + File.separator;
    }

}
