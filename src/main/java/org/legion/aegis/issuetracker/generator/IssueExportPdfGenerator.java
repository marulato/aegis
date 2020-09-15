package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.common.docgen.DocumentConsts;
import org.legion.aegis.common.docgen.PdfTemplateGenerator;
import org.legion.aegis.issuetracker.vo.IssueVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueExportPdfGenerator extends PdfTemplateGenerator {

    private final List<IssueVO> issueVOList;

    public IssueExportPdfGenerator(List<IssueVO> issueVOList) {
        this.issueVOList = issueVOList;
        setPageSize(DocumentConsts.PAGE_A3.rotate());
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("issueList", issueVOList);
        return params;
    }

    @Override
    public String getTemplate() {
        return "issueExport.ftl";
    }
}
