package org.legion.aegis.admin.service;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.legion.aegis.admin.dao.SystemMgrDAO;
import org.legion.aegis.admin.dto.IssueStatusDto;
import org.legion.aegis.admin.entity.IssueResolution;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.vo.IssueResolutionVO;
import org.legion.aegis.admin.vo.IssueStatusVO;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.exception.InvalidXMLFormatException;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.XMLUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemMgrService {

    private final SystemMgrDAO systemMgrDAO;
    private static final Logger log = LoggerFactory.getLogger(SystemMgrService.class);

    @Autowired
    public SystemMgrService(SystemMgrDAO systemMgrDAO) {
        this.systemMgrDAO = systemMgrDAO;
    }

    public SearchResult<IssueStatusVO> searchIssueStatus(SearchParam param) {
        SearchResult<IssueStatusVO> searchResult = new SearchResult<>(systemMgrDAO.searchIssueStatus(param), param);
        searchResult.setTotalCounts(systemMgrDAO.searchIssueStatusCounts(param));
        return searchResult;
    }

    public SearchResult<IssueResolutionVO> searchIssueResolutions(SearchParam param) {
        SearchResult<IssueResolutionVO> searchResult = new SearchResult<>(systemMgrDAO.searchIssueResolutions(param), param);
        searchResult.setTotalCounts(systemMgrDAO.searchIssueResolutionsCounts(param));
        return searchResult;
    }

    public IssueStatus getIssueStatusByCode(String statusCode) {
        return systemMgrDAO.getIssueStatusByCode(statusCode);
    }

    public IssueResolution getIssueResolutionByCode(String resolutionCode) {
        return systemMgrDAO.getIssueResolutionByCode(resolutionCode);
    }

    public void updateIssueStatus(IssueStatus issueStatus) {
        if (issueStatus != null) {
            IssueStatus old = getIssueStatusByCode(issueStatus.getStatusCode());
            if (old != null) {
                old.setColor(issueStatus.getColor().toUpperCase());
                old.setStatusCode(issueStatus.getStatusCode().toUpperCase());
                old.setDisplayName(issueStatus.getDisplayName());
                old.setDescription(issueStatus.getDescription());
                old.setIsInuse(issueStatus.getIsInuse());
                JPAExecutor.update(old);
            }
        }
    }

    public void updateIssueResolutin(IssueResolution resolution) {
        if (resolution != null) {
            IssueResolution old = getIssueResolutionByCode(resolution.getResolutionCode());
            if (old != null) {
                old.setResolutionCode(resolution.getResolutionCode().toUpperCase());
                old.setDisplayName(resolution.getDisplayName());
                old.setDescription(resolution.getDescription());
                old.setIsInuse(resolution.getIsInuse());
                JPAExecutor.update(old);
            }
        }
    }

    public byte[] exportIssueStatus() throws Exception {
        List<IssueStatus> issueStatuses = systemMgrDAO.getAllIssueStatus();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("systemManagement_issueStatus");
        for (IssueStatus obj : issueStatuses) {
            Element parent = root.addElement("issueStatus");
            Element statusCodeEle = parent.addElement("statusCode");
            statusCodeEle.setText(obj.getStatusCode());
            Element displayNameEle = parent.addElement("displayName");
            displayNameEle.setText(obj.getDisplayName());
            Element descEle = parent.addElement("description");
            descEle.setText(obj.getDescription());
            Element colorEle = parent.addElement("color");
            colorEle.setText(obj.getColor());
            Element isSystemEle = parent.addElement("isSystem");
            isSystemEle.setText(obj.getIsSystem());
            Element isInuseEle = parent.addElement("isInuse");
            isInuseEle.setText(StringUtils.isEmpty(obj.getIsInuse()) ? AppConsts.YES : AppConsts.NO);
        }
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new XMLWriter(stringWriter, XMLUtils.getDefaultFormat());
        XMLUtils.write(xmlWriter, document);
        return stringWriter.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public List<ConstraintViolation> importIssueStatus(InputStream inputStream) throws Exception {
        if (inputStream != null) {
            List<IssueStatus> readyForAdd = new ArrayList<>();
            List<IssueStatus> readyForModify = new ArrayList<>();
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<IssueStatusDto> issueStatusList = new ArrayList<>();
            if (root.getName().equals("systemManagement_issueStatus")) {
                List<Element> childElements = root.elements();
                for (Element parent : childElements) {
                    if (parent.getName().equals("issueStatus")) {
                        IssueStatusDto issueStatus = new IssueStatusDto();
                        List<Element> eachNodeList = parent.elements();
                        XMLUtils.mapping(eachNodeList, issueStatus);
                        issueStatusList.add(issueStatus);
                    }
                }
            }
            for (IssueStatusDto dto : issueStatusList) {
                IssueStatus exist = getIssueStatusByCode(dto.getStatusCode());
                if (exist == null) {
                    List<ConstraintViolation> violations = CommonValidator.validate(dto, "add");
                    if (violations.isEmpty()) {
                        IssueStatus issueStatus = BeanUtils.mapFromDto(dto, IssueStatus.class);
                        readyForAdd.add(issueStatus);
                    } else {
                        return violations;
                    }
                } else {
                    List<ConstraintViolation> violations = CommonValidator.validate(dto, "modify");
                    if (violations.isEmpty()) {
                        IssueStatus issueStatus = BeanUtils.mapFromDto(dto, IssueStatus.class);
                        readyForModify.add(issueStatus);
                    } else {
                        return violations;
                    }
                }
            }
            for (IssueStatus var : readyForAdd) {
                JPAExecutor.save(var);
            }
            for (IssueStatus var : readyForModify) {
                updateIssueStatus(var);
            }
        }
        return new ArrayList<>();
    }
}
