package org.legion.aegis.issuetracker.service;

import org.legion.aegis.admin.entity.IssueResolution;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.utils.Calculator;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.issuetracker.consts.IssueConsts;
import org.legion.aegis.issuetracker.dao.IssueStatisticsDAO;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.vo.CommonStatisticsVO;
import org.legion.aegis.issuetracker.vo.UserStatisticsVO;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class IssueStatisticsService {

    private final IssueService issueService;
    private final IssueStatisticsDAO statisticsDAO;
    private final SystemMgrService systemMgrService;
    private final UserAccountService userAccountService;

    public IssueStatisticsService(IssueService issueService, SystemMgrService systemMgrService,
                                  IssueStatisticsDAO statisticsDAO, UserAccountService userAccountService) {
        this.issueService = issueService;
        this.systemMgrService = systemMgrService;
        this.userAccountService = userAccountService;
        this.statisticsDAO = statisticsDAO;
    }

    public List<CommonStatisticsVO> prepareStatusStatData(Long projectId) {
        List<Issue> issueList = statisticsDAO.retrieveIssueByProject(projectId);
        AppContext context = AppContext.getFromWebThread();
        List<CommonStatisticsVO> voList = new ArrayList<>();
        if (userAccountService.isProjectAccessible(context.getUserId(), projectId)) {
            Map<String, List<Issue>> statusMap = new HashMap<>();
            Map<String, IssueStatus> masterCodeMap = systemMgrService.getIssueStatusMap();
            for (Issue issue : issueList) {
                statusMap.computeIfAbsent(issue.getStatus(), k -> new ArrayList<>());
                statusMap.get(issue.getStatus()).add(issue);
            }
            Set<String> keySet = statusMap.keySet();
            for (String key : keySet) {
                voList.add(initStatistics(statusMap, key, masterCodeMap.get(key).getDisplayName(), issueList.size()));
            }
        }
        return voList;
    }

    public List<CommonStatisticsVO> prepareResStatData(Long projectId) {
        List<Issue> issueList = statisticsDAO.retrieveIssueByProject(projectId);
        List<CommonStatisticsVO> voList = new ArrayList<>();
        AppContext context = AppContext.getFromWebThread();
        if (userAccountService.isProjectAccessible(context.getUserId(), projectId)) {
            Map<String, List<Issue>> resMap = new HashMap<>();
            Map<String, IssueResolution> masterCodeMap = systemMgrService.getIssueResolutionMap();
            for (Issue issue : issueList) {
                resMap.computeIfAbsent(issue.getResolution(), k -> new ArrayList<>());
                resMap.get(issue.getResolution()).add(issue);
            }
            Set<String> keySet = resMap.keySet();
            for (String key : keySet) {
                voList.add(initStatistics(resMap, key, masterCodeMap.get(key).getDisplayName(), issueList.size()));
            }
        }
        return voList;
    }

    public List<UserStatisticsVO> prepareUserStat(Long projectId) {
        List<UserStatisticsVO> list = new ArrayList<>();
        List<Issue> issuesUnderProject = statisticsDAO.retrieveIssueByProject(projectId);
        AppContext context = AppContext.getFromWebThread();
        if (issuesUnderProject.size() > 0 && userAccountService.isProjectAccessible(context.getUserId(), projectId)) {
            List<UserAccount> users = userAccountService.getReportersUnderProject(projectId);
            users.addAll(userAccountService.getDevelopersUnderProject(projectId));
            Map<Long, List<Issue>> reporterMap = new HashMap<>(users.size());
            Map<Long, List<Issue>> assignedMap = new HashMap<>(users.size());
            Map<Long, UserStatisticsVO> statisticsVOMap = new HashMap<>(users.size());
            for (Issue issue : issuesUnderProject) {
                reporterMap.putIfAbsent(issue.getReportedBy(), new ArrayList<>());
                reporterMap.get(issue.getReportedBy()).add(issue);
            }
            for (Issue issue : issuesUnderProject) {
                assignedMap.putIfAbsent(issue.getAssignedTo(), new ArrayList<>());
                assignedMap.get(issue.getAssignedTo()).add(issue);
            }
            Set<Long> reporterKeySet = reporterMap.keySet();
            for (Long rid : reporterKeySet) {
                List<Issue> issues = reporterMap.get(rid);
                UserStatisticsVO vo = new UserStatisticsVO();
                vo.setName(userAccountService.getUserById(rid).getName());
                vo.setUserId(rid);
                vo.setTotalReport(issues.size());
                vo.setReportPercent(Calculator.multiply(Calculator.divide(vo.getTotalReport(),
                        issuesUnderProject.size(), 3), 100));
                statisticsVOMap.put(rid, vo);
            }
            Set<Long> assignedKeySet = assignedMap.keySet();
            for (Long aId : assignedKeySet) {
                List<Issue> issues = assignedMap.get(aId);
                UserStatisticsVO vo = statisticsVOMap.get(aId);
                if (vo == null) {
                    vo = new UserStatisticsVO();
                    vo.setName(userAccountService.getUserById(aId).getName());
                    vo.setUserId(aId);
                    statisticsVOMap.put(aId, vo);
                }
                vo.setTotalAssigned(issues.size());
                vo.setAssignedPercent(Calculator.multiply(Calculator.divide(vo.getTotalAssigned(), issues.size(), 3), 100));
                int fixed = 0;
                int unfixed = 0;
                for (Issue issue : issues) {
                    if (IssueConsts.ISSUE_RESOLUTION_RESOLVED.equals(issue.getResolution())
                            || IssueConsts.ISSUE_RESOLUTION_NO_NEED.equals(issue.getResolution())) {
                        fixed ++;
                    } else if (!IssueConsts.ISSUE_RESOLUTION_UNSOLVABLE.equals(issue.getResolution())) {
                        unfixed ++;
                    }
                }
                vo.setTotalFixed(fixed);
                vo.setTotalUnfixed(unfixed);
            }
            statisticsVOMap.forEach((k, v) -> list.add(v));
        }
        return list;
    }

    public UserStatisticsVO displayUserStat(Long userId, Long projectId) {
        UserStatisticsVO vo = new UserStatisticsVO();
        List<Issue> issuesUnderProject = statisticsDAO.retrieveIssueByProject(projectId);
        List<Issue> reportedByMe = statisticsDAO.retrieveIssuesByReporter(userId, projectId);
        List<Issue> assignedToMe = statisticsDAO.retrieveIssuesByDeveloper(userId, projectId);
        vo.setName(userAccountService.getUserById(userId).getName());
        vo.setTotalReport(reportedByMe.size());
        vo.setTotalAssigned(assignedToMe.size());
        for (Issue issue : assignedToMe) {
            if (IssueConsts.ISSUE_RESOLUTION_RESOLVED.equals(issue.getResolution())
                    || IssueConsts.ISSUE_RESOLUTION_NO_NEED.equals(issue.getResolution())) {
                vo.setTotalFixed(vo.getTotalFixed() + 1);
            } else if (!IssueConsts.ISSUE_RESOLUTION_UNSOLVABLE.equals(issue.getResolution())) {
                vo.setTotalUnfixed(vo.getTotalUnfixed() + 1);
            }
        }
        vo.setReportPercent(Calculator.multiply(Calculator.divide(vo.getTotalReport(),
                issuesUnderProject.size(), 3), 100));
        vo.setAssignedPercent(Calculator.multiply(Calculator.divide(vo.getTotalAssigned(),
                issuesUnderProject.size(), 3), 100));
        if (!reportedByMe.isEmpty()) {
            reportedByMe.sort(Comparator.comparing(Issue::getReportedAt));
            int rpDays = DateUtils.getDaysBetween(reportedByMe.get(0).getReportedAt(),
                    reportedByMe.get(reportedByMe.size() - 1).getReportedAt());
            vo.setReportPerDay(Calculator.divide(reportedByMe.size(), rpDays == 0 ? 1 : rpDays));
        }
        if (!assignedToMe.isEmpty()) {
            assignedToMe.sort(Comparator.comparing(Issue::getCreatedAt));
            int asDays = DateUtils.getDaysBetween(assignedToMe.get(0).getCreatedAt(),
                    assignedToMe.get(assignedToMe.size() - 1).getCreatedAt());
            vo.setFixedPerDay(Calculator.divide(vo.getTotalFixed(), asDays == 0 ? 1 : asDays));
            List<Double> slaList = new ArrayList<>();
            Map<String, CommonStatisticsVO> statusStatMap = new HashMap<>();
            Map<String, CommonStatisticsVO> resStatMap = new HashMap<>();
            Map<String, CommonStatisticsVO> sevStatMap = new HashMap<>();
            Map<String, IssueStatus> statusMap = systemMgrService.getIssueStatusMap();
            Map<String, IssueResolution> resMap = systemMgrService.getIssueResolutionMap();
            for (Issue issue : assignedToMe) {
                slaList.add(issueService.getSLA(issue));
                CommonStatisticsVO statusStat = statusStatMap.computeIfAbsent(issue.getStatus(), k -> new CommonStatisticsVO());
                if (StringUtils.isBlank(statusStat.getName())) {
                    statusStat.setName(statusMap.get(issue.getStatus()).getDisplayName());
                }
                statusStat.setCount(statusStat.getCount() + 1);
                statusStat.setPercent(Calculator.multiply(Calculator.
                        divide(statusStat.getCount(), assignedToMe.size(), 3), 100));

                CommonStatisticsVO resStat = resStatMap.computeIfAbsent(issue.getResolution(), k -> new CommonStatisticsVO());
                if (StringUtils.isBlank(resStat.getName())) {
                    resStat.setName(resMap.get(issue.getResolution()).getDisplayName());
                }
                resStat.setCount(resStat.getCount() + 1);
                resStat.setPercent(Calculator.multiply(Calculator.
                        divide(resStat.getCount(), assignedToMe.size(), 3), 100));

                CommonStatisticsVO sevStat = sevStatMap.computeIfAbsent(issue.getSeverity(), k -> new CommonStatisticsVO());
                if (StringUtils.isBlank(sevStat.getName())) {
                    sevStat.setName(MasterCodeUtils.getMasterCode("issue.severity", issue.getSeverity()).getValue());
                }
                sevStat.setCount(sevStat.getCount() + 1);
                sevStat.setPercent(Calculator.multiply(Calculator.
                        divide(sevStat.getCount(), assignedToMe.size(), 3), 100));
            }
            Collections.sort(slaList);
            vo.setAverageSla(Calculator.average(slaList));
            vo.setMinSla(slaList.get(0));
            vo.setMaxSla(slaList.get(slaList.size() - 1));
            vo.setStatusDistMap(statusStatMap);
            vo.setResolutionDistMap(resStatMap);
            vo.setSeverityDistMap(sevStatMap);
        }
        return vo;
    }

    private CommonStatisticsVO initStatistics(Map<String, List<Issue>> map, String key, String name, int size) {
        CommonStatisticsVO vo = new CommonStatisticsVO();
        vo.setName(name);
        vo.setCount(map.get(key).size());
        vo.setPercent(Calculator.multiply(Calculator.divide(vo.getCount(), size, 3), 100));
        return vo;
    }
}
