package org.legion.aegis.admin.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.legion.aegis.admin.controller.UserAccountController;
import org.legion.aegis.admin.dao.UserAccountDAO;
import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.generator.NewUserEmailGenerator;
import org.legion.aegis.admin.vo.UserAccountVO;
import org.legion.aegis.admin.vo.UserLoginHistoryVO;
import org.legion.aegis.admin.vo.UserProjectVO;
import org.legion.aegis.admin.vo.UserSearchVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.CollectionUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.MiscGenerator;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.legion.aegis.general.service.ExternalEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
public class UserAccountService {

    private final UserAccountDAO userAccountDAO;
    private final ExternalEmailService emailService;
    private final ProjectService projectService;

    @Autowired
    public UserAccountService(UserAccountDAO userAccountDAO, ExternalEmailService emailService, ProjectService projectService) {
        this.userAccountDAO = userAccountDAO;
        this.emailService = emailService;
        this.projectService = projectService;
    }

    public static boolean isDevQA(String roleId) {
        return AppConsts.ROLE_DEV.equals(roleId) || AppConsts.ROLE_QA.equals(roleId);
    }

    public static boolean isSupervisor(String roleId) {
        return AppConsts.ROLE_DEV_SUPERVISOR.equals(roleId) || AppConsts.ROLE_QA_SUPERVISOR.equals(roleId);
    }

