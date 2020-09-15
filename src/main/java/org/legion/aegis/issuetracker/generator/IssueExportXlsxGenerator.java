package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.common.docgen.Excel;
import org.legion.aegis.common.docgen.IDocGenerator;
import org.legion.aegis.issuetracker.vo.IssueVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueExportXlsxGenerator implements IDocGenerator {

    private final List<IssueVO> issueVOList;

    public IssueExportXlsxGenerator(List<IssueVO> issueVOList) {
        this.issueVOList = issueVOList;
    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getTemplate() {
        return null;
    }

    @Override
    public byte[] generate() {
        Excel excel = new Excel();
        if (issueVOList != null) {
            for (int i = 0; i < issueVOList.size(); i++) {
                List<String> row = new ArrayList<>();
                IssueVO vo = issueVOList.get(i);
                row.add(vo.getIssueId());
                row.add(vo.getReportedBy());
                row.add(vo.getAssignedTo());
                row.add(vo.getModuleName());
                row.add(vo.getSeverity());
                row.add(vo.getSla());
                row.add(vo.getResolution());
                row.add(vo.getStatus());
                row.add(vo.getTitle());
                row.add(vo.getReportedAt());
                row.add(vo.getUpdatedAt());
                excel.setRow(i + 1, row);
            }
        }
        return excel.saveAsByteArray();
    }
}
