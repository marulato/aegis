package org.legion.aegis.issuetracker.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;

@Entity(tableName = "ISU_FOLLOWER", whereClause = "ISSUE_ID = #{issueId} AND USER_ACCT_ID = #{userAcctId}")
public class IssueFollower extends BasePO {

    private Long issueId;
    private Long userAcctId;
    private String isNotificationEnabled;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getUserAcctId() {
        return userAcctId;
    }

    public void setUserAcctId(Long userAcctId) {
        this.userAcctId = userAcctId;
    }

    public String getIsNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setIsNotificationEnabled(String isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }
}
