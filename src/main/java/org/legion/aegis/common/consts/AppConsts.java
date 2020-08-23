package org.legion.aegis.common.consts;

import java.util.List;

public class AppConsts {

    public static final String YES         = "Y";
    public static final String NO          = "N";
    public static final String TRUE        = "TRUE";
    public static final String FALSE       = "FALSE";
    public static final String TRUE_SHORT  = "T";
    public static final String FALSE_SHORT = "F";

    public static final String GENDER_MALE    = "M";
    public static final String GENDER_FEMALE  = "F";

    public static final String ACCOUNT_STATUS_ACTIVE    = "A";
    public static final String ACCOUNT_STATUS_INACTIVE  = "I";
    public static final String ACCOUNT_STATUS_EXPIRED   = "E";
    public static final String ACCOUNT_STATUS_VOIDED    = "V";
    public static final String ACCOUNT_STATUS_LOCKED    = "L";
    public static final String ACCOUNT_STATUS_FROZEN    = "F";



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

    public static final String EMIAL_STATUS_NOT_SENT       = "NOT_SENT";
    public static final String EMIAL_STATUS_SENT           = "SENT";
    public static final String EMIAL_STATUS_RECEIVED       = "RECEIVED";
    public static final String EMIAL_STATUS_READ           = "READ";
    public static final String EMIAL_STATUS_RECYCLED       = "RECYCLED";
    public static final String EMIAL_STATUS_SENT_FAILED    = "FAILED";


    public static final List<String> ALL_DOC_CATEGORIES = List.of();


}
