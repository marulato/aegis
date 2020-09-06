package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.*;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.vo.ProjectGroupVO;
import org.legion.aegis.admin.vo.ProjectVO;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import java.util.List;

@Mapper
public interface ProjectDAO {

    @Select("SELECT * FROM PJT_PROJECT")
    List<Project> getAllProjects();

    @Select("SELECT * FROM PJT_PROJECT WHERE ID = #{id}")
    Project getProjectById(Long id);

    @Select("SELECT * FROM PJT_PROJECT WHERE FILE_PATH = #{path}")
    List<Project> getProjectsByPath(String path);

    List<ProjectVO> search(@Param("sp") SearchParam param);

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyColumn = "ID", keyProperty = "id")
    void createProject(Project project);

    @Select("SELECT * FROM PJT_PROJECT WHERE IS_PUBLIC = #{isPublic}")
    List<Project> getProjectsByIsPublic(String isPublic);

    List<Project> getAllProjectsUnderUser(Long userId, String roleId);

    List<ProjectGroupVO> searchProjectGroup(@Param("sp") SearchParam param);

    @Select("SELECT * FROM PJT_PROJECT_GROUP WHERE ID = #{id}")
    ProjectGroup getProjectGroupById(Long id);

    @Select("SELECT * FROM PJT_PROJECT WHERE GROUP_ID = #{id}")
    List<Project> getProjectsUnderGroup(Long id);

    List<ProjectGroup> getProjectGroupUnderUser(Long userId, String role);

    Integer searchProjectCount(@Param("sp") SearchParam param);

    Integer searchGroupCount(@Param("sp") SearchParam param);

}
