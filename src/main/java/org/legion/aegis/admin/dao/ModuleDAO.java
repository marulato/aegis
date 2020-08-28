package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.Module;

import java.util.List;

@Mapper
public interface ModuleDAO {

    @Select("SELECT * FROM PJT_MODULE WHERE ID = #{id}")
    Module getModuleById(Long id);

    @Select("SELECT * FROM PJT_MODULE WHERE PROJECT_ID = #{projectId}")
    List<Module> getModulesByProjectId(Long projectId);
}
