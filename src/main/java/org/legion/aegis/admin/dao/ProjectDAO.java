package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.*;
import org.legion.aegis.admin.entity.Project;
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

    List<Project> search(@Param("sp") SearchParam param);

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyColumn = "ID", keyProperty = "id")
    void createProject(Project project);

    @Select("SELECT * FROM PJT_PROJECT WHERE IS_PUBLIC = #{isPublic}")
    List<Project> getProjectsByIsPublic(String isPublic);

    List<Project> getProjectsUnderUser(Long userId);
}
