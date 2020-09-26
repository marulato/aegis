package org.legion.aegis.admin.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.legion.aegis.admin.entity.IssueResolution;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.vo.IssueResolutionVO;
import org.legion.aegis.admin.vo.IssueStatusVO;
import org.legion.aegis.common.base.SearchParam;
import java.util.List;

@Mapper
public interface SystemMgrDAO {

    List<IssueStatusVO> searchIssueStatus(@Param("sp") SearchParam param);

    Integer searchIssueStatusCounts(@Param("sp") SearchParam param);

    @Select("SELECT * FROM CM_ISSUE_STATUS WHERE STATUS_CODE = #{statusCode}")
    IssueStatus getIssueStatusByCode(String statusCode);

    @Select("SELECT * FROM CM_ISSUE_STATUS WHERE IS_INUSE = 'Y'")
    List<IssueStatus> getAllInuseIssueStatus();

    @Select("SELECT * FROM CM_ISSUE_STATUS")
    List<IssueStatus> getAllIssueStatus();

    List<IssueResolutionVO> searchIssueResolutions(@Param("sp") SearchParam param);

    Integer searchIssueResolutionsCounts(@Param("sp") SearchParam param);

    @Select("SELECT * FROM CM_ISSUE_RESOLUTION WHERE RESOLUTION_CODE = #{resolutionCode}")
    IssueResolution getIssueResolutionByCode(String resolutionCode);

    @Select("SELECT * FROM CM_ISSUE_RESOLUTION WHERE IS_INUSE = 'Y'")
    List<IssueResolution> getAllInuseIssueResolutions();

    @Select("SELECT * FROM CM_ISSUE_RESOLUTION")
    List<IssueResolution> getAllIssueResolutions();
}
