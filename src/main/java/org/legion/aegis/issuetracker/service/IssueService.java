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
import org.legion.aegis.issuetracker.consts.IssueConsts;
import org.legion.aegis.issuetracker.dao.IssueDAO;
import org.legion.aegis.issuetracker.dto.IssueDto;
import org.legion.aegis.issuetracker.entity.*;
import org.legion.aegis.issuetracker.vo.IssueConfirmationVO;
import org.legion.aegis.issuetracker.vo.IssueNoteVO;
import org.legion.aegis.issuetracker.vo.IssueTimelineVO;
import org.legion.aegis.issuetracker.vo.IssueVO;
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

    @Autowired
    public IssueService(IssueDAO issueDAO, SystemMgrService systemMgrService,
                        FileNetService fileNetService, ProjectService projectService, UserAccountService userAccountService) {
        this.issueDAO = issueDAO;
        this.systemMgrService = systemMgrService;
        this.fileNetService = fileNetService;
        this.projectService = projectService;
        this.userAccountService = userAccountService;
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
            vo.setSla(String.valueOf(Calculator.divide(DateUtils.getHoursBetween(
                    DateUtils.parseDatetime(vo.getReportedAt()), new Date()), 24, 1)));
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
        }
    }

    public Issue getIssueById(Long id) {
        return issueDAO.getIssueById(id);
    }

    public IssueVO getIssueVOForView(Issue issue) {
        if (issue != null) {
            IssueVO vo = new IssueVO();
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
                noteVO.setCreatedBy(note.getCreatedBy());
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
        if (dto != null && issue != null) {
            createIssueHistory(issueId, issue.getStatus(), dto.getStatus(), "status");
            issue.setStatus(dto.getStatus());

            createIssueHistory(issueId, issue.getRootCause(), dto.getRootCause(), "rootCause");
            issue.setRootCause(dto.getRootCause());

            createIssueHistory(issueId, issue.getResolution(), dto.getResolution(), "resolution");
            issue.setResolution(dto.getResolution());

            createIssueHistory(issueId, issue.getSeverity(), dto.getSeverity(), "severity");
            issue.setSeverity(dto.getSeverity());

            createIssueHistory(issueId, issue.getPriority(), dto.getPriority(), "priority");
            issue.setPriority(dto.getPriority());

            createIssueHistory(issueId, DateUtils.getDateString(issue.getFixedAt(), "yyyy/MM/dd HH:mm"),
                    dto.getFixedAt(), "fixedAt");
            issue.setFixedAt(DateUtils.parseDatetime(dto.getFixedAt(), "yyyy/MM/dd HH:mm"));

            if (StringUtils.isNotBlank(dto.getUpdatedNote())) {
                IssueNote issueNote = new IssueNote();
                issueNote.setIssueId(issue.getId());
                issueNote.setContent(dto.getUpdatedNote());
                JPAExecutor.save(issueNote);
            }

            if (StringUtils.isNotBlank(dto.getConfirmedBy())) {
                IssueConfirmation existConfirmation = issueDAO.getIssueConfirmationByIssueId(issueId);
                AppContext context = AppContext.getFromWebThread();
                if (existConfirmation == null) {
                    createIssueHistory(issueId, null, context.getUserId() + "-" +
                            StringUtils.parseIfIsLong(dto.getConfirmedBy()), "confirmation");
                    IssueConfirmation confirmation = new IssueConfirmation();
                    confirmation.setIsConfirmed(AppConsts.NO);
                    confirmation.setIssueId(issue.getId());
                    confirmation.setRequestFrom(context.getUserId());
                    confirmation.setRequestTo(StringUtils.parseIfIsLong(dto.getConfirmedBy()));
                    JPAExecutor.save(confirmation);
                } else  {
                    createIssueHistory(issueId, existConfirmation.getRequestFrom() + "-" +
                            existConfirmation.getRequestTo(), context.getUserId() + "-" +
                            StringUtils.parseIfIsLong(dto.getConfirmedBy()), "confirmation");
                    existConfirmation.setIsConfirmed(AppConsts.NO);
                    existConfirmation.setRequestFrom(context.getUserId());
                    existConfirmation.setRequestTo(StringUtils.parseIfIsLong(dto.getConfirmedBy()));
                    JPAExecutor.update(existConfirmation);
                }
            }
            for (MultipartFile file : dto.getAttachments()) {
                createIssueHistory(issueId, null, file.getOriginalFilename(), "attachments");
            }
            saveAttachments(dto.getAttachments(), issue);
            JPAExecutor.update(issue);
        }
    }

    @Transactional
    public void reAssign(Long issueId, Long userId) {
        Issue issue = getIssueById(issueId);
        if (issue != null) {
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
            IssueConfirmation confirmation = issueDAO.getIssueConfirmationByIssueId(issueId);
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
                        String[] newConfirm = history.getNewValue().split("-");
                        if (StringUtils.isNotBlank(history.getOldValue())) {
                            String[] oldConfirm = history.getOldValue().split("-");
                            timeline.setOldValue(userAccountService.getUserById(StringUtils.parseIfIsLong(oldConfirm[0])).getName() + "-" +
                                    userAccountService.getUserById(StringUtils.parseIfIsLong(oldConfirm[1])).getName());
                        }
                        timeline.setNewValue(userAccountService.getUserById(StringUtils.parseIfIsLong(newConfirm[0])).getName() + "-" +
                                userAccountService.getUserById(StringUtils.parseIfIsLong(newConfirm[1])).getName());
                        break;
                    default:
                        timeline.setOldValue(history.getOldValue());
                        timeline.setNewValue(history.getNewValue());
                        break;
                }
                timeline.setType(getTimelineType(history.getFieldName()));
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
                timeline.setType(getTimelineType("NOTE"));
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

    private void createIssueHistory(Long issueId, String oldValue, String newValue, String fieldName) {
        if (StringUtils.isNotBlank(fieldName) && issueId != null
                && !(StringUtils.isBlank(oldValue) && StringUtils.isBlank(newValue)) &&
                ((oldValue != null && !oldValue.trim().equals(newValue)) || (!newValue.equals(oldValue)))) {
            IssueHistory history = new IssueHistory();
            history.setIssueId(issueId);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
            history.setFieldName(NameConvertor.getColumn(fieldName));
            JPAExecutor.save(history);
        }
    }

    private void saveAttachments(List<MultipartFile> attachments, Issue issue) throws Exception {
        if (attachments != null) {
            for (MultipartFile file : attachments) {
                Project project = projectService.getProjectById(issue.getProjectId(), false);
                if (project != null) {
                    FileNet fileNet = fileNetService.saveFileNetLocal(file.getOriginalFilename(), file.getBytes(),
                            SystemConsts.ROOT_STORAGE_PATH + project.getFilePath());
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

    private String getTimelineType(String fieldName) {
        String type;
        switch (fieldName) {
            case "STATUS":
                type = "问题状态";
                break;
            case "RESOLUTION":
                type = "解决状态";
                break;
            case "ROOT_CAUSE":
                type = "问题原因";
                break;
            case "FIXED_AT":
                type = "解决时间";
                break;
            case "ASSIGNED_TO":
                type = "转发";
                break;
            case "NOTE":
                type = "备注";
                break;
            case "PRIORITY":
                type = "优先级";
                break;
            case "CONFIRMATION":
                type = "等待确认";
                break;
            case "ATTACHMENTS":
                type = "附件";
                break;
            default:
                type = "Unknown";
        }
        return type;
    }

}
