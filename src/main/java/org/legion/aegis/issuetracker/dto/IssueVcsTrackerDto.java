package org.legion.aegis.issuetracker.dto;


import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.NotBlank;

public class IssueVcsTrackerDto extends BaseDto {

    private Long id;
    private Long issueId;
    @NotBlank(message = "文件路径不能为空")
    @Length(max = 4000, message = "长度不得超过4000字符")
    private String fileFullPath;
    @NotBlank(message = "主版本不能为空")
    @Length(max = 64, message = "长度不得超过64字符")
    private String masterVersion;
    @Length(max = 256, message = "长度不得超过256字符")
    private String branch;
    @Length(max = 64, message = "长度不得超过64字符")
    private String branchVersion;
    @Length(max = 64, message = "长度不得超过64字符")
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
