package org.legion.aegis.issuetracker.consts;

import java.util.HashMap;
import java.util.Map;

public class IssueConsts {

    public static final String ISSUE_STATUS_OPEN                    = "OPEN";
    public static final String ISSUE_STATUS_REOPEN                  = "REOPENED";
    public static final String ISSUE_STATUS_CLOSED                  = "CLOSED";
    public static final String ISSUE_STATUS_INVESTIGATION           = "INVEST";
    public static final String ISSUE_STATUS_VERIFIED_DEV            = "VERIFIED_DEV";
    public static final String ISSUE_STATUS_COMMITTED_REPOS         = "COMMITTED_REPO";
    public static final String ISSUE_STATUS_PENDING_DEPLOYMENT      = "P_DEPLOYMENT";
    public static final String ISSUE_STATUS_PENDING_CONFIRMATION    = "P_CONFIRM";
    public static final String ISSUE_STATUS_DEPLOYED                = "DEPLOYED";

    public static final String ISSUE_RESOLUTION_OPEN          = "OPEN";
    public static final String ISSUE_RESOLUTION_RESOLVED      = "RESOLVED";
    public static final String ISSUE_RESOLUTION_POSTPONE      = "POSTPONE";
    public static final String ISSUE_RESOLUTION_RESOLVING     = "RESOLVING";
    public static final String ISSUE_RESOLUTION_UNSOLVABLE    = "UNSOLVABLE";
    public static final String ISSUE_RESOLUTION_NO_NEED       = "NO_NEED";
    public static final String ISSUE_RESOLUTION_NOT_PROCEED   = "NOT_PROCEED";

    public static final String ISSUE_RELATIONSHIP_RESEMBLE   = "RESEMBLE";
    public static final String ISSUE_RELATIONSHIP_DUPLICATED = "DUPLICATED";
    public static final String ISSUE_RELATIONSHIP_CONFLICTED = "CONFLICTED";

    private static final Map<String, String> fieldTypeMap;

    public static String getTimelineType(String field) {
        String name =  fieldTypeMap.get(field);
        return name == null ? "Unknown" : name;
    }

    static {
        fieldTypeMap = new HashMap<>();
        fieldTypeMap.put("STATUS", "问题状态");
        fieldTypeMap.put("RESOLUTION", "解决状态");
        fieldTypeMap.put("ROOT_CAUSE", "问题原因");
        fieldTypeMap.put("FIXED_AT", "解决时间");
        fieldTypeMap.put("ASSIGNED_TO", "转发");
        fieldTypeMap.put("NOTE", "备注");
        fieldTypeMap.put("PRIORITY", "优先级");
        fieldTypeMap.put("CONFIRMATION", "等待确认");
        fieldTypeMap.put("ATTACHMENTS", "附件");
        fieldTypeMap.put("FOLLOWER", "关注");
        fieldTypeMap.put("RELATIONSHIP", "相关性");

    }

}
