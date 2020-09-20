package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;
import org.legion.aegis.issuetracker.entity.IssueVcsTracker;

public class IssueVcsTrackerVO extends BaseVO {

    public IssueVcsTrackerVO() {}

    public IssueVcsTrackerVO(IssueVcsTracker issueVcsTracker) {
        super(issueVcsTracker);
    }

    private Long id;
    private Long issueId;
    private String fileFullPath;
    private String masterVersion;
    private String branch;
    private String branchVersion;
    private String tag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getFileFullPath() {
        return fileFullPath;
    }

    public void setFileFullPath(String fileFullPath) {
        this.fileFullPath = fileFullPath;
    }

    public String getMasterVersion() {
        return masterVersion;
    }

    public void setMasterVersion(String masterVersion) {
        this.masterVersion = masterVersion;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBranchVersion() {
        return branchVersion;
    }

    public void setBranchVersion(String branchVersion) {
        this.branchVersion = branchVersion;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
