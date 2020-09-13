package org.legion.aegis.initializer.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountDAO {

    public void dropTable_AC_ROLE();

    public void createTable_AC_ROLE();
}
