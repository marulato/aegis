package org.legion.aegis.admin.service;

import org.legion.aegis.admin.dao.ModuleDAO;
import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.common.base.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
