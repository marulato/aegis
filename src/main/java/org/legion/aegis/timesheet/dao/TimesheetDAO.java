package org.legion.aegis.timesheet.dao;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.legion.aegis.common.jpa.SimpleSQLGenerator;
import org.legion.aegis.timesheet.entity.CommonEvent;
import org.legion.aegis.timesheet.vo.EventVO;

import java.util.List;
import java.util.Map;

@Mapper
public interface TimesheetDAO {

    @InsertProvider(type = SimpleSQLGenerator.class, method = "insert")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
    void createCommonEvent(CommonEvent event);

    List<EventVO> searchCommonEvents(@Param("sp") Map<String, Object> params);

    List<EventVO> searchPublicEvents(@Param("sp") Map<String, Object> params);
}
