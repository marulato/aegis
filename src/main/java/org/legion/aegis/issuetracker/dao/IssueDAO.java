package org.legion.aegis.issuetracker.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.issuetracker.vo.IssueVO;

import java.util.List;

@Mapper
public interface IssueDAO {

    List<IssueVO> search(@Param("sp") SearchParam param);

    Integer searchCounts(@Param("sp") SearchParam param);
}
