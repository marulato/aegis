package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.vo.UserStatisticsVO;

import java.util.List;

@Mapper
public interface IssueStatisticsDAO {

    @Select("SELECT * FROM ISU_ISSUE WHERE PROJECT_ID = #{projectId}")
    List<Issue> retrieveIssueByProject(Long projectId);

    List<Issue> retrieveIssuesByReporter(Long reporterId, Long projectId);

    List<Issue> retrieveIssuesByDeveloper(Long reporterId, Long projectId);

}
