package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.common.docgen.Excel;
import org.legion.aegis.common.docgen.IDocGenerator;
import org.legion.aegis.issuetracker.vo.IssueVcsTrackerVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueVCSGenerator implements IDocGenerator {

    private final List<IssueVcsTrackerVO> voList;

    public IssueVCSGenerator(List<IssueVcsTrackerVO> voList) {
        this.voList = voList;
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
    public byte[] generate() throws Exception {
        Excel excel = new Excel();
        List<String> header = new ArrayList<>();
        header.add("问题ID");
        header.add("项目");
        header.add("分支");
        header.add("Tag");
        header.add("文件");
        header.add("主干版本");
        header.add("分支版本");
        header.add("修改时间");
        excel.setRow(0, header);
        if (voList != null) {
            for (int i = 0; i < voList.size(); i++) {
                IssueVcsTrackerVO vo = voList.get(i);
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(vo.getIssueId()));
                row.add(vo.getProject());
                row.add(vo.getBranch());
                row.add(vo.getTag());
                row.add(vo.getFileFullPath());
                row.add(vo.getMasterVersion());
                row.add(vo.getBranchVersion());
                row.add(vo.getUpdatedAt());
                excel.setRow(i + 1, row);
            }
        }
        return excel.saveAsByteArray();
    }
}
