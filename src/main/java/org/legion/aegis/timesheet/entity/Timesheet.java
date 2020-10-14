package org.legion.aegis.timesheet.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "GNL_TIMESHEET")
public class Timesheet extends BasePO {
    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long userAcctId;
    private String eventReferralTbl;
    private Long eventReferralId;
    private String isAutoAdded;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAcctId() {
        return userAcctId;
    }

    public void setUserAcctId(Long userAcctId) {
        this.userAcctId = userAcctId;
    }

    public String getEventReferralTbl() {
        return eventReferralTbl;
    }

    public void setEventReferralTbl(String eventReferralTbl) {
        this.eventReferralTbl = eventReferralTbl;
    }

    public Long getEventReferralId() {
        return eventReferralId;
    }

    public void setEventReferralId(Long eventReferralId) {
        this.eventReferralId = eventReferralId;
    }

    public String getIsAutoAdded() {
        return isAutoAdded;
    }

    public void setIsAutoAdded(String isAutoAdded) {
        this.isAutoAdded = isAutoAdded;
    }
}
