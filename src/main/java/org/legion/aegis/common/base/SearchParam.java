package org.legion.aegis.common.base;

import java.io.Serializable;
import java.util.Map;

public class SearchParam implements Serializable {

    private Integer pageNo;
    private Integer pageSize;
    private Integer orderColumnNo;
    private String order;
    private Map<String, String> params;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOrderColumnNo() {
        return orderColumnNo;
    }

    public void setOrderColumnNo(Integer orderColumnNo) {
        this.orderColumnNo = orderColumnNo;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
