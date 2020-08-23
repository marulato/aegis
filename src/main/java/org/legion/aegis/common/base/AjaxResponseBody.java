package org.legion.aegis.common.base;

import java.io.Serializable;
import java.util.Date;

public class AjaxResponseBody implements Serializable {

    private int status;
    private Object data;
    private Date requestAt;
    private Date respondAt;

    AjaxResponseBody(){}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    void setData(Object data) {
        this.data = data;
    }

    public Date getRequestAt() {
        return requestAt;
    }

    public void setRequestAt(Date requestAt) {
        this.requestAt = requestAt;
    }

    public Date getRespondAt() {
        return respondAt;
    }

    public void setRespondAt(Date respondAt) {
        this.respondAt = respondAt;
    }
}
