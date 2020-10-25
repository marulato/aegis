package org.legion.aegis.issuetracker.service;

import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.jpa.NameConvertor;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.*;
import org.legion.aegis.general.entity.FileNet;
import org.legion.aegis.general.service.FileNetService;
import org.legion.aegis.general.websocket.MessageWebSocketServer;
import org.legion.aegis.issuetracker.consts.IssueConsts;
import org.legion.aegis.issuetracker.dao.IssueDAO;
import org.legion.aegis.issuetracker.dto.IssueDto;
import org.legion.aegis.issuetracker.dto.IssueFollowerDto;
import org.legion.aegis.issuetracker.dto.IssueRelationshipDto;
import org.legion.aegis.issuetracker.dto.IssueVcsTrackerDto;
import org.legion.aegis.issuetracker.entity.*;
import org.legion.aegis.issuetracker.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class IssueService {

    private final IssueDAO issueDAO;
    private final SystemMgrService systemMgrService;
    private final FileNetService fileNetService;
    private final ProjectService projectService;
    private final UserAccountService userAccountService;
    private final IssueEmailService emailService;

    private static final Logger log = LoggerFactory.getLogger(IssueService.class);

    @Autowired
    public IssueService(IssueDAO issueDAO, SystemMgrService systemMgrService,
                        FileNetService fileNetService, ProjectService projectService, UserAccountService userAccountService,
                        IssueEmailService emailService) {
        this.issueDAO = issueDAO;
        this.systemMgrService = systemMgrService;
        this.fileNetService = fileNetService;
        this.projectService = projectService;
        this.userAccountService = userAccountService;
        this.emailService = emailService;
    }

    public SearchResult<IssueVO> search(SearchParam param) {
        if (StringUtils.isNotBlank((String) param.getParams().get("reportedRange"))){
            String[] dateRange = ((String) param.getParams().get("reportedRange")).split("-");
            if (dateRange.length == 2) {
                Date start = DateUtils.parseDatetime(dateRange[0]);
                Date end = DateUtils.parseDatetime(dateRange[1]);
                if (start != null && end != null && !end.before(start)) {
                    end = DateUtils.addDay(end, 1);
                    param.addParam("reportedFrom", start);
                    param.addParam("reportedTo", end);
                }
            }
        }
        if (StringUtils.isNotBlank((String) param.getParams().get("updatedRange"))){
            String[] dateRange = ((String) param.getParams().get("updatedRange")).split("-");
            if (dateRange.length == 2) {
                Date start = DateUtils.parseDatetime(dateRange[0]);
                Date end = DateUtils.parseDatetime(dateRange[1]);
                if (start != null && end != null && !end.before(start)) {
                    end = DateUtils.addDay(end, 1);
                    param.addParam("updatedFrom", start);
                    param.addParam("updatedTo", end);
                }
            }
        }
        List<IssueVO> results = issueDAO.search(param);
        for (IssueVO vo : results) {
            if (vo.getFixedAt() == null) {
                vo.setSla(String.valueOf(Calculator.divide(DateUtils.getHoursBetween(
                        DateUtils.parseDatetime(vo.getReportedAt()), new Date()), 24, 1)));
            } else {
                vo.setSla(String.valueOf(Calculator.divide(DateUtils.getHoursBetween(
                        DateUtils.parseDatetime(vo.getReportedAt()),
                        DateUtils.parseDatetime(vo.getFixedAt())), 24, 1)));
            }
            vo.setIssueId(formatIssueId(String.valueOf(vo.getId())));
            Issue issue = issueDAO.getIssueById(vo.getId());
            IssueStatus issueStatus = systemMgrService.getIssueStatusByCode(issue.getStatus());
            if (issueStatus != null) {
                vo.setColor(issueStatus.getColor());
            }
        }
        if (param.getOrderColumnNo() != null && param.getOrderColumnNo() == 5) {
            if ("ASC".equalsIgnoreCase(param.getOrder())) {
                results.sort(Comparator.comparing(IssueVO::getSla));
            } else {
                results.sort(Comparator.comparing(IssueVO::getSla, Comparator.reverseOrder()));
            }
        }
        SearchResult<IssueVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(issueDAO.searchCounts(param));
        AppContext context = AppContext.getFromWebThread();
        SearchFilter filter = issueDAO.getSearchFilterByUserId(context.getUserId());
        if (filter == null) {
            filter = new SearchFilter();
            filter.setUserAcctId(context.getUserId());
            filter.setGroupId(StringUtils.parseIfIsLong((String) param.getParams().get("groupId")));
            filter.setProjectId(StringUtils.parseIfIsLong((String) param.getParams().get("projectId")));
            JPAExecutor.save(filter);
        } else {
            filter.setGroupId(StringUtils.parseIfIsLong((String) param.getParams().get("groupId")));
            filter.setProjectId(StringUtils.parseIfIsLong((String) param.getParams().get("projectId")));
            JPAExecutor.update(filter);
        }
        return searchResult;
    }

    @Transactional
    public void reportIssue(IssueDto dto) throws Exception {
        if (dto != null) {
            AppContext context = AppContext.getFromWebThread();
            Issue issue = BeanUtils.mapFromDto(dto, Issue.class);
            if (issue.getAssignedTo() == null || issue.getAssignedTo() == 0) {
                issue.setStatus(IssueConsts.ISSUE_RESOLUTION_OPEN);
            } else {
                issue.setStatus(IssueConsts.ISSUE_STATUS_INVESTIGATION);
            }
            issue.setResolution(IssueConsts.ISSUE_RESOLUTION_OPEN);
            issue.setDescription(dto.getDescription().getBytes(StandardCharsets.UTF_8));
            issue.setReportedAt(new Date());
            issue.setReportedBy(context.getUserId());
            issue.createAuditValues(context);
            issueDAO.createIssue(issue);
            saveAttachments(dto.getAttachments(), issue);
            emailService.sendEmailNewIssueReported(issue);
            MessageWebSocketServer socketServer = SpringUtils.getBean(MessageWebSocketServer.class);
            socketServer.sendMessage(issue.getAssignedTo(),
                    formatIssueId("[" + issue.getId()) + "] " + issue.getTitle());
        }
    }

    public Issue getIssueById(Long id) {
        return issueDAO.getIssueById(id);
    }

    public IssueVO getIssueVOForView(Issue issue) {
        if (issue != null) {
            AppContext context = AppContext.getFromWebThread();
            IssueVO vo = new IssueVO();
            vo.setId(issue.getId());
            vo.setStatusCode(issue.getStatus());
            vo.setSeverityCode(issue.getSeverity());
            vo.setPriorityCode(issue.getPriority());
            vo.setResolutionCode(issue.getResolution());
            vo.setIssueId(formatIssueId(String.valueOf(issue.getId())));
            vo.setTitle(issue.getTitle());
            vo.setDescription(new String(issue.getDescription(), StandardCharsets.UTF_8));
            vo.setRootCause(issue.getRootCause());
            IssueStatus issueStatus = systemMgrService.getIssueStatusByCode(issue.getStatus());
            vo.setStatus(issueStatus.getDisplayName());
            vo.setColor(issueStatus.getColor());
            IssueResolution issueResolution = systemMgrService.getIssueResolutionByCode(issue.getResolution());
            vo.setResolution(issueResolution.getDisplayName());
            vo.setSeverity(MasterCodeUtils.getMasterCode("issue.severity", issue.getSeverity()).getValue());
            vo.setSeverityColor(MasterCodeUtils.getMasterCode("issue.severity", issue.getSeverity()).getDescription());
            vo.setPriority(MasterCodeUtils.getMasterCode("issue.priority", issue.getPriority()).getValue());
            UserAccount assignedTo = userAccountService.getUserById(issue.getAssignedTo());
            UserAccount reportedBy = userAccountService.getUserById(issue.getReportedBy());
            vo.setAssignedTo(assignedTo != null ? assignedTo.getName() : "-");
            vo.setReportedBy(reportedBy.getName());
            vo.setReportedAt(DateUtils.getDateString(issue.getReportedAt(), "yyyy/MM/dd HH:mm:ss"));
            vo.setFixedAt(DateUtils.getDateString(issue.getFixedAt(), "yyyy/MM/dd HH:mm"));
            List<IssueAttachment> attachments = getIssueAttachment(issue.getId());
            vo.setAttachments(new ArrayList<>());
            for (IssueAttachment attachment : attachments) {
                FileNet fileNet = fileNetService.getFileNetById(attachment.getFileNetId());
                vo.getAttachments().add(fileNet);
            }
            List<IssueNote> notes = issueDAO.getNotesByIssueId(issue.getId());
            vo.setNotes(new ArrayList<>());
            for (IssueNote note : notes) {
                IssueNoteVO noteVO = new IssueNoteVO();
                noteVO.setContent(note.getContent());
                noteVO.setCreatedAt(DateUtils.getDateString(note.getCreatedAt(), "yyyy/MM/dd HH:mm:ss"));
                noteVO.setCreatedBy(userAccountService.getUserByLoginId(note.getCreatedBy()).getName());
                vo.getNotes().add(noteVO);
            }
            ProjectGroup group = projectService.getProjectGroupById(issue.getGroupId());
            vo.setGroupName(group.getName());
            Project project = projectService.getProjectById(issue.getProjectId(), false);
            vo.setProjectName(project.getName());
            Module module = projectService.getModuleById(issue.getModuleId());
            vo.setModuleName(module.getName());
            IssueConfirmation confirmation = issueDAO.getIssueConfirmationByIssueId(issue.getId());
            if (confirmation != null) {
                IssueConfirmationVO confirmationVO = new IssueConfirmationVO();
                confirmationVO.setRequestFrom(userAccountService.getUserById(confirmation.getRequestFrom()).getName());
                confirmationVO.setRequestTo(userAccountService.getUserById(confirmation.getRequestTo()).getName());
                confirmationVO.setIsConfirmed(confirmation.getIsConfirmed());
                vo.setConfirmation(confirmationVO);
            }
            List<IssueFollower> followers = issueDAO.getFollowerByIssueId(issue.getId());
            vo.setCanConfirm(true);
            if (issue.getAssignedTo().equals(context.getUserId())
                    || issue.getReportedBy().equals(context.getUserId())) {
                vo.setCanCancel(false);
                vo.setCanConfirm(false);
            }
            vo.setFollowers(new ArrayList<>());
            for (IssueFollower follower : followers) {
                vo.getFollowers().add(userAccountService.getUserById(follower.getUserAcctId()).getName());
                if (follower.getUserAcctId().equals(context.getUserId())) {
                    vo.setCanCancel(true);
                    vo.setCanConfirm(false);
                    break;
                }
            }
            List<IssueRelationship> relationships = issueDAO.getRelationship(issue.getId());
            vo.setRelationships(new ArrayList<>());
            for (IssueRelationship relationship : relationships) {
                IssueRelationshipVO relationshipVO = new IssueRelationshipVO();
                relationshipVO.setId(relationship.getId());
                relationshipVO.setSrcId(relationship.getSrcIssueId());
                if (relationship.getSrcIssueId().equals(issue.getId())) {
                    relationshipVO.setDestIssueId(formatIssueId(String.valueOf(relationship.getDestIssueId())));
                    relationshipVO.setDestId(String.valueOf(relationship.getDestIssueId()));
                } else if (relationship.getDestIssueId().equals(issue.getId())) {
                    relationshipVO.setDestIssueId(formatIssueId(String.valueOf(relationship.getSrcIssueId())));
                    relationshipVO.setDestId(String.valueOf(relationship.getSrcIssueId()));
                }
                relationshipVO.setRelationshipType(MasterCodeUtils.getMasterCode("issue.relationship",
                        relationship.getRelationshipType()).getValue());
                vo.getRelationships().add(relationshipVO);
            }
            List<IssueVcsTracker> vcsTrackers = issueDAO.getIssueVcsTrackerByIssueId(issue.getId());
            vo.setVcsTrackers(new ArrayList<>());
            for (IssueVcsTracker tracker : vcsTrackers) {
                IssueVcsTrackerVO trackerVO = new IssueVcsTrackerVO(tracker);
                vo.getVcsTrackers().add(trackerVO);
            }
            return vo;
        }
        return null;
    }

    public List<IssueAttachment> getIssueAttachment(Long issueId) {
        return issueDAO.getIssueAttachment(issueId);
    }

    @Transactional
    public void updateIssue(IssueDto dto, Long issueId) throws Exception {
        Issue issue = getIssueById(issueId);
        boolean isNotModified;
        if (dto != null && issue != null) {
            isNotModified = createIssueHistory(issueId, issue.getStatus(), dto.getStatus(), "status");
            issue.setStatus(dto.getStatus());

            isNotModified = isNotModified && createIssueHistory(issueId, issue.getRootCause(), dto.getRootCause(), "rootCause");
            issue.setRootCause(dto.getRootCause());

            isNotModified = isNotModified && createIssueHistory(issueId, issue.getResolution(), dto.getResolution(), "resolution");
            issue.setResolution(dto.getResolution());

            isNotModified = isNotModified && createIssueHistory(issueId, issue.getSeverity(), dto.getSeverity(), "severity");
            issue.setSeverity(dto.getSeverity());

            isNotModified = isNotModified && createIssueHistory(issueId, issue.getPriority(), dto.getPriority(), "priority");
            issue.setPriority(dto.getPriority());

            if (dto.getFixedAt() != null && (IssueConsts.ISSUE_RESOLUTION_RESOLVED.equals(dto.getResolution())
            || IssueConsts.ISSUE_RESOLUTION_NO_NEED.equals(dto.getResolution())
                    || IssueConsts.ISSUE_RESOLUTION_UNSOLVABLE.equals(dto.getResolution()))) {
                createIssueHistory(issueId, DateUtils.getDateString(issue.getFixedAt(), "yyyy/MM/dd HH:mm"),
                        dto.getFixedAt(), "fixedAt");
                issue.setFixedAt(DateUtils.parseDatetime(dto.getFixedAt(), "yyyy/MM/dd HH:mm"));
                isNotModified = false;
            }

            if (StringUtils.isNotBlank(dto.getUpdatedNote())) {
                IssueNote issueNote = new IssueNote();
                issueNote.setIssueId(issue.getId());
                issueNote.setContent(dto.getUpdatedNote());
                JPAExecutor.save(issueNote);
                isNotModified = false;
            }

            IssueConfirmation existConfirmation = issueDAO.getIssueConfirmationByIssueId(issueId);
            AppContext context = AppContext.getFromWebThread();
            if (existConfirmation == null && StringUtils.isNotBlank(dto.getConfirmedBy())) {
                //add confirmation
                log.info("Add Confirmation");
                isNotModified = false;
                createIssueHistory(issueId, null, context.getUserId() + "-" +
                        StringUtils.parseIfIsLong(dto.getConfirmedBy()), "confirmation");
                IssueConfirmation confirmation = new IssueConfirmation();
                confirmation.setIsConfirmed(AppConsts.NO);
                confirmation.setIssueId(issue.getId());
                confirmation.setRequestFrom(context.getUserId());
                confirmation.setRequestTo(StringUtils.parseIfIsLong(dto.getConfirmedBy()));
                JPAExecutor.save(confirmation);
            } else if (existConfirmation != null && !IssueConsts.ISSUE_STATUS_PENDING_CONFIRMATION.equals(dto.getStatus())) {
                //delete confirmation
                log.info("Delete Confirmation");
                isNotModified = false;
                createIssueHistory(issueId, existConfirmation.getRequestFrom() + "-" +
                        existConfirmation.getRequestTo(), null, "confirmation");
                JPAExecutor.delete(existConfirmation);

            } else if (existConfirmation != null && StringUtils.isNotBlank(dto.getConfirmedBy())
                    && IssueConsts.ISSUE_STATUS_PENDING_CONFIRMATION.equals(dto.getStatus())) {
                //update confirmation
                log.info("Update Confirmation");
                isNotModified = false;
                createIssueHistory(issueId, existConfirmation.getRequestFrom() + "-" +
                        existConfirmation.getRequestTo(), context.getUserId() + "-" +
                        StringUtils.parseIfIsLong(dto.getConfirmedBy()), "confirmation");
                existConfirmation.setIsConfirmed(AppConsts.NO);
                existConfirmation.setRequestFrom(context.getUserId());
                existConfirmation.setRequestTo(StringUtils.parseIfIsLong(dto.getConfirmedBy()));
                JPAExecutor.update(existConfirmation);
            }
            dto.getAttachments().removeIf(var -> StringUtils.isBlank(var.getOriginalFilename()));
            for (MultipartFile file : dto.getAttachments()) {
                createIssueHistory(issueId, null, file.getOriginalFilename(), "attachments");
                isNotModified = false;
            }
            saveAttachments(dto.getAttachments(), issue);
            if (!isNotModified) {
                JPAExecutor.update(issue);
            } else {
                log.info(LogUtils.around("No update was made", "="));
            }
        }
    }

    @Transactional
    public void reAssign(Long issueId, Long userId) {
        Issue issue = getIssueById(issueId);
        if (issue != null) {
            if (issue.getAssignedTo() == null) {
                issue.setStatus(IssueConsts.ISSUE_STATUS_INVESTIGATION);
                createIssueHistory(issueId, IssueConsts.ISSUE_STATUS_OPEN, issue.getStatus(), "status");
            }
            createIssueHistory(issueId, String.valueOf(issue.getAssignedTo()), String.valueOf(userId), "assignedTo");
            issue.setAssignedTo(userId);
            JPAExecutor.update(issue);
        }
    }

    public Map<String, List<IssueTimelineVO>> retrieveIssueHistory(Long issueId) {
        Issue issue = getIssueById(issueId);
        List<IssueTimelineVO> timelineVOList = new ArrayList<>();
        if (issue != null) {
            List<IssueHistory> histories = issueDAO.getHistoryByIssueId(issueId);
            List<IssueNote> issueNotes = issueDAO.getNotesByIssueId(issueId);
            for (IssueHistory history : histories) {
                IssueTimelineVO timeline = new IssueTimelineVO();
                switch (history.getFieldName()) {
                    case "STATUS":
                        IssueStatus oldStatus = systemMgrService.getIssueStatusByCode(history.getOldValue());
                        IssueStatus newStatus = systemMgrService.getIssueStatusByCode(history.getNewValue());
                        timeline.setOldValue(oldStatus != null ? oldStatus.getDisplayName() : null);
                        timeline.setNewValue(newStatus != null ? newStatus.getDisplayName() : null);
                        break;
                    case "RESOLUTION":
                        IssueResolution oldRes = systemMgrService.getIssueResolutionByCode(history.getOldValue());
                        IssueResolution newRes = systemMgrService.getIssueResolutionByCode(history.getNewValue());
                        timeline.setOldValue(oldRes != null ? oldRes.getDisplayName() : null);
                        timeline.setNewValue(newRes != null ? newRes.getDisplayName() : null);
                        break;
                    case "ASSIGNED_TO":
                        UserAccount oldUser = userAccountService.getUserById(StringUtils.parseIfIsLong(history.getOldValue()));
                        UserAccount newUser = userAccountService.getUserById(StringUtils.parseIfIsLong(history.getNewValue()));
                        timeline.setOldValue(oldUser != null ? oldUser.getName() : null);
                        timeline.setNewValue(newUser != null ? newUser.getName() : null);
                        break;
                    case "PRIORITY":
                        MasterCode oldPriority = MasterCodeUtils.getMasterCode("issue.priority", history.getOldValue());
                        MasterCode newPriority = MasterCodeUtils.getMasterCode("issue.priority", history.getNewValue());
                        timeline.setOldValue(oldPriority.getValue());
                        timeline.setNewValue(newPriority.getValue());
                        break;
                    case "SEVERITY":
                        MasterCode oldSeverity = MasterCodeUtils.getMasterCode("issue.severity", history.getOldValue());
                        MasterCode newSeverity = MasterCodeUtils.getMasterCode("issue.severity", history.getNewValue());
                        timeline.setOldValue(oldSeverity.getValue());
                        timeline.setNewValue(newSeverity.getValue());
                        break;
                    case "ATTACHMENTS":
                        timeline.setNewValue(history.getNewValue());
                        break;
                    case "CONFIRMATION":
                        if (StringUtils.isNotBlank(history.getNewValue())) {
                            String[] newConfirm = history.getNewValue().split("-");
                            timeline.setNewValue(userAccountService.getUserById(StringUtils.parseIfIsLong(newConfirm[0])).getName() + "-" +
                                    userAccountService.getUserById(StringUtils.parseIfIsLong(newConfirm[1])).getName());
                        }
                        if (StringUtils.isNotBlank(history.getOldValue())) {
                            String[] oldConfirm = history.getOldValue().split("-");
                            timeline.setOldValue(userAccountService.getUserById(StringUtils.parseIfIsLong(oldConfirm[0])).getName() + "-" +
                                    userAccountService.getUserById(StringUtils.parseIfIsLong(oldConfirm[1])).getName());
                        }
                        break;
                    case "FOLLOWER":
                        UserAccount oldFollower = userAccountService.getUserById(StringUtils.parseIfIsLong(history.getOldValue()));
                        UserAccount newFollower = userAccountService.getUserById(StringUtils.parseIfIsLong(history.getNewValue()));
                        if (oldFollower != null) {
                            timeline.setOldValue(oldFollower.getName());
                            timeline.setNewValue("关注了该问题");
                        }
                        if (newFollower != null) {
                            timeline.setNewValue(newFollower.getName());
                            timeline.setOldValue("取消关注");
                        }
                        break;
                    default:
                        timeline.setOldValue(history.getOldValue());
                        timeline.setNewValue(history.getNewValue());
                        break;
                }
                timeline.setType(IssueConsts.getTimelineType(history.getFieldName()));
                timeline.setDate(history.getCreatedAt());
                if (StringUtils.isEmpty(history.getOldValue())) {
                    timeline.setAdded(true);
                }
                if (StringUtils.isEmpty(history.getNewValue())) {
                    timeline.setDeleted(true);
                }
                if (StringUtils.isNotEmpty(history.getOldValue()) && StringUtils.isNotEmpty(history.getNewValue())) {
                    timeline.setUpdated(true);
                }
                UserAccount user = userAccountService.getUserByLoginId(history.getCreatedBy());
                if (user != null) {
                    timeline.setBy(user.getName());
                }
                timeline.setAt(DateUtils.getDateString(history.getCreatedAt(), DateUtils.STD_FORMAT_2));
                timelineVOList.add(timeline);
            }
            for (IssueNote note : issueNotes) {
                IssueTimelineVO timeline = new IssueTimelineVO();
                timeline.setAdded(true);
                timeline.setType(IssueConsts.getTimelineType("NOTE"));
                UserAccount user = userAccountService.getUserByLoginId(note.getCreatedBy());
                if (user != null) {
                    timeline.setBy(user.getName());
                }
                timeline.setAt(DateUtils.getDateString(note.getCreatedAt(), DateUtils.STD_FORMAT_2));
                timeline.setOldValue(null);
                timeline.setNewValue(note.getContent());
                timeline.setDate(note.getCreatedAt());
                timelineVOList.add(timeline);
            }
        }
        timelineVOList.sort((Comparator.comparing(IssueTimelineVO::getDate, Comparator.reverseOrder())));
        Map<String, List<IssueTimelineVO>> timelineUnderSameDay = new TreeMap<>(Comparator.reverseOrder());
        for (IssueTimelineVO vo : timelineVOList) {
            String day = DateUtils.getDateString(vo.getDate(), "yyyy/MM/dd");
            timelineUnderSameDay.computeIfAbsent(day, k -> new ArrayList<>());
            timelineUnderSameDay.get(day).add(vo);
        }

        return timelineUnderSameDay;
    }

    public Integer getTodayNewIssueCount(Long projectId) {
        return issueDAO.getTodayNewIssueCount(projectId);
    }

    public Integer getTodayFixedIssueCount(Long projectId) {
        return issueDAO.getTodayFixedIssueCount(projectId);
    }

    public Integer getNotAssignedIssueCount(Long projectId) {
        return issueDAO.getNotAssignedIssueCount(projectId);
    }

    public Integer getReopenedIssueCount(Long projectId) {
        return issueDAO.getReopenedIssueCount(projectId);
    }

    public SearchResult<IssueVO> searchAssignedToMe(SearchParam param) {
        SearchResult<IssueVO> searchResult = new SearchResult<>(issueDAO.searchAssignedToMe(param), param);
        for (IssueVO vo : searchResult.getResultList()) {
            vo.setIssueId(formatIssueId(String.valueOf(vo.getId())));
        }
        searchResult.setTotalCounts(issueDAO.searchAssignedToMeCount(param));
        return searchResult;
    }

    public SearchResult<IssueVO> searchReportedByMe(SearchParam param) {
        SearchResult<IssueVO> searchResult = new SearchResult<>(issueDAO.searchReportedByMe(param), param);
        for (IssueVO vo : searchResult.getResultList()) {
            vo.setIssueId(formatIssueId(String.valueOf(vo.getId())));
        }
        searchResult.setTotalCounts(issueDAO.searchReportedByMeCount(param));
        return searchResult;
    }

    public SearchResult<IssueVO> searchFollowedByMe(SearchParam param) {
        SearchResult<IssueVO> searchResult = new SearchResult<>(issueDAO.searchFollowedByMe(param), param);
        for (IssueVO vo : searchResult.getResultList()) {
            vo.setIssueId(formatIssueId(String.valueOf(vo.getId())));
        }
        searchResult.setTotalCounts(issueDAO.searchFollowedByMeCount(param));
        return searchResult;
    }

    public void followIssue(IssueFollowerDto dto) throws Exception {
        if (dto != null) {
            IssueFollower issueFollower = BeanUtils.mapFromDto(dto, IssueFollower.class);
            if (issueFollower != null) {
                JPAExecutor.save(issueFollower);
                createIssueHistory(StringUtils.parseIfIsLong(dto.getIssueId()), null,
                        dto.getUserAcctId(), "follower");
            }
        }
    }

    public void createRelationShip(IssueRelationshipDto dto) throws Exception {
        if (dto != null) {
            IssueRelationship relationship = BeanUtils.mapFromDto(dto, IssueRelationship.class);
            if (relationship != null) {
                JPAExecutor.save(relationship);
                createIssueHistory(relationship.getSrcIssueId(), null,
                        String.valueOf(relationship.getDestIssueId()), "relationship");
            }
        }
    }

    public boolean terminateRelationship(Long id, Issue issue) {
        IssueRelationship relationship = issueDAO.getRelationshipById(id);
        if (relationship != null && issue != null && relationship.getSrcIssueId().equals(issue.getId())) {
            createIssueHistory(issue.getId(), String.valueOf(relationship.getDestIssueId()),
                    null, "relationship");
            JPAExecutor.delete(relationship);
            return true;
        }
        return false;
    }

    public void cancelFollow(Long issueId, Long userId) {
        IssueFollower follower = issueDAO.getFollowerByIssueIdAndUserId(issueId, userId);
        if (follower != null) {
            JPAExecutor.delete(follower);
            createIssueHistory(issueId, String.valueOf(userId), null, "follower");
        }
    }

    public void createVcs(IssueVcsTrackerDto dto, Issue issue) throws Exception {
        IssueVcsTracker tracker = BeanUtils.mapFromDto(dto, IssueVcsTracker.class);
        if (tracker != null && issue != null) {
            tracker.setIssueId(issue.getId());
            tracker.setProjectId(issue.getProjectId());
            JPAExecutor.save(tracker);
        }
    }

    public void updateVcs(IssueVcsTrackerDto dto) throws Exception {
        IssueVcsTracker tracker = BeanUtils.mapFromDto(dto, IssueVcsTracker.class);
        IssueVcsTracker old = getIssueVcsById(dto.getId());
        if (old != null) {
            old.setBranch(tracker.getBranch());
            old.setBranchVersion(tracker.getBranchVersion());
            old.setFileFullPath(tracker.getFileFullPath());
            old.setMasterVersion(tracker.getMasterVersion());
            old.setTag(tracker.getTag());
            JPAExecutor.update(old);
        }
    }

    public IssueVcsTracker getIssueVcsById(Long id) {
        return issueDAO.getIssueVcsTrackerById(id);
    }

    public double getSLA(Issue issue) {
        if (issue.getFixedAt() == null) {
            return Calculator.divide(DateUtils.getHoursBetween(issue.getReportedAt(), new Date()), 24, 1);
        } else {
            return Calculator.divide(DateUtils.getHoursBetween(issue.getReportedAt(), issue.getFixedAt()), 24, 1);
        }
    }

    public SearchResult<IssueVcsTrackerVO> searchIssueVCS(SearchParam param) {
        if (StringUtils.isNotBlank((String) param.getParams().get("issueId"))) {
            param.getParams().put("issueId", StringUtils.parseIfIsLong((String) param.getParams().get("issueId")));
        }
        SearchResult<IssueVcsTrackerVO> result = new SearchResult<>(issueDAO.searchVcs(param), param);
        result.setTotalCounts(issueDAO.searchVcsCount(param));
        return result;
    }

    private String formatIssueId(String id) {
        if (id.length() < 4) {
            StringBuilder issueId = new StringBuilder(id);
            for (int i = 0; i < 4 - id.length(); i++) {
                issueId.insert(0, "0");
            }
            return issueId.toString();
        }
        return null;
    }

    /**
     * @return isNotModified, if created, return true.
     */
    private boolean createIssueHistory(Long issueId, String oldValue, String newValue, String fieldName) {
        if (StringUtils.isNotBlank(fieldName) && issueId != null
                && !(StringUtils.isBlank(oldValue) && StringUtils.isBlank(newValue)) &&
                ((oldValue != null && !oldValue.trim().equals(newValue)) || (!newValue.equals(oldValue)))) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issueId);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setFieldName(NameConvertor.getColumn(fieldName));
            JPAExecutor.save(history);
            return false;
        }
        return true;
    }

    private void saveAttachments(List<MultipartFile> attachments, Issue issue) throws Exception {
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                Project project = projectService.getProjectById(issue.getProjectId(), false);
                if (project != null) {
                    FileNet fileNet = fileNetService.saveFileNetLocal(file.getOriginalFilename(), file.getBytes(),
                            SystemConsts.ROOT_STORAGE_PATH + project.getFilePath(), null);
                    if (fileNet != null) {
                        IssueAttachment issueAttachment = new IssueAttachment();
                        issueAttachment.setIssueId(issue.getId());
                        issueAttachment.setFileNetId(fileNet.getId());
                        issueAttachment.setFileName(file.getOriginalFilename());
                        JPAExecutor.save(issueAttachment);
                    }
                }
            }
        }
    }
}
