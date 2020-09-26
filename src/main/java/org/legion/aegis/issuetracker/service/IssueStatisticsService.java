package org.legion.aegis.issuetracker.service;

import org.legion.aegis.admin.entity.IssueResolution;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.utils.Calculator;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.issuetracker.dao.IssueDAO;
import org.legion.aegis.issuetracker.dao.IssueStatisticsDAO;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.vo.CommonStatisticsVO;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class IssueStatisticsService {

    private final IssueDAO issueDAO;
    private final IssueStatisticsDAO statisticsDAO;
    private final SystemMgrService systemMgrService;
    private final ProjectService projectService;
    private final UserAccountService userAccountService;

    public IssueStatisticsService(IssueDAO issueDAO, SystemMgrService systemMgrService, IssueStatisticsDAO statisticsDAO,
                                  ProjectService projectService, UserAccountService userAccountService) {
        this.issueDAO = issueDAO;
        this.systemMgrService = systemMgrService;
        this.projectService = projectService;
        this.userAccountService = userAccountService;
        this.statisticsDAO = statisticsDAO;
    }

    public List<CommonStatisticsVO> prepareStatusStatData(Long projectId) {
        List<Issue> issueList = statisticsDAO.retrieveIssueByProject(projectId);
        Map<String, List<Issue>> statusMap = new HashMap<>();
        Map<String, IssueStatus> masterCodeMap = systemMgrService.getIssueStatusMap();
        for (Issue issue : issueList) {
            statusMap.computeIfAbsent(issue.getStatus(), k -> new ArrayList<>());
            statusMap.get(issue.getStatus()).add(issue);
        }
        Set<String> keySet = statusMap.keySet();
        List<CommonStatisticsVO> voList = new ArrayList<>();
        for (String key : keySet) {
            voList.add(initStatistics(statusMap, key, masterCodeMap.get(key).getDisplayName(), issueList.size()));
        }
        return voList;
    }

    public List<CommonStatisticsVO> prepareResStatData(Long projectId) {
        List<Issue> issueList = statisticsDAO.retrieveIssueByProject(projectId);
        Map<String, List<Issue>> resMap = new HashMap<>();
        Map<String, IssueResolution> masterCodeMap = systemMgrService.getIssueResolutionMap();
        for (Issue issue : issueList) {
            resMap.computeIfAbsent(issue.getResolution(), k -> new ArrayList<>());
            resMap.get(issue.getResolution()).add(issue);
        }
        Set<String> keySet = resMap.keySet();
        List<CommonStatisticsVO> voList = new ArrayList<>();
        for (String key : keySet) {
            voList.add(initStatistics(resMap, key, masterCodeMap.get(key).getDisplayName(), issueList.size()));
        }
        return voList;
    }

    private CommonStatisticsVO initStatistics(Map<String, List<Issue>> map, String key, String name, int size) {
        CommonStatisticsVO vo = new CommonStatisticsVO();
        vo.setName(name);
        vo.setCount(map.get(key).size());
        vo.setPercent(Calculator.multiply(Calculator.divide(vo.getCount(), size, 3), 100));
        return vo;
    }
}
