package org.legion.aegis.general.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.Sequence;

@Mapper
public interface SequenceDAO {

    @Select("SELECT * FROM CM_SEQUENCE WHERE NAME = #{name}")
    Sequence getSequence(String name);

}
