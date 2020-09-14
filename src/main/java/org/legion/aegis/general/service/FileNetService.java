package org.legion.aegis.general.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.consts.ContentConsts;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.utils.FTPClients;
import org.legion.aegis.common.utils.FileNameGenerator;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.general.dao.FileNetDAO;
import org.legion.aegis.general.entity.FileNet;
import org.legion.aegis.general.ex.FTPUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Map;
import java.util.UUID;

@Service
public class FileNetService {

    private final FileNetDAO fileNetDAO;

    @Autowired
    public FileNetService(FileNetDAO fileNetDAO) {
        this.fileNetDAO = fileNetDAO;
    }

    public static String getFileUUIDFirstHalf(byte[] fileData) {
        if (fileData != null) {
            return UUID.nameUUIDFromBytes(fileData).toString().replace("-", "");
        }
        return null;
    }

    public Long saveFileNetFTP(String path, String fileName, byte[] data) throws Exception {
        if (StringUtils.isNotBlank(path) && data != null) {
            FileNet fileNet = new FileNet();
            fileNet.setSha512(DigestUtils.sha512Hex(data).toUpperCase());
            fileNet.setSize(data.length);
            fileNet.setFileUuid(getFileUUID(data));
            fileNet.setFileName(fileName);
            fileNet.setPath(path);
            fileNet.setStorageType(AppConsts.FILE_NET_STORAGE_TYPE_FTP);
            fileNet.setStatus(AppConsts.FILE_NET_STATUS_STORED);
            String extension = FilenameUtils.getExtension(fileNet.getFileName()).toUpperCase();
            fileNet.setFileType(StringUtils.isNotBlank(extension) ? extension : AppConsts.FILE_NET_FILE_TYPE_UNKNOWN);
            fileNet.setMimeType(getMimeType(extension));
            FTPClients ftpClients = new FTPClients(true);
            InputStream inputStream = new ByteArrayInputStream(data);
            boolean isUploaded = ftpClients.upload(fileNet.getPath(), fileNet.getFileUuid(), inputStream);
            ftpClients.disConnect();
            inputStream.close();
            if (isUploaded) {
                fileNet.createAuditValues(AppContext.getFromWebThread());
                fileNetDAO.create(fileNet);
                return fileNet.getId();
            } else {
                throw new FTPUploadException("Upload file to FTP FAILED");
            }
        }
        return 0L;
    }

    private FileNet saveFileNet(String fileName, byte[] data, boolean saveDB) {
        if (StringUtils.isNotBlank(fileName) && data != null) {
            FileNet fileNet = new FileNet();
            fileNet.setSha512(DigestUtils.sha512Hex(data).toUpperCase());
            fileNet.setSize(data.length);
            fileNet.setFileUuid(getFileUUID(data));
            fileNet.setFileName(fileName);
            fileNet.setStatus(AppConsts.FILE_NET_STATUS_STORED);
            if (saveDB) {
                fileNet.setStorageType(AppConsts.FILE_NET_STORAGE_TYPE_DATABASE);
                fileNet.setData(data);
            } else {
                fileNet.setStorageType(AppConsts.FILE_NET_STORAGE_TYPE_LOCAL);
            }
            String extension = FilenameUtils.getExtension(fileNet.getFileName()).toUpperCase();
            fileNet.setFileType(StringUtils.isNotBlank(extension) ? extension : AppConsts.FILE_NET_FILE_TYPE_UNKNOWN);
            fileNet.setMimeType(getMimeType(extension));
            fileNet.createAuditValues(AppContext.getFromWebThread());
            fileNetDAO.create(fileNet);
            return fileNet;

        }
        return null;
    }

    public FileNet saveFileNetDB(String fileName, byte[] data) {
        return saveFileNet(fileName, data, true);
    }

    public FileNet saveFileNetLocal(String fileName, byte[] data, String localPath) throws Exception {
        if (StringUtils.isNotBlank(localPath)) {
            File file = new File(localPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileNet fileNet = saveFileNet(fileName, data, false);
            if (fileNet != null) {
                String savedName = fileNet.getFileUuid();
                String fullPath = FileNameGenerator.format(localPath) + savedName;
                try (FileOutputStream outputStream = new FileOutputStream(new File(fullPath))) {
                    outputStream.write(data);
                }
            }
            return fileNet;
        }
        return null;
    }


    public String getMimeType(String extension) {
        if (StringUtils.isNotBlank(extension)) {
            Map<String, String> map = ContentConsts.getExtensionMimeMap();
            String mimeType = map.get(extension.toUpperCase());
            if (StringUtils.isBlank(mimeType)) {
                mimeType = map.get(AppConsts.FILE_NET_FILE_TYPE_UNKNOWN);
            }
            return mimeType;
        }
        return null;
    }

    public FileNet getFileNetById(Long id) {
        return fileNetDAO.getFileNetById(id);
    }

    private String getFileUUID(byte[] fileData) {
        if (fileData != null) {
            String fileUUID = UUID.nameUUIDFromBytes(fileData).toString().replace("-", "");
            String random = UUID.randomUUID().toString();
            return fileUUID + "$" + random;
        }
        return null;
    }
}
