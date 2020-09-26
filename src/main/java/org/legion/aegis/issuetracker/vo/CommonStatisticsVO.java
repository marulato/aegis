package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;

public class CommonStatisticsVO extends BaseVO {

    private String name;
    private Integer count;
    private Double percent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
