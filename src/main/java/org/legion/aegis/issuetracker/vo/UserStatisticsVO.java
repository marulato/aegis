package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;

import java.util.Map;

public class UserStatisticsVO extends BaseVO {

    private Long userId;
    private String loginId;
    private String name;
    private int totalReport;
    private double reportPercent;
    private double reportPerDay;
    private int totalAssigned;
    private double assignedPercent;
    private double assignedPerDay;
    private int totalFixed;
    private int totalUnfixed;
    private double fixedPerDay;
    private double maxSla;
    private double minSla;
    private double averageSla;
    private Map<String, CommonStatisticsVO> statusDistMap;
    private Map<String, CommonStatisticsVO> severityDistMap;
    private Map<String, CommonStatisticsVO> resolutionDistMap;

    private boolean canView;

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

    public int getTotalReport() {
        return totalReport;
    }

    public void setTotalReport(int totalReport) {
        this.totalReport = totalReport;
    }

    public double getReportPercent() {
        return reportPercent;
    }

    public void setReportPercent(double reportPercent) {
        this.reportPercent = reportPercent;
    }

    public double getReportPerDay() {
        return reportPerDay;
    }

    public void setReportPerDay(double reportPerDay) {
        this.reportPerDay = reportPerDay;
    }

    public int getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(int totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    public double getAssignedPercent() {
        return assignedPercent;
    }

    public void setAssignedPercent(double assignedPercent) {
        this.assignedPercent = assignedPercent;
    }

    public double getAssignedPerDay() {
        return assignedPerDay;
    }

    public void setAssignedPerDay(double assignedPerDay) {
        this.assignedPerDay = assignedPerDay;
    }

    public int getTotalFixed() {
        return totalFixed;
    }

    public void setTotalFixed(int totalFixed) {
        this.totalFixed = totalFixed;
    }

    public int getTotalUnfixed() {
        return totalUnfixed;
    }

    public void setTotalUnfixed(int totalUnfixed) {
        this.totalUnfixed = totalUnfixed;
    }

    public double getFixedPerDay() {
        return fixedPerDay;
    }

    public void setFixedPerDay(double fixedPerDay) {
        this.fixedPerDay = fixedPerDay;
    }

    public double getMaxSla() {
        return maxSla;
    }

    public void setMaxSla(double maxSla) {
        this.maxSla = maxSla;
    }

    public double getMinSla() {
        return minSla;
    }

    public void setMinSla(double minSla) {
        this.minSla = minSla;
    }

    public double getAverageSla() {
        return averageSla;
    }

    public void setAverageSla(double averageSla) {
        this.averageSla = averageSla;
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

    public boolean isCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
