package org.legion.aegis.admin.service;

import org.legion.aegis.admin.dao.ModuleDAO;
import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.dto.ProjectDto;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserProjectAssign;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectDAO projectDAO;
    private final ModuleDAO moduleDAO;

    @Autowired
    public ProjectService(ProjectDAO projectDAO, ModuleDAO moduleDAO) {
        this.projectDAO = projectDAO;
        this.moduleDAO = moduleDAO;
    }

    public List<Project> getAllProjects() {
        return projectDAO.getAllProjects();
    }

    public List<Project> getAllProjectsForSearchSelector(Long userId) {
        List<Project> projects = projectDAO.getProjectsUnderUser(userId);
        Project project = new Project();
        project.setId(0L);
        project.setName("请选择");
        projects.add(0, project);
        return projects;
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

    public List<Project> search(SearchParam param) {
        return projectDAO.search(param);
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
                UserProjectAssign projectAssign = new UserProjectAssign();
                projectAssign.setProjectId(project.getId());
                projectAssign.setUserAcctId(context.getUserId());
                projectAssign.setAssignReason("New Project Created by " + context.getLoginId());
                JPAExecutor.save(projectAssign);
            }
        }
    }

    public List<ProjectDto> toDtoView(List<Project> projects) throws Exception {
        List<ProjectDto> dtoList = new ArrayList<>();
        if (projects != null) {
            for (Project project : projects) {
                ProjectDto dto = toDtoView(project);
                if (dto != null) {
                    dtoList.add(dto);
                }
            }
        }
        return dtoList;
    }

    public ProjectDto toDtoView(Project project) throws Exception {
        if (project != null) {
            ProjectDto dto = BeanUtils.mapDtoFromPO(project, ProjectDto.class, "yyyy/MM/dd");
            if (dto != null) {
                if (AppConsts.STATUS_ACTIVE.equals(project.getStatus())) {
                    dto.setStatusCode(AppConsts.STATUS_ACTIVE_CHN);
                } else if (AppConsts.STATUS_BANNED.equals(project.getStatus())) {
                    dto.setStatusCode(AppConsts.STATUS_BANNED_CHN);
                }
                if (AppConsts.YES.equals(project.getIsPublic())) {
                    dto.setIsPublic("公开");
                } else {
                    dto.setIsPublic("私有");
                }
                return dto;
            }
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

}
