package org.legion.aegis.issuetracker.generator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import org.legion.aegis.common.docgen.IDocGenerator;
import org.legion.aegis.common.utils.XMLUtils;
import org.legion.aegis.issuetracker.vo.IssueVO;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class IssueExportXmlGenerator implements IDocGenerator {

    private final List<IssueVO> issueVOList;

    public IssueExportXmlGenerator(List<IssueVO> issueVOList) {
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
        if (issueVOList != null) {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("issueTracker_list");
            for (IssueVO vo : issueVOList) {
                Element main = root.addElement("issue");
                main.addElement("issueId").setText(vo.getIssueId());
                main.addElement("reportedBy").setText(vo.getReportedBy());
                main.addElement("assignedTo").setText(vo.getAssignedTo());
                main.addElement("moduleName").setText(vo.getModuleName());
                main.addElement("severity").setText(vo.getSeverity());
                main.addElement("sla").setText(vo.getSla());
                main.addElement("resolution").setText(vo.getResolution());
                main.addElement("status").setText(vo.getStatus());
                main.addElement("title").setText(vo.getTitle());
                main.addElement("reportedAt").setText(vo.getReportedAt());
                main.addElement("updatedAt").setText(vo.getUpdatedAt());
            }
            StringWriter stringWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(stringWriter, XMLUtils.getDefaultFormat());
            XMLUtils.write(xmlWriter, document);
            return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
        }
        return new byte[0];
    }
}
