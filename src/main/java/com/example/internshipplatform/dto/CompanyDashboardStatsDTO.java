package com.example.internshipplatform.dto;

import java.util.Map;

public class CompanyDashboardStatsDTO {
    private long totalOffers;
    private long totalApplications;
    private Map<String, Long> applicationsByStatus;

    public CompanyDashboardStatsDTO() {
    }

    public CompanyDashboardStatsDTO(long totalOffers, long totalApplications, Map<String, Long> applicationsByStatus) {
        this.totalOffers = totalOffers;
        this.totalApplications = totalApplications;
        this.applicationsByStatus = applicationsByStatus;
    }

    public long getTotalOffers() {
        return totalOffers;
    }

    public void setTotalOffers(long totalOffers) {
        this.totalOffers = totalOffers;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public void setTotalApplications(long totalApplications) {
        this.totalApplications = totalApplications;
    }

    public Map<String, Long> getApplicationsByStatus() {
        return applicationsByStatus;
    }

    public void setApplicationsByStatus(Map<String, Long> applicationsByStatus) {
        this.applicationsByStatus = applicationsByStatus;
    }
} 