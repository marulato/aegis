package org.legion.aegis.issuetracker.dto;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.issuetracker.controller.IssueController;
import org.legion.aegis.issuetracker.dao.IssueDAO;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.entity.IssueFollower;

import java.util.List;

public class IssueFollowerDto extends BaseDto {

    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validateDuplicate", message = "你已经关注此问题，无需重复关注"),
            @ValidateWithMethod(methodName = "validateInCharge", message = "你已经是该问题的负责人，无需关注")
    })
    private String issueId;
    private String userAcctId;
    @MemberOf(value = {AppConsts.YES, AppConsts.NO}, message = "请选择是否接受邮件通知")
    private String isNotificationEnabled;

    private boolean validateDuplicate(String issueId) {
        Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
        if (issue != null) {
            AppContext context = AppContext.getFromWebThread();
            IssueDAO issueDAO = SpringUtils.getBean(IssueDAO.class);
            List<IssueFollower> followers = issueDAO.getFollowerByIssueId(issue.getId());
            for (IssueFollower follower : followers) {
                if (follower.getUserAcctId().equals(context.getUserId())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean validateInCharge(String userAcctId) {
        AppContext context = AppContext.getFromWebThread();
        Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
        if (issue != null) {
            return !issue.getAssignedTo().equals(context.getUserId())
                    && !issue.getReportedBy().equals(context.getUserId());
        }
        return false;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getUserAcctId() {
        return userAcctId;
    }

    public void setUserAcctId(String userAcctId) {
        this.userAcctId = userAcctId;
    }

    public String getIsNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setIsNotificationEnabled(String isNotificationEnabled) {
        this.isNotificationEnabled = isNotificationEnabled;
    }
}
