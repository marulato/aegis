package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.*;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.issuetracker.entity.*;
import org.legion.aegis.issuetracker.vo.IssueVO;

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
}
