package org.legion.aegis.issuetracker.dto;

import org.legion.aegis.admin.entity.UserProjectAssign;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.utils.NumberUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.issuetracker.consts.IssueConsts;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.service.IssueService;

import java.util.List;

public class IssueRelationshipDto extends BaseDto {

    private String id;
    @NotBlank(message = "")
    private String srcIssueId;
    @ValidateWithMethod(methodName = "validateDest", message = "此ID不存在或您没有权限访问")
    private String destIssueId;
    @MemberOf(value = {IssueConsts.ISSUE_RELATIONSHIP_RESEMBLE, IssueConsts.ISSUE_RELATIONSHIP_CONFLICTED,
            IssueConsts.ISSUE_RELATIONSHIP_DUPLICATED}, message = "请选择正确的相关性表示")
    private String relationshipType;

    private boolean validateDest(String destIssueId) {
        IssueService issueService = SpringUtils.getBean(IssueService.class);
        Issue issue = issueService.getIssueById(StringUtils.parseIfIsLong(destIssueId));
        if (issue != null) {
            AppContext context = AppContext.getFromWebThread();
            List<UserProjectAssign> assigns = context.getAssignments();
            for (UserProjectAssign assign : assigns) {
                if (NumberUtils.isPositive(assign.getProjectId()) && issue.getProjectId().equals(assign.getProjectId())) {
                    return true;
                }
                if (NumberUtils.isInvalidId(assign.getProjectId()) && issue.getGroupId().equals(assign.getGroupId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
