package org.legion.aegis.common.consts;

import java.util.List;

public class AppConsts {


    public static final String YES         = "Y";
    public static final String NO          = "N";
    public static final String TRUE        = "TRUE";
    public static final String FALSE       = "FALSE";
    public static final String TRUE_SHORT  = "T";
    public static final String FALSE_SHORT = "F";

    public static final String STATUS_ACTIVE            = "ACTIVE";
    public static final String STATUS_ACTIVE_CHN        = "正常";
    public static final String STATUS_ACTIVE_SHORT      = "A";
    public static final String STATUS_INACTIVE          = "INACTIVE";
    public static final String STATUS_INACTIVE_CHN      = "未生效";
    public static final String STATUS_INACTIVE_SHORT    = "I";
    public static final String STATUS_EXPIRED           = "EXPIRED";
    public static final String STATUS_EXPIRED_CHN       = "已过期";
    public static final String STATUS_EXPIRED_SHORT     = "E";
    public static final String STATUS_VOID              = "VOID";
    public static final String STATUS_VOID_CHN          = "无效";
    public static final String STATUS_BANNED            = "BANNED";
    public static final String STATUS_BANNED_CHN        = "已禁用";

    public static final String GENDER_MALE    = "M";
    public static final String GENDER_FEMALE  = "F";

    public static final String ACCOUNT_STATUS_ACTIVE    = "A";
    public static final String ACCOUNT_STATUS_INACTIVE  = "I";
    public static final String ACCOUNT_STATUS_EXPIRED   = "E";
    public static final String ACCOUNT_STATUS_VOIDED    = "V";
    public static final String ACCOUNT_STATUS_LOCKED    = "L";
    public static final String ACCOUNT_STATUS_FROZEN    = "F";

    public static final String ROLE_SYSTEM_ADMIN   = "SYSADMIN";
    public static final String ROLE_DEV            = "DEV";
    public static final String ROLE_QA             = "QA";
    public static final String ROLE_DEV_SUPERVISOR = "DEV_S";
    public static final String ROLE_QA_SUPERVISOR  = "QA_S";

    public static final String USER_DOMAIN_INTRANET  = "INTRANET";
    public static final String USER_DOMAIN_EXTRANET  = "EXTRANET";



    public static final int RESPONSE_VALIDATION_NOT_PASS  = 221;
    public static final int RESPONSE_SUCCESS          = 200;
    public static final int RESPONSE_SERVER_ERROR         = 500;
    public static final int RESPONSE_PERMISSION_DENIED    = 502;
    public static final int RESPONSE_MAPPING_NOT_FOUND    = 404;

    public static final String BATCH_JOB_SUCCESS    = "SUCCESS";
    public static final String BATCH_JOB_PROCESSING = "PROCESSING";
    public static final String BATCH_JOB_BLOCKED    = "BLOCKED";
    public static final String BATCH_JOB_FAILED     = "FAILED";

    public static final String FILE_NET_FILE_TYPE_UNKNOWN         = "UN";

    public static final String FILE_NET_STORAGE_TYPE_LOCAL        = "LOCAL";
    public static final String FILE_NET_STORAGE_TYPE_FTP          = "FTP";
    public static final String FILE_NET_STORAGE_TYPE_SFTP         = "SFTP";
    public static final String FILE_NET_STORAGE_TYPE_REMOTE       = "REMOTE";
    public static final String FILE_NET_STORAGE_TYPE_DATABASE     = "DB";

    public static final String FILE_NET_STATUS_STORED           = "STORED";
    public static final String FILE_NET_STATUS_VOID             = "VOID";
    public static final String FILE_NET_STATUS_PENDING_DELETE   = "P_DEL";
    public static final String FILE_NET_STATUS_DELETED          = "DELETED";

    public static final String FILE_NET_SRC_TYPE_USER_UPLOAD       = "U_UPLOAD";
    public static final String FILE_NET_SRC_TYPE_AUTO_GENERATED    = "GENERATED";
    public static final String FILE_NET_SRC_TYPE_EMAIL_ATTACHMENT  = "EMAIL";


    public static final String EMAIL_STATUS_SENT           = "SENT";
    public static final String EMAIL_STATUS_NOT_SENT       = "NOT_SENT";
    public static final String EMAIL_STATUS_SENT_FAILED    = "FAILED";
    public static final String EMAIL_STATUS_DRAFT          = "DRAFT";
    public static final String EMAIL_STATUS_RECOVERED      = "RECOVERED";
    public static final String EMAIL_STATUS_INBOX          = "INBOX";
    public static final String EMAIL_STATUS_OUTBOX         = "OUTBOX";
    public static final String EMAIL_STATUS_DELETED        = "DELETED";

    public static final String EMAIL_RECIPIENT_RECIPIENT      = "RECIPIENT";
    public static final String EMAIL_RECIPIENT_SENDER         = "SENDER";
    public static final String EMAIL_RECIPIENT_CC             = "CC";

    public static final String EMAIL_READ_STATUS_READ      = "READ";
    public static final String EMAIL_READ_STATUS_UNREAD    = "UNREAD";

    public static final String TIMESHEET_EVENT_COMMON       = "COMMON";
    public static final String TIMESHEET_EVENT_TASK         = "TASK";
    public static final String TIMESHEET_EVENT_PUBLIC       = "PUBLIC";

    public static final List<String> ALL_DOC_CATEGORIES = List.of();


}
