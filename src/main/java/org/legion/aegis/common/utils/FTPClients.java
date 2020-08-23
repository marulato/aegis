package org.legion.aegis.common.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

public class FTPClients {

    private FTPClient ftpClient;
    private static final Logger log = LoggerFactory.getLogger(FTPClients.class);

    public FTPClients(){}

    public FTPClients(boolean autoConnect) throws Exception {
        if (autoConnect) {
            connect();
        }
    }

    public void connect() throws Exception {
        ftpClient = new FTPClient();
        String host = ConfigUtils.get("server.ftp.host");
        String port = ConfigUtils.get("server.ftp.port");
        if (!StringUtils.isInteger(port)) {
            port = "21";
        }
        String username = ConfigUtils.get("server.ftp.username");
        String password = ConfigUtils.get("server.ftp.password");
        ftpClient.connect(host, Integer.parseInt(port));
        ftpClient.login(username, password);
        int replyCode = ftpClient.getReplyCode();
        if (FTPReply.isPositiveCompletion(replyCode)) {
            log.info("Connected to " + host + ":" + port);
        } else {
            log.error("Connection Failed, ftp server response with code: " + replyCode);
        }
    }

    public void connect(String host, int port, String username, String password) throws Exception {
        ftpClient = new FTPClient();
        ftpClient.connect(host, port);
        ftpClient.login(username, password);
        int replyCode = ftpClient.getReplyCode();
        if (FTPReply.isPositiveCompletion(replyCode)) {
            log.info("Connected to " + host + ":" + port + " at " + DateUtils.getLogDate(new Date()));
        } else {
            log.error("Connection Failed, ftp server response with code: " + replyCode);
        }
    }

    public void disConnect() throws Exception {
        if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
            log.info("Disconnected from ftp server at " + DateUtils.getLogDate(new Date()));
        }
    }

    public boolean cd(String dir) throws Exception {
        if (isConnected() && StringUtils.isNotBlank(dir)) {
            return ftpClient.changeWorkingDirectory(formatPath(dir));
        }
        return false;
    }

    public boolean mkdir(String dir) throws Exception {
        if (isConnected() && StringUtils.isNotBlank(dir)) {
            boolean success = true;
            String[] eachDir = formatPath(dir).split("/");
            for (String subDir : eachDir) {
                if (!ftpClient.changeWorkingDirectory(subDir.trim())) {
                    success = success && ftpClient.makeDirectory(subDir.trim());
                    success = success && ftpClient.changeWorkingDirectory(subDir.trim());
                }
            }
            ftpClient.changeWorkingDirectory("/");
            return success;
        }
        return false;
    }

    public boolean isFileExist(String dir, String fileName) throws Exception {
        if (isConnected() && StringUtils.isNotBlank(dir) && StringUtils.isNotBlank(fileName)) {
            if (cd(dir)) {
                FTPFile[] ftpFiles =ftpClient.listFiles();
                for (FTPFile ftpFile : ftpFiles) {
                    if (fileName.equals(ftpFile.getName())) {
                        ftpClient.changeWorkingDirectory("/");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isFileExist(String fullPathName) throws Exception {
        if (StringUtils.isNotBlank(fullPathName)) {
            String fileName = formatPath(fullPathName).substring(fullPathName.lastIndexOf("/") + 1);
            String dir = formatPath(fullPathName).substring(0, fullPathName.lastIndexOf("/") + 1);
            return isFileExist(dir, fileName);
        }
        return false;
    }

    public boolean upload(String destDir, String destFileName, InputStream inputStream) throws Exception {
        boolean success = false;
        long start = System.currentTimeMillis();
        if (isConnected() && inputStream != null && cd(destDir) && StringUtils.isNotBlank(destFileName)) {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            success = ftpClient.storeFile(destFileName, inputStream);
        } else if (isConnected() && inputStream != null && StringUtils.isNotBlank(destFileName)) {
            ftpClient.enterLocalPassiveMode();
            if (mkdir(destDir) && cd(destDir)) {
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                success = ftpClient.storeFile(destFileName, inputStream);
            }
        }
        long end = System.currentTimeMillis();
        ftpClient.changeWorkingDirectory("/");
        if (success) {
            log.info("[" + destFileName + "] uploaded successfully, " +
                    "time cost: " + (end - start) + " ms");
        }
        return success;
    }

    public byte[] downloadBytes(String dir, String fileName) throws Exception {
        if (isFileExist(dir, fileName)) {
            dir = formatPath(dir);
            if (!dir.endsWith("/")) {
                dir += "/";
            }
            String fullPath = dir + fileName;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            long start = System.currentTimeMillis();
            boolean success = ftpClient.retrieveFile(fullPath, outputStream);
            long end = System.currentTimeMillis();
            byte[] data = outputStream.toByteArray();
            if (success) {
                log.info("[" + fileName + " (" +getSizeInKB(data.length) + " KB)] downloaded successfully, " +
                        "time cost: " + (end - start) + " ms");
            }
            outputStream.close();
            ftpClient.changeWorkingDirectory("/");
            return data;
        }
        return null;
    }

    public boolean deleteFile(String dir, String fileName) throws Exception {
        boolean success = false;
        if (isFileExist(dir, fileName)) {
            dir = formatPath(dir);
            if (!dir.endsWith("/")) {
                dir += "/";
            }
            success = ftpClient.deleteFile(dir + fileName);
            ftpClient.changeWorkingDirectory("/");
            if (success) {
                log.info("[" + fileName + "] deleted successfully");
            }
        }
        return success;
    }


    private boolean isConnected() {
        return ftpClient != null && ftpClient.isConnected() && ftpClient.isAvailable();
    }

    private String formatPath(String dir) {
        if (StringUtils.isNotBlank(dir)) {
            return dir.replaceAll("\\\\", "/");
        }
        return dir;
    }

    private String getSizeInKB(long bytes) {
        return String.valueOf(Calculator.divide(
                Double.parseDouble(Long.toString(bytes)), 1024, 0, Calculator.ROUND_UP));
    }

}
