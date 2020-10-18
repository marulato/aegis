package org.legion.aegis.issuetracker.dto;

public class ExportDto {

    private byte[] data;
    private String uuid;
    private String type;

    public Integer getSize() {
        if (data != null) {
            return data.length;
        }
        return 0;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
