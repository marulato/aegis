package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.*;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.issuetracker.entity.*;
import org.legion.aegis.issuetracker.vo.IssueVO;
import org.legion.aegis.issuetracker.vo.IssueVcsTrackerVO;

import java.util.List;

@Mapper
public interface IssueDAO {

    List<IssueVO> search(@Param("sp") SearchParam param);

    Integer searchCounts(@Param("sp") SearchParam param);

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void createIssue(Issue issue);

    @Select("SELECT * FROM ISU_ISSUE WHERE ID = #{id}")
    Issue getIssueById(Long id);

    @Select("SELECT * FROM ISU_ATTACHMENT WHERE ISSUE_ID = #{issueId}")
    List<IssueAttachment> getIssueAttachment(Long issueId);

    @Select("SELECT * FROM ISU_NOTE WHERE ISSUE_ID = #{issueId}")
    List<IssueNote> getNotesByIssueId(Long issueId);

    @Select("SELECT * FROM ISU_ISSUE_HISTORY WHERE ISSUE_ID = #{issueId} ORDER BY CREATED_AT")
    List<IssueHistory> getHistoryByIssueId(Long issueId);

    @Select("SELECT * FROM ISU_P_CONFIRMATION WHERE ISSUE_ID = #{issueId}")
    IssueConfirmation getIssueConfirmationByIssueId(Long issueId);

    Integer getTodayNewIssueCount(Long projectId);

    Integer getTodayFixedIssueCount(Long projectId);

    Integer getNotAssignedIssueCount(Long projectId);

    Integer getReopenedIssueCount(Long projectId);

    List<IssueVO> searchAssignedToMe(@Param("sp") SearchParam searchParam);

    Integer searchAssignedToMeCount(@Param("sp") SearchParam searchParam);

    List<IssueVO> searchReportedByMe(@Param("sp") SearchParam searchParam);

    Integer searchReportedByMeCount(@Param("sp") SearchParam searchParam);

    List<IssueVO> searchFollowedByMe(@Param("sp") SearchParam searchParam);

    Integer searchFollowedByMeCount (@Param("sp") SearchParam searchParam);

    @Select("SELECT * FROM ISU_FOLLOWER WHERE ISSUE_ID = #{issueId}")
    List<IssueFollower> getFollowerByIssueId(Long issueId);

    @Select("SELECT * FROM ISU_FOLLOWER WHERE ISSUE_ID = #{param1} AND USER_ACCT_ID = #{param2}")
    IssueFollower getFollowerByIssueIdAndUserId(Long issueId, Long userId);

    @Select("SELECT * FROM ISU_RELATIONSHIP WHERE SRC_ISSUE_ID = #{issueId} OR DEST_ISSUE_ID = #{issueId}")
    List<IssueRelationship> getRelationship(Long issueId);

    @Select("SELECT * FROM ISU_RELATIONSHIP WHERE ID = #{id}")
    IssueRelationship getRelationshipById(Long id);

    @Select("SELECT * FROM ISU_VCS_TRACKER WHERE ISSUE_ID = #{issueId}")
    List<IssueVcsTracker> getIssueVcsTrackerByIssueId(Long issueId);

    @Select("SELECT * FROM ISU_VCS_TRACKER WHERE ID = #{id}")
    IssueVcsTracker getIssueVcsTrackerById(Long id);

    @Select("SELECT * FROM ISU_SEARCH_FILTER WHERE USER_ACCT_ID = #{userId}")
    SearchFilter getSearchFilterByUserId(Long userId);

    List<IssueVcsTrackerVO> searchVcs(@Param("sp") SearchParam param);

    Integer searchVcsCount(@Param("sp") SearchParam param);
}
