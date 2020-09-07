package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.legion.aegis.admin.vo.IssueStatusVO;
import org.legion.aegis.common.base.SearchParam;
import java.util.List;

@Mapper
public interface SystemMgrDAO {

    List<IssueStatusVO> searchIssueStatus(@Param("sp") SearchParam param);

    Integer searchIssueStatusCounts(@Param("sp") SearchParam param);
}
