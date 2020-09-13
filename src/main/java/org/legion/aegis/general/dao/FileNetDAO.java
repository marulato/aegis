package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.general.entity.FileNet;


@Mapper
public interface FileNetDAO {

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void create(FileNet fileNet);

    @Select("SELECT * FROM CM_FILE_NET WHERE ID = #{id}")
    FileNet getFileNetById(Long id);
}
