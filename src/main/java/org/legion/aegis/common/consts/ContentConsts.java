package org.legion.aegis.common.consts;

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

    public static Map<String, String> getExtensionMimeMap() {
        Map<String, String> map = new HashMap<>(20);
        map.put("PDF", MT_PDF);
        map.put("DOC", MT_DOC);
        map.put("DOCX", MT_DOCX);
        map.put("XLS", MT_XLS);
        map.put("XLSX", MT_XLSX);
        map.put("JPG", MT_JPG);
        map.put("JPEG", MT_JPEG);
        map.put("PNG", MT_PNG);
        map.put("ZIP", MT_ZIP);
        map.put("MP4", MT_MP4);
        map.put("MOV", MT_MOV);
        map.put("AVI", MT_AVI);
        map.put("WMV", MT_WMV);
        map.put("WAV", MT_WAV);
        map.put("MP3", MT_MP3);
        map.put("TRM", MT_TRM);
        map.put("TXT", MT_TXT);
        map.put(AppConsts.FILE_NET_FILE_TYPE_UNKNOWN, "application/text");
        return map;
    }

}
