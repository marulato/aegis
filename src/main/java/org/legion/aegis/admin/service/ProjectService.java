package org.legion.aegis.admin.service;

import org.legion.aegis.admin.dao.ModuleDAO;
import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.dto.ProjectDto;
import org.legion.aegis.admin.dto.ProjectGroupDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.vo.ProjectGroupVO;
import org.legion.aegis.admin.vo.ProjectVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final ModuleDAO moduleDAO;

    @Autowired
    public ProjectService(ProjectDAO projectDAO, ModuleDAO moduleDAO) {
        this.projectDAO = projectDAO;
        this.moduleDAO = moduleDAO;
    }

    public List<Project> getAllProjects(boolean onlyActive, boolean onlyPrivate) {
        List<Project> projectList = projectDAO.getAllProjects();
        if (onlyActive) {
            projectList.removeIf(project -> AppConsts.STATUS_BANNED.equals(project.getStatus()));
        }
        if (onlyPrivate) {
            projectList.removeIf(project -> AppConsts.YES.equals(project.getIsPublic()));
        }
        return projectList;
    }

    public List<Project> getAllProjectsForSearchSelector(Long userId, String roleId) {
        List<Project> projects = projectDAO.getAllProjectsUnderUser(userId, roleId);
        for (Project project : projects) {
            ProjectGroup projectGroup = projectDAO.getProjectGroupById(project.getGroupId());
            project.setName(projectGroup.getName() + " - " + project.getName());
        }
        projects.sort(Comparator.comparing(Project::getName));
        Project project = new Project();
        project.setId(0L);
        project.setName("请选择");
        projects.add(0, project);
        return projects;
    }

    public List<Project> getProjectsForDevAndQA(Long userId) {
        return projectDAO.getAllProjectsUnderUser(userId, null);
    }

    public List<Project> getProjectsUnderSupervisor(Long userId, String roleId) {
        return projectDAO.getAllProjectsUnderUser(userId, roleId);
    }

    public List<Project> getProjectsByIsPublic(String isPublic) {
        return projectDAO.getProjectsByIsPublic(isPublic);
    }

    public Project getProjectById(Long id, Boolean loadModules) {
        Project project = projectDAO.getProjectById(id);
        if (project != null) {
            if (loadModules) {
                project.setModules(moduleDAO.getModulesByProjectId(project.getId()));
            }
        }
        return project;
    }

    public SearchResult<ProjectVO> search(SearchParam param) {
        SearchResult<ProjectVO> searchResult = new SearchResult<>(projectDAO.search(param), param);
        searchResult.setTotalCounts(projectDAO.searchProjectCount(param));
        return searchResult;
    }

    public Module getModuleById(Long id) {
        return moduleDAO.getModuleById(id);
    }

    public List<Module> getModulesByProjectId(Long projectId) {
        return moduleDAO.getModulesByProjectId(projectId);
    }

    public void updateProject(ProjectDto dto) throws Exception {
        if (dto != null) {
            Project project = getProjectById(StringUtils.parseIfIsLong(dto.getProjectId()), false);
            if (project != null) {
                project.setName(dto.getName());
                project.setDescription(dto.getDescription());
                project.setStage(dto.getStage());
                //project.setFilePath(dto.getFilePath());
                project.setIsPublic(dto.getIsPublic());
                JPAExecutor.update(project);
            }
        }
    }

    @Transactional
    public void saveNewProject(ProjectDto dto, AppContext context) throws Exception {
        if (dto != null && context != null) {
            Project project = BeanUtils.mapFromDto(dto, Project.class);
            if (project != null) {
                project.setStatus(AppConsts.STATUS_ACTIVE);
                project.setGroupId(Long.parseLong(dto.getGroup()));
                project.createAuditValues(AppContext.getFromWebThread());
                projectDAO.createProject(project);
                if (dto.getModule() != null) {
                    for (String module : dto.getModule()) {
                        String[] details = module.split("&&");
                        if (details.length >= 1) {
                            Module mod = new Module();
                            mod.setProjectId(project.getId());
                            mod.setName(details[0]);
                            if (details.length == 2) {
                                mod.setDescription(details[1]);
                            }
                            JPAExecutor.save(mod);
                        }
                    }
                }
            }
        }
    }

    public List<ProjectVO> toProjectView(List<Project> projects) throws Exception {
        List<ProjectVO> dtoList = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                ProjectVO dto = toProjectView(project);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    public ProjectVO toProjectView(Project project) throws Exception {
        if (project != null) {
            ProjectVO vo = new ProjectVO(project);
            AppContext context = AppContext.getFromWebThread();
            vo.setRole(context.getCurrentRole().getId());
            if (AppConsts.STATUS_ACTIVE.equals(project.getStatus())) {
                vo.setStatusDesc(AppConsts.STATUS_ACTIVE_CHN);
            } else if (AppConsts.STATUS_BANNED.equals(project.getStatus())) {
                vo.setStatusDesc(AppConsts.STATUS_BANNED_CHN);
            }
            if (AppConsts.YES.equals(project.getIsPublic())) {
                vo.setIsPublic("公开");
            } else {
                vo.setIsPublic("私有");
            }
            return vo;
        }
        return null;
    }

    public boolean isPathAvailable(String path) {
        List<Project> projectList = projectDAO.getProjectsByPath(path);
        return projectList.isEmpty();
    }

    public String generateRandomDir() {
        UUID uuid = UUID.randomUUID();
        List<Project> project = projectDAO.getProjectsByPath(uuid.toString());
        while (!project.isEmpty()) {
            uuid = UUID.randomUUID();
            project = projectDAO.getProjectsByPath(uuid.toString());
        }
        return uuid.toString();
    }

    public void banProject(Project project) {
        if (project != null) {
            project.setStatus(AppConsts.STATUS_BANNED);
            JPAExecutor.update(project);
        }
    }

    public void activateProject(Project project) {
        if (project != null) {
            project.setStatus(AppConsts.STATUS_ACTIVE);
            JPAExecutor.update(project);
        }
    }

    public SearchResult<ProjectGroupVO> searchGroup(SearchParam param) {
        SearchResult<ProjectGroupVO> searchResult = new SearchResult<>(projectDAO.searchProjectGroup(param), param);
        searchResult.setTotalCounts(projectDAO.searchGroupCount(param));
        return searchResult;
    }

    public void saveProjectGroup(ProjectGroup group) {
        if (group != null && group.getId() != null && group.getId() > 0) {
            ProjectGroup projectGroup = getProjectGroupById(group.getId());
            projectGroup.setName(group.getName());
            projectGroup.setDescription(group.getDescription());
            JPAExecutor.update(projectGroup);
        } else if (group != null) {
            JPAExecutor.save(group);
        }
    }

    public ProjectGroup getProjectGroupById(Long id) {
        return projectDAO.getProjectGroupById(id);
    }

    public List<Project> getProjectsUnderGroup(Long groupId) {
        return projectDAO.getProjectsUnderGroup(groupId);
    }

    public List<ProjectGroup> getAllProjectGroup() {
        return projectDAO.getProjectGroupUnderUser(null, AppConsts.ROLE_SYSTEM_ADMIN);
    }

    public List<ProjectGroup> getProjectGroupUnderUser(Long userId, String role) {
        return projectDAO.getProjectGroupUnderUser(userId, role);
    }

    public void verifyRequest(Long projectId, AppContext context) {
        if (context != null && projectId != null) {
            Project requested = getProjectById(projectId, false);
            List<Project> projectList = projectDAO.getAllProjectsUnderUser(context.getUserId(), context.getRoleId());
            if (!projectList.contains(requested)) {
                throw new PermissionDeniedException("User [" + context.getLoginId()
                        + "] tried to access INACCESSIBLE Project ID [" + requested + "]");
            }

        }
    }

    public List<Project> getProjectSelector() {
        List<Project> projectList = getAllProjects(true, false);
        for (Project project : projectList) {
            ProjectGroup group = getProjectGroupById(project.getGroupId());
            project.setName(group.getName() + " - " + project.getName());
        }
        projectList.sort(Comparator.comparing(Project::getName));
        return projectList;
    }
}