    public String encryptPassword(String pwd) {
        if (StringUtils.isNotBlank(pwd)) {
            String hash = pwd;
            for (int i = 0; i < 5; i++) {
                hash = DigestUtils.sha256Hex(hash);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.encode(hash);
        }
        return pwd;
    }

    public boolean isPasswordMatch(String plaintext, String ciphertext) {
        if (StringUtils.isNotEmpty(plaintext) && StringUtils.isNotEmpty(ciphertext)) {
            String hash = plaintext;
            for (int i = 0; i < 5; i++) {
                hash = DigestUtils.sha256Hex(hash);
            }
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            return encoder.matches(hash, ciphertext);
        }
        return false;
    }

    public boolean isActive(UserAccount user) {
        if (user != null) {
            long now = DateUtils.now().getTime();
            return user.getActivatedAt().getTime() <= now && user.getDeactivatedAt().getTime() >= now;
        }
        return false;
    }

    public UserAccount getUserByLoginId(String loginId) {
        if (StringUtils.isNotBlank(loginId)) {
            return userAccountDAO.getUserByLoginId(loginId);
        }
        return null;
    }

    public UserAccount getUserById(Long id) {
        return userAccountDAO.getUserById(id);
    }

    public List<UserProjectAssign> getUserProjectAssignments(Long userId) {
        return userAccountDAO.getUserProjectAssignments(userId);
    }

    public List<UserRoleAssign> getActiveRoleAssignById(Long userAcctId) {
        if (userAcctId != null && userAcctId > 0) {
            return userAccountDAO.getActiveUserRoleAssignment(userAcctId);
        }
        return null;
    }

    public List<UserRoleAssign> getRoleAssigments(Long userId) {
        return userAccountDAO.getUserRoleAssignment(userId);
    }

    public UserRole getRoleById(String roleId) {
        if (StringUtils.isNotBlank(roleId)) {
            return userAccountDAO.getRoleById(roleId);
        }
        return null;
    }

    public List<UserRole> getAllRoles() {
        return userAccountDAO.getAllRoles();
    }
    public List<UserRole> getRoleSelectorForSearch(AppContext context) {
        List<UserRole> roles = userAccountDAO.getAllRoles();
        UserRole userRole = new UserRole();
        userRole.setId("0");
        userRole.setRoleName("请选择");
        roles.add(0, userRole);
        return roles;
    }


    public UserAccount createDefaultUser(UserAccount account) {
        if (account != null) {
            String pwd = null;
            if (account.getActivatedAt() == null) {
                account.setActivatedAt(DateUtils.today());
            }
            if (account.getDeactivatedAt() == null) {
                account.setDeactivatedAt(DateUtils.addYear(DateUtils.today(), 3));
            }
            if (StringUtils.isBlank(account.getPassword())) {
                pwd = MiscGenerator.generateInitialPassword();
                account.setPassword(encryptPassword(pwd));
            }
            account.setIsFirstLogin(AppConsts.YES);
            account.setIsNeedChangePwd(AppConsts.YES);
            account.setStatus(AppConsts.ACCOUNT_STATUS_ACTIVE);
            account.setLoginFailedTimes(0);
            JPAExecutor.save(account);
            UserAccount newUser = getUserByLoginId(account.getLoginId());
            newUser.setOriginalPwd(pwd);
            return newUser;
        }
        return null;
    }

    public void assignRoles(UserAccount account, List<UserRole> roles) {
        if (account != null && !CollectionUtils.isEmpty(roles)) {
            UserAccount user = userAccountDAO.getUserById(account.getId());
            if (user == null) {
                user = getUserByLoginId(account.getLoginId());
            }
            if (user != null) {
                for (UserRole role : roles) {
                    UserRoleAssign assign = new UserRoleAssign();
                    assign.setRoleId(role.getId());
                    assign.setUserAcctId(account.getId());
                    assign.setApprovedBy(AppContext.getFromWebThread().getLoginId());
                    assign.setActivatedAt(account.getActivatedAt());
                    assign.setDeactivatedAt(account.getDeactivatedAt());
                    JPAExecutor.save(assign);
                }
            }
        }
    }

    @Transactional
    public UserAccount createUser(UserAccount userAccount, List<UserRole> roles, String[] projectOrGroup) {
        if (userAccount != null && !CollectionUtils.isEmpty(roles) && projectOrGroup != null && projectOrGroup.length > 0) {
            UserAccount newUser = createDefaultUser(userAccount);
            if (newUser != null) {
                assignRoles(newUser, roles);
                for (UserRole role : roles) {
                    //assign group
                    if (isSupervisor(role.getId())) {
                        for (String groupId : projectOrGroup) {
                            ProjectGroup group = projectService.getProjectGroupById(StringUtils.parseIfIsLong(groupId));
                            if (group != null) {
                                UserProjectAssign projectAssign = new UserProjectAssign();
                                projectAssign.setGroupId(group.getId());
                                projectAssign.setUserAcctId(newUser.getId());
                                projectAssign.setAssignReason("New user created");
                                JPAExecutor.save(projectAssign);
                            }
                        }
                        //assign project
                    } else if (isDevQA(role.getId())) {
                        Set<Project> projectsToAssign = new HashSet<>();
                        for (String projectId : projectOrGroup) {
                            Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), false);
                            projectsToAssign.add(project);
                            List<Project> projectList = projectService.getProjectsUnderGroup(project.getGroupId());
                            projectList.removeIf(project1 -> AppConsts.NO.equals(project1.getIsPublic()));
                            projectsToAssign.addAll(projectList);
                        }
                        assignProjects(projectsToAssign, newUser.getId());
                    }
                }

                return newUser;
            }
        }
        return null;
    }

    public void sendAcknowledgementEmail(UserAccount account) throws Exception {
        if (account != null) {
            NewUserEmailGenerator generator = new NewUserEmailGenerator(account);
            emailService.sendEmail(new String[]{account.getEmail()}, null, generator.getSubject(), generator.getEmailContent());
        }
    }

    public SearchResult<UserSearchVO> searchUsers(SearchParam searchParam) {
        List<UserSearchVO> userSearchVOS = userAccountDAO.search(searchParam);
        SearchResult<UserSearchVO> searchResult = new SearchResult<>(userSearchVOS, searchParam);
        searchResult.setTotalCounts(userAccountDAO.searchCount(searchParam));
        for (UserSearchVO vo : userSearchVOS) {
            if (isSupervisor(vo.getRoleId())) {
                List<ProjectGroup> groups = projectService.getProjectGroupUnderUser(vo.getId(), vo.getRoleId());
                List<String> groupNames = new ArrayList<>();
                for (ProjectGroup group : groups) {
                    groupNames.add(group.getName());
                }
                if (groupNames.size() <= 3) {
                    vo.setProject(Joiner.on("<br/>").join(groupNames));
                } else {
                    vo.setProject(Joiner.on("<br/>").join(CollectionUtils.subList(groupNames, 0, 3)) + "...");
                }
            } else if (isDevQA(vo.getRoleId())) {
                List<Project> projectList = projectService.getProjectsForDevAndQA(vo.getId());
                List<String> projectNames = new ArrayList<>();
                for (Project project : projectList) {
                    projectNames.add(project.getName());
                }
                if (projectNames.size() <= 3) {
                    vo.setProject(Joiner.on("<br/>").join(projectNames));
                } else {
                    vo.setProject(Joiner.on("<br/>").join(CollectionUtils.subList(projectNames, 0, 3)) +
                            "<br/>...（" + "+另外" + (projectNames.size() - 3) + "个）");
                }
            }
            vo.setProject(StringUtils.getNonEmpty(vo.getProject()));
        }
        return searchResult;
    }


    public List<UserProjectVO> searchUserProjects(Long userId, String role) {
        return userAccountDAO.searchUserProjects(userId, role);
    }

    public UserAccountVO searchUserInfo(Long userId) {
        return userAccountDAO.searchUserInfo(userId);
    }

    public void verifyRequest(Long userId) {
        if (userId != null) {
            AppContext context = AppContext.getFromWebThread();
            if (AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
                return;
            } else {
                List<UserAccount> accounts = userAccountDAO.getUserUnderSupervisor(context.getUserId());
                for (UserAccount account : accounts) {
                    if (account.getId().equals(userId)) {
                        return;
                    }
                }
                throw new PermissionDeniedException();
            }
        }
    }


    public List<Project> prepareProjectSelectorForModify(Long userId) {
        AppContext context = AppContext.getFromWebThread();
        UserRoleAssign roleAssign = userAccountDAO.getUserRoleAssignment(userId).get(0);
        List<Project> selector = new ArrayList<>();
        if (isDevQA(roleAssign.getRoleId())) {
            List<Project> allProjects = projectService.getProjectsUnderSupervisor(context.getUserId(), context.getRoleId());
            List<Project> assignedProjects = projectService.getProjectsForDevAndQA(userId);
            Iterator<Project> iterator = allProjects.iterator();
            while (iterator.hasNext()) {
                Project current = iterator.next();
                //remove projects which have been already assigned
                if (assignedProjects.contains(current)) {
                    iterator.remove();
                } else {
                    ProjectGroup group = projectService.getProjectGroupById(current.getGroupId());
                    current.setName(group.getName() + " - " + current.getName());
                    selector.add(current);
                }
            }

        }
        selector.sort(Comparator.comparing(Project::getName));
        return selector;
    }

    public List<ProjectGroup> prepareGroupSelectorForModify(Long userId) {
        AppContext context = AppContext.getFromWebThread();
        UserRoleAssign roleAssign = userAccountDAO.getUserRoleAssignment(userId).get(0);
        List<ProjectGroup> groups = projectService.getAllProjectGroup();
        if (isSupervisor(roleAssign.getRoleId())) {
            List<ProjectGroup> assignedGroups = projectService.getProjectGroupUnderUser(userId, roleAssign.getRoleId());
            groups.removeIf(assignedGroups::contains);
        } else {
            groups.clear();
        }
        groups.sort(Comparator.comparing(ProjectGroup::getName));
        return groups;
    }

    @Transactional
    public void removeProjectAssignment(Long userId, Long projectId) {
        List<Long> idList = (List) SessionManager.getAttribute(UserAccountController.PROJECT_KEY);
        AppContext context = AppContext.getFromWebThread();
        UserProjectAssign assign = userAccountDAO.getProjectAssignmentByUserIdAndProjectId(userId, projectId);
        if (assign != null && assign.getUserAcctId().equals(userId) && (idList.contains(projectId) || context.isAdminRole())) {
            userAccountDAO.deleteProjectAssign(assign.getUserAcctId(), assign.getProjectId());
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Transactional
    public void removeGroup(Long userId, Long groupId) {
        List<Long> idList = (List) SessionManager.getAttribute(UserAccountController.GROUP_KEY);
        AppContext context = AppContext.getFromWebThread();
        UserRoleAssign roleAssign = userAccountDAO.getUserRoleAssignment(userId).get(0);
        List<UserProjectAssign> assigns = userAccountDAO.getUserProjectAssignmentsByUserIdAndGroup(userId, groupId);
        if (assigns != null && (idList.contains(groupId) || context.isAdminRole())) {
            for (UserProjectAssign assign : assigns) {
                userAccountDAO.deleteGroupAssign(assign.getUserAcctId(), assign.getGroupId());
            }
        }

    }

    @Transactional
    public void updateUser(UserDto userDto) {
        if (userDto != null) {
            UserAccount userAccount = getUserById(StringUtils.parseIfIsLong(userDto.getUserId()));
            UserRoleAssign roleAssign = userAccountDAO.getUserRoleAssignment(userAccount.getId()).get(0);
            if (userAccount != null) {
                userAccount.setName(userDto.getName());
                userAccount.setEmail(userDto.getEmail());
                userAccount.setDeactivatedAt(userDto.getDeactivatedAt());
                JPAExecutor.update(userAccount);
                if (isDevQA(roleAssign.getRoleId()) && userDto.getProject() != null) {
                    List<Project> assignedProjects = projectService.getProjectsForDevAndQA(userAccount.getId());
                    Set<Project> needToAssign = new HashSet<>();
                    for (String pId : userDto.getProject()) {
                        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(pId), false);
                        if (assignedProjects.contains(project) || project == null) {
                            continue;
                        } else {
                            needToAssign.add(project);
                            //all public projects under this specific group should be automatic assigned
                            List<Project> projectList = projectService.getProjectsUnderGroup(project.getGroupId());
                            projectList.removeIf(project1 -> AppConsts.NO.equals(project1.getIsPublic())
                                    || assignedProjects.contains(project1));
                            needToAssign.addAll(projectList);

                        }
                    }
                    assignProjects(needToAssign, userAccount.getId());
                } else if (userDto.getProject() != null){
                    List<ProjectGroup> assignedGroups = projectService.getProjectGroupUnderUser(userAccount.getId(), roleAssign.getRoleId());
                    for (String pId : userDto.getProject()) {
                        ProjectGroup group = projectService.getProjectGroupById(StringUtils.parseIfIsLong(pId));
                        if (assignedGroups.contains(group) || group == null) {
                            break;
                        } else {
                            UserProjectAssign projectAssign = new UserProjectAssign();
                            projectAssign.setUserAcctId(userAccount.getId());
                            projectAssign.setGroupId(group.getId());
                            projectAssign.setAssignReason("User Modified");
                            JPAExecutor.save(projectAssign);
                        }
                    }
                }
            }
        }
    }

    public void resetPassword(UserAccount userAccount, String newPwd) {
        if (userAccount != null && StringUtils.isNotBlank(newPwd)) {
            userAccount.setPassword(encryptPassword(newPwd));
            userAccount.setLoginFailedTimes(0);
            userAccount.setStatus(AppConsts.ACCOUNT_STATUS_ACTIVE);
            JPAExecutor.update(userAccount);
        }
    }

    public List<UserAccount> getReportersUnderProject(Long projectId) {
        List<UserAccount> reporters = userAccountDAO.getUsersUnderProject(projectId, AppConsts.ROLE_QA);
        List<UserAccount> supervisors = userAccountDAO.getUsersUnderProject(projectId, AppConsts.ROLE_QA_SUPERVISOR);
        reporters.addAll(supervisors);
        return reporters;
    }

    public List<UserAccount> getDevelopersUnderProject(Long projectId) {
        List<UserAccount> devs = userAccountDAO.getUsersUnderProject(projectId, AppConsts.ROLE_DEV);
        List<UserAccount> supervisors = userAccountDAO.getUsersUnderProject(projectId, AppConsts.ROLE_DEV_SUPERVISOR);
        devs.addAll(supervisors);
        return devs;
    }

    public SearchResult<UserLoginHistoryVO> searchLoginHistory(SearchParam param) {
        SearchResult<UserLoginHistoryVO> searchResult = new SearchResult<>(userAccountDAO.searchLoginHistory(param), param);
        int i = (param.getPageNo() - 1) * param.getPageSize();
        for (UserLoginHistoryVO vo : searchResult.getResultList()) {
            vo.setBrowser(StringUtils.getBrowser(vo.getBrowser()));
            vo.setIndex(++i);
        }
        searchResult.setTotalCounts(userAccountDAO.searchLoginHistoryCount(param));
        return searchResult;
    }

    public boolean isProjectAccessible(Long userId, Long projectId) {
        List<UserProjectAssign> assigns = getUserProjectAssignments(userId);
        AppContext  context = AppContext.getFromWebThread();
        for (UserProjectAssign assign : assigns) {
            if (assign.getProjectId() != null && assign.getProjectId().equals(projectId)) {
                return true;
            }
            if (assign.getProjectId() == null && assign.getGroupId() != null) {
                List<Project> projects = projectService.getProjectsUnderGroup(assign.getGroupId());
                for (Project project : projects) {
                    if (project.getId().equals(projectId)) {
                        return true;
                    }
                }
            }
        }
        return AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId());
    }

    private void assignProjects(Collection<Project> projects, Long userId) {
        if (projects != null && userId != null) {
            for (Project var : projects) {
                UserProjectAssign projectAssign = new UserProjectAssign();
                projectAssign.setUserAcctId(userId);
                projectAssign.setGroupId(var.getGroupId());
                projectAssign.setProjectId(var.getId());
                projectAssign.setAssignReason("User Modified");
                JPAExecutor.save(projectAssign);
            }
        }
    }
}
