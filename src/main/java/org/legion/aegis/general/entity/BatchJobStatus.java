package org.legion.aegis.general.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;
import java.util.Date;

@Entity(tableName = "SA_BATCH_JOB_STATUS")
public class BatchJobStatus extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long batchJobStatusId;
    private String batchJobId;
    private Date startAt;
    private Date endAt;
    private String status;
    private String triggeredBy;

    public Long getBatchJobStatusId() {
        return batchJobStatusId;
    }

    public void setBatchJobStatusId(Long batchJobStatusId) {
        this.batchJobStatusId = batchJobStatusId;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Date getEndAt() {
        return endAt;
    }

    public void setEndAt(Date endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }
}
