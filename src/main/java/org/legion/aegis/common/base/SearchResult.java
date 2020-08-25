package org.legion.aegis.common.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class  SearchResult <E> implements Serializable {

    private Integer totalCounts;
    private Integer draw;
    private List<E> resultList;

    public SearchResult() {
        resultList = new ArrayList<>();
    }

    public Integer getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public List<E> getResultList() {
        return resultList;
    }

    public void setResultList(List<E> resultList) {
        this.resultList = resultList;
    }
}
