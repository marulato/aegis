package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.common.docgen.Excel;
import org.legion.aegis.common.docgen.IDocGenerator;
import org.legion.aegis.issuetracker.vo.CommonStatisticsVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueStatisticsXlsxGenerator implements IDocGenerator {

    private final List<CommonStatisticsVO> commonStatisticsVOList;
    private List<String> types;
    private String type;

    public IssueStatisticsXlsxGenerator(List<CommonStatisticsVO> commonStatisticsVOList) {
        this.commonStatisticsVOList = commonStatisticsVOList;
        types = new ArrayList<>();
        types.add("status");
        types.add("resolution");
        types.add("severity");
        types.add("priority");
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
        if (types.contains(type)) {
            if (commonStatisticsVOList != null) {
                List<String> header = new ArrayList<>();
                header.add("名称");
                header.add("数量");
                header.add("百分比（%）");
                excel.setRow(0, header);
                for (int i = 0; i < commonStatisticsVOList.size(); i++) {
                    List<String> row = new ArrayList<>();
                    CommonStatisticsVO vo = commonStatisticsVOList.get(i);
                    row.add(vo.getName());
                    row.add(String.valueOf(vo.getCount()));
                    row.add(String.valueOf(vo.getPercent()));
                    excel.setRow(i + 1, row);
                }
            }
        }
        return excel.saveAsByteArray();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
