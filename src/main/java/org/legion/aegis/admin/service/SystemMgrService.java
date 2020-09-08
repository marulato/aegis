package org.legion.aegis.admin.service;

import org.legion.aegis.admin.dao.SystemMgrDAO;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.vo.IssueStatusVO;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemMgrService {

    private final SystemMgrDAO systemMgrDAO;

    @Autowired
    public SystemMgrService(SystemMgrDAO systemMgrDAO) {
        this.systemMgrDAO = systemMgrDAO;
    }

    public SearchResult<IssueStatusVO> searchIssueStatus(SearchParam param) {
        SearchResult<IssueStatusVO> searchResult = new SearchResult<>(systemMgrDAO.searchIssueStatus(param), param);
        searchResult.setTotalCounts(systemMgrDAO.searchIssueStatusCounts(param));
        return searchResult;
    }

    public IssueStatus getIssueStatusByCode(String statusCode) {
        return systemMgrDAO.getIssueStatusByCode(statusCode);
    }
}
