package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.Mapper;
import org.legion.aegis.issuetracker.entity.Issue;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface IssueStatisticsDAO {

    List<Issue> retrieveIssueByProject(Long projectId, Map<String, Date> dateMap);

    List<Issue> retrieveIssuesByReporter(Long reporterId, Long projectId, Map<String, Date> dateMap);

    List<Issue> retrieveIssuesByDeveloper(Long reporterId, Long projectId, Map<String, Date> dateMap);

}
