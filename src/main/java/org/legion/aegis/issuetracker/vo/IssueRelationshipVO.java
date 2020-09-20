package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;

public class IssueRelationshipVO extends BaseVO {

    private Long id;
    private String srcIssueId;
    private String destIssueId;
    private String relationshipType;

    private String destId;
    private Long srcId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSrcIssueId() {
        return srcIssueId;
    }

    public void setSrcIssueId(String srcIssueId) {
        this.srcIssueId = srcIssueId;
    }

    public String getDestIssueId() {
        return destIssueId;
    }

    public void setDestIssueId(String destIssueId) {
        this.destIssueId = destIssueId;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public Long getSrcId() {
        return srcId;
    }

    public void setSrcId(Long srcId) {
        this.srcId = srcId;
    }
}
