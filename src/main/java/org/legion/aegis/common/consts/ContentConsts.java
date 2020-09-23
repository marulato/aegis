package org.legion.aegis.common.consts;

import org.legion.aegis.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ContentConsts {

    public static final String MT_PDF                               = "application/pdf";
    public static final String MT_DOC                               = "application/msword";
    public static final String MT_DOCX                              = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MT_XLS                               = "application/vnd.ms-excel";
    public static final String MT_XLSX                              = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MT_JPEG                              = "image/jpeg";
    public static final String MT_PNG                               = "image/png";
    public static final String MT_ZIP                               = "application/zip";
    public static final String MT_MP4                               = "video/mp4";
    public static final String MT_MOV                               = "video/quicktime";
    public static final String MT_AVI                               = "video/x-msvideo";
    public static final String MT_WMV                               = "video/x-ms-wmv";
    public static final String MT_WAV                               = "audio/wav";
    public static final String MT_MP3                               = "audio/mpeg3";
    public static final String MT_TRM                               = "application/x-msterminal";
    public static final String MT_JPG                               = "image/jpg";
    public static final String MT_TXT                               = "text/plain";

    //Tika content type
    public static final String TK_PDF                               = "application/pdf";
    public static final String TK_DOC                               = "application/msword";
    public static final String TK_DOCX                              = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String TK_XLS                               = "application/vnd.ms-excel";
    public static final String TK_XLSX                              = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String TK_JPEG                              = "image/jpeg";
    public static final String TK_PNG                               = "image/png";
    public static final String TK_ZIP                               = "application/zip";
    public static final String TK_MP4                               = "video/mp4";
    public static final String TK_MOV                               = "video/quicktime";
    public static final String TK_AVI                               = "video/x-msvideo";
    public static final String TK_WMV                               = "video/x-ms-wmv";
    public static final String TK_WAV                               = "audio/wav";
    public static final String TK_MP3                               = "audio/mpeg3";
    public static final String TK_TRM                               = "application/x-msterminal";
    public static final String TK_CSV                               = "text/csv";

    public static String getMimeType(String extension) {
        String mime = extensionMimeMap.get(extension);
        if (StringUtils.isBlank(mime)) {
            mime = extensionMimeMap.get(AppConsts.FILE_NET_FILE_TYPE_UNKNOWN);
        }
        return mime;
    }

    private static final Map<String, String> extensionMimeMap;

    static {
        extensionMimeMap = new HashMap<>(20);
        extensionMimeMap.put("PDF", MT_PDF);
        extensionMimeMap.put("DOC", MT_DOC);
        extensionMimeMap.put("DOCX", MT_DOCX);
        extensionMimeMap.put("XLS", MT_XLS);
        extensionMimeMap.put("XLSX", MT_XLSX);
        extensionMimeMap.put("JPG", MT_JPG);
        extensionMimeMap.put("JPEG", MT_JPEG);
        extensionMimeMap.put("PNG", MT_PNG);
        extensionMimeMap.put("ZIP", MT_ZIP);
        extensionMimeMap.put("MP4", MT_MP4);
        extensionMimeMap.put("MOV", MT_MOV);
        extensionMimeMap.put("AVI", MT_AVI);
        extensionMimeMap.put("WMV", MT_WMV);
        extensionMimeMap.put("WAV", MT_WAV);
        extensionMimeMap.put("MP3", MT_MP3);
        extensionMimeMap.put("TRM", MT_TRM);
        extensionMimeMap.put("TXT", MT_TXT);
        extensionMimeMap.put(AppConsts.FILE_NET_FILE_TYPE_UNKNOWN, "application/text");
    }

}
