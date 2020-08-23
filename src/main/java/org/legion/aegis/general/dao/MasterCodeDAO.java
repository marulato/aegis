package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.Config;
import org.legion.aegis.admin.entity.MasterCode;


import java.util.List;

@Mapper
public interface MasterCodeDAO {

    @Select("SELECT * FROM CM_CONFIG WHERE CONFIG_KEY = #{key}")
    Config getConfig(String key);

    @Select("SELECT * FROM CM_MASTER_CODE WHERE TYPE = #{param1} AND CODE = #{param2}")
    MasterCode getMasterCode(String type, String code);

    @Select("SELECT * FROM CM_MASTER_CODE")
    List<MasterCode> getAllMasterCodes();

    @Select("SELECT * FROM CM_MASTER_CODE WHERE TYPE = #{type}")
    List<MasterCode> getMasterCodeByType(String type);

}
