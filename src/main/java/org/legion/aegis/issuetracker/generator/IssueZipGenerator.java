package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.common.docgen.IDocGenerator;
import org.legion.aegis.common.utils.CompressUtils;
import org.legion.aegis.issuetracker.vo.IssueVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueZipGenerator implements IDocGenerator {

    private final List<IssueVO> issueVOList;

    public IssueZipGenerator(List<IssueVO> issueVOList) {
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
    public byte[] generate() throws Exception {
        IDocGenerator pdfGen = new IssueExportPdfGenerator(issueVOList);
        IDocGenerator xlsxGen = new IssueExportXlsxGenerator(issueVOList);
        IDocGenerator xmlGen = new IssueExportXmlGenerator(issueVOList);
        Map<String, byte[]> dataMap = new HashMap<>();
        dataMap.put("Export_PDF.pdf", pdfGen.generate());
        dataMap.put("Export_XLSX.xlsx", xlsxGen.generate());
        dataMap.put("Export_XML.xml", xmlGen.generate());
        return CompressUtils.compressZip(dataMap);
    }
}
