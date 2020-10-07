package org.legion.aegis.common.consts;

import org.legion.aegis.common.utils.Calculator;
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

    public static final int KB   = 1024;
    public static final int MB   = 1024 * KB;
    public static final long GB  = 1024L * MB;

    private static final Map<String, String> extensionMimeMap;
    private static final Map<String, String> iconClassMap;


    public static String getMimeType(String extension) {
        String mime = extensionMimeMap.get(extension);
        if (StringUtils.isBlank(mime)) {
            mime = extensionMimeMap.get(AppConsts.FILE_NET_FILE_TYPE_UNKNOWN);
        }
        return mime;
    }

    public static String getIconClass(String extension) {
        String icon = iconClassMap.get(extension);
        if (StringUtils.isBlank(icon)) {
            icon = "far fa-file-alt";
        }
        return icon;
    }

    public static String getSize(long size) {
        if (size >= 0 && size < KB) {
            return size + "B";
        } else if (size >= KB && size < MB) {
            return Calculator.divide(size, KB, 1) + " KB";
        } else if (size >= MB && size < GB) {
            return Calculator.divide(size, MB, 1) + " MB";
        } else if (size >= GB){
            return Calculator.divide(size, GB, 1) + " GB";
        }
        return "-1B";
    }

    static {
        extensionMimeMap = new HashMap<>(30);
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

        iconClassMap = new HashMap<>(50);
        iconClassMap.put("PDF", "far fa-file-pdf");
        iconClassMap.put("DOC", "far fa-file-word");
        iconClassMap.put("DOCX", "far fa-file-word");
        iconClassMap.put("XLS", "far fa-file-spreadsheet");
        iconClassMap.put("XLSX", "far fa-file-spreadsheet");
        iconClassMap.put("ZIP", "far fa-file-archive");
        iconClassMap.put("RAR", "far fa-file-archive");
        iconClassMap.put("7Z", "far fa-file-archive");
        iconClassMap.put("GZ", "far fa-file-archive");
        iconClassMap.put("XZ", "far fa-file-archive");
        iconClassMap.put("TAR", "far fa-file-archive");
        iconClassMap.put("PPT", "far fa-file-powerpoint");
        iconClassMap.put("PPTX", "far fa-file-powerpoint");
        iconClassMap.put("TXT", "far fa-file-code");
        iconClassMap.put("XML", "far fa-file-code");
        iconClassMap.put("JPG", "far fa-file-image");
        iconClassMap.put("JPEG", "far fa-file-image");
        iconClassMap.put("PNG", "far fa-file-image");
        iconClassMap.put("BMP", "far fa-file-image");
        iconClassMap.put("GIF", "far fa-file-image");
        iconClassMap.put("MP3", "far fa-file-audio");
        iconClassMap.put("WAV", "far fa-file-audio");
        iconClassMap.put("AAC", "far fa-file-audio");
        iconClassMap.put("APE", "far fa-file-audio");
        iconClassMap.put("FLAC", "far fa-file-audio");
        iconClassMap.put("OGG", "far fa-file-audio");
        iconClassMap.put("MID", "far fa-file-audio");
        iconClassMap.put("MP4", "far fa-file-video");
        iconClassMap.put("MOV", "far fa-file-video");
        iconClassMap.put("AVI", "far fa-file-video");
        iconClassMap.put("FLV", "far fa-file-video");
        iconClassMap.put("M4V", "far fa-file-video");
        iconClassMap.put("MKV", "far fa-file-video");
    }

}
