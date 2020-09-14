package org.legion.aegis.issuetracker.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;

@Entity(tableName = "ISU_P_CONFIRMATION", whereClause = "ISSUE_ID = #{issueId}")
public class IssueConfirmation extends BasePO {

    private Long issueId;
    private Long requestFrom;
    private Long requestTo;
    private String isConfirmed;

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(Long requestFrom) {
        this.requestFrom = requestFrom;
    }

    public Long getRequestTo() {
        return requestTo;
    }

    public void setRequestTo(Long requestTo) {
        this.requestTo = requestTo;
    }

    public String getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(String isConfirmed) {
        this.isConfirmed = isConfirmed;
    }
}
