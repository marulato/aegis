package org.legion.aegis.general.vo;

import org.legion.aegis.common.base.BaseVO;

public class EmailAttachmentVO extends BaseVO {

    private String iconClass;
    private String fileName;
    private Integer size;
    private String uuid;
    private String extension;
    private Long id;
    private Long fileNetId;
    private String sizeString;

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSizeString() {
        return sizeString;
    }

    public void setSizeString(String sizeString) {
        this.sizeString = sizeString;
    }

    public Long getFileNetId() {
        return fileNetId;
    }

    public void setFileNetId(Long fileNetId) {
        this.fileNetId = fileNetId;
    }
}
