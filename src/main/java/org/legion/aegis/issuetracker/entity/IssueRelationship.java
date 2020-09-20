package org.legion.aegis.issuetracker.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;
import org.legion.aegis.common.jpa.annotation.PrimaryKey;

@Entity(tableName = "ISU_RELATIONSHIP")
public class IssueRelationship extends BasePO {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    private Long srcIssueId;
    private Long destIssueId;
    private String relationshipType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSrcIssueId() {
        return srcIssueId;
    }

    public void setSrcIssueId(Long srcIssueId) {
        this.srcIssueId = srcIssueId;
    }

    public Long getDestIssueId() {
        return destIssueId;
    }

    public void setDestIssueId(Long destIssueId) {
        this.destIssueId = destIssueId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
