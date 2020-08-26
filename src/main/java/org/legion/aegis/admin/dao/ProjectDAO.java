package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.Project;
import java.util.List;

@Mapper
public interface ProjectDAO {

    @Select("SELECT * FROM PJT_PROJECT")
    List<Project> getAllProjects();

    @Select("SELECT * FROM PJT_PROJECT WHERE ID = #{id}")
    Project getProjectById(Long id);
}
