package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;

import java.util.Map;

public class UserStatisticsVO extends BaseVO {

    private Long userId;
    private String loginId;
    private String name;
    private Integer totalReport;
    private Double reportPercent;
    private Double reportPerDay;
    private Integer totalAssigned;
    private Double assignedPercent;
    private Double assignedPerDay;
    private Integer totalFixed;
    private Integer totalUnfixed;
    private Double fixedPerDay;
    private Integer maxSla;
    private Integer minSla;
    private Integer averageSla;
    private Integer totalMonitoring;
    private Integer totalReopened;
    private Integer totalClosed;
    private Map<String, CommonStatisticsVO> statusDistMap;
    private Map<String, CommonStatisticsVO> severityDistMap;
    private Map<String, CommonStatisticsVO> resolutionDistMap;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTotalReport() {
        return totalReport;
    }

    public void setTotalReport(Integer totalReport) {
        this.totalReport = totalReport;
    }

    public Double getReportPercent() {
        return reportPercent;
    }

    public void setReportPercent(Double reportPercent) {
        this.reportPercent = reportPercent;
    }

    public Double getReportPerDay() {
        return reportPerDay;
    }

    public void setReportPerDay(Double reportPerDay) {
        this.reportPerDay = reportPerDay;
    }

    public Integer getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(Integer totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public Double getAssignedPercent() {
        return assignedPercent;
    }

    public void setAssignedPercent(Double assignedPercent) {
        this.assignedPercent = assignedPercent;
    }

    public Double getAssignedPerDay() {
        return assignedPerDay;
    }

    public void setAssignedPerDay(Double assignedPerDay) {
        this.assignedPerDay = assignedPerDay;
    }

    public Integer getTotalFixed() {
        return totalFixed;
    }

    public void setTotalFixed(Integer totalFixed) {
        this.totalFixed = totalFixed;
    }

    public Integer getTotalUnfixed() {
        return totalUnfixed;
    }

    public void setTotalUnfixed(Integer totalUnfixed) {
        this.totalUnfixed = totalUnfixed;
    }

    public Double getFixedPerDay() {
        return fixedPerDay;
    }

    public void setFixedPerDay(Double fixedPerDay) {
        this.fixedPerDay = fixedPerDay;
    }

    public Integer getMaxSla() {
        return maxSla;
    }

    public void setMaxSla(Integer maxSla) {
        this.maxSla = maxSla;
    }

    public Integer getMinSla() {
        return minSla;
    }

    public void setMinSla(Integer minSla) {
        this.minSla = minSla;
    }

    public Integer getAverageSla() {
        return averageSla;
    }

    public void setAverageSla(Integer averageSla) {
        this.averageSla = averageSla;
    }

    public Integer getTotalMonitoring() {
        return totalMonitoring;
    }

    public void setTotalMonitoring(Integer totalMonitoring) {
        this.totalMonitoring = totalMonitoring;
    }

    public Integer getTotalReopened() {
        return totalReopened;
    }

    public void setTotalReopened(Integer totalReopened) {
        this.totalReopened = totalReopened;
    }

    public Integer getTotalClosed() {
        return totalClosed;
    }

    public void setTotalClosed(Integer totalClosed) {
        this.totalClosed = totalClosed;
    }

    public Map<String, CommonStatisticsVO> getStatusDistMap() {
        return statusDistMap;
    }

    public void setStatusDistMap(Map<String, CommonStatisticsVO> statusDistMap) {
        this.statusDistMap = statusDistMap;
    }

    public Map<String, CommonStatisticsVO> getSeverityDistMap() {
        return severityDistMap;
    }

    public void setSeverityDistMap(Map<String, CommonStatisticsVO> severityDistMap) {
        this.severityDistMap = severityDistMap;
    }

    public Map<String, CommonStatisticsVO> getResolutionDistMap() {
        return resolutionDistMap;
    }

    public void setResolutionDistMap(Map<String, CommonStatisticsVO> resolutionDistMap) {
        this.resolutionDistMap = resolutionDistMap;
    }
}
