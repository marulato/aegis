package org.legion.aegis.admin.service;

import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.entity.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectDAO projectDAO;

    @Autowired
    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public List<Project> getAllProjects() {
        return projectDAO.getAllProjects();
    }
}
