package org.legion.aegis.general.service;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.docgen.DocumentConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.general.dao.DocumentDAO;
import org.legion.aegis.general.entity.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class DocumentService {

    private final DocumentDAO documentDAO;

    @Autowired
    public DocumentService(DocumentDAO documentDAO) {
        this.documentDAO = documentDAO;
    }

    public Long saveDocument(Document document) {
        if (document != null) {
            Document existsDoc = documentDAO.getDocumentById(document.getId());
            if (existsDoc != null) {
                JPAExecutor.update(document);
                return document.getId();
            } else {
                document.createAuditValues(AppContext.getFromWebThread());
                documentDAO.create(document);
                return document.getId();
            }
        }
        return 0L;
    }

    public void updateDocument(Document document, boolean increaseVersion) {
        if (document != null) {
            Document existsDoc = documentDAO.getDocumentById(document.getId());
            if (existsDoc != null && !DocumentConsts.DOC_STATUS_DELETED.equals(existsDoc.getStatus())) {
                if (existsDoc.getVersion().equals(document.getVersion()) && increaseVersion) {
                    document.setVersion(document.getVersion() + 1);
                }
                JPAExecutor.update(document);
            }
        }
    }


    public void deleteDocument(Long documentId, String reason) {
        if (documentId > 0) {
            Document existsDoc = documentDAO.getDocumentById(documentId);
            if (existsDoc != null && !DocumentConsts.DOC_STATUS_DELETED.equals(existsDoc.getStatus())) {
                existsDoc.setIsDeleted(AppConsts.YES);
                existsDoc.setDeletedFor(reason);
                existsDoc.setDeletedAt(new Date());
                existsDoc.setDeletedBy(AppContext.getFromWebThread().getLoginId());
                existsDoc.setStatus(DocumentConsts.DOC_STATUS_DELETED);
                JPAExecutor.update(existsDoc);
            }
        }
    }



    public void recoverDocument(Long documentId, String reason) {
        if (documentId > 0) {
            Document existsDoc = documentDAO.getDocumentById(documentId);
            if (existsDoc != null && DocumentConsts.DOC_STATUS_DELETED.equals(existsDoc.getStatus())) {
                existsDoc.setIsDeleted(AppConsts.NO);
                existsDoc.setStatus(AppConsts.ACCOUNT_STATUS_ACTIVE);
                JPAExecutor.update(existsDoc);
            }
        }
    }

}
