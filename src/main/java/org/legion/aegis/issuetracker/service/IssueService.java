package org.legion.aegis.issuetracker.service;

import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.issuetracker.dao.IssueDAO;
import org.legion.aegis.issuetracker.vo.IssueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class IssueService {

    private final IssueDAO issueDAO;

    @Autowired
    public IssueService(IssueDAO issueDAO) {
        this.issueDAO = issueDAO;
    }

    public SearchResult<IssueVO> search(SearchParam param) {
        if (StringUtils.isNotBlank((String) param.getParams().get("reportedRange"))){
            String[] dateRange = ((String) param.getParams().get("reportedRange")).split("-");
            if (dateRange.length == 2) {
                Date start = DateUtils.parseDatetime(dateRange[0]);
                Date end = DateUtils.parseDatetime(dateRange[1]);
                if (start != null && end != null && !end.before(start)) {
                    param.addParam("reportedFrom", start);
                    param.addParam("reportedTo", end);
                }
            }
        }
        if (StringUtils.isNotBlank((String) param.getParams().get("updatedRange"))){
            String[] dateRange = ((String) param.getParams().get("updatedRange")).split("-");
            if (dateRange.length == 2) {
                Date start = DateUtils.parseDatetime(dateRange[0]);
                Date end = DateUtils.parseDatetime(dateRange[1]);
                if (start != null && end != null && !end.before(start)) {
                    param.addParam("updatedFrom", start);
                    param.addParam("updatedTo", end);
                }
            }
        }
        List<IssueVO> results = issueDAO.search(param);
        for (IssueVO vo : results) {
            vo.setSla(String.valueOf(DateUtils.getDaysBetween(DateUtils.parseDatetime(vo.getReportedAt()), new Date())));
        }
        if (param.getOrderColumnNo() == 5) {
            if ("ASC".equalsIgnoreCase(param.getOrder())) {
                results.sort(Comparator.comparing(IssueVO::getSla));
            } else {
                results.sort(Comparator.comparing(IssueVO::getSla, Comparator.reverseOrder()));
            }
        }
        SearchResult<IssueVO> searchResult = new SearchResult<>(results, param);
        searchResult.setTotalCounts(issueDAO.searchCounts(param));
        return searchResult;
    }
}
