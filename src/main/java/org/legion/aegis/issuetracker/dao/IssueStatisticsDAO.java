package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.issuetracker.entity.Issue;
import java.util.List;

@Mapper
public interface IssueStatisticsDAO {

    @Select("SELECT * FROM ISU_ISSUE WHERE PROJECT_ID = #{projectId} ")
    List<Issue> retrieveIssueByProject(Long projectId);
}
