package com.university.facility.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CheckAvailabilityResponse {
    private Long resourceId;
    private String resourceCode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean available;
    private boolean withinOperatingHours;
    private boolean capacitySufficient;
    private boolean userEligible;
    private boolean approvalRequired;
    private String message;

    public CheckAvailabilityResponse() {
    }

    public CheckAvailabilityResponse(Long resourceId, String resourceCode, LocalDate date, LocalTime startTime,
                                     LocalTime endTime, boolean available, boolean withinOperatingHours,
                                     boolean capacitySufficient, boolean userEligible, boolean approvalRequired,
                                     String message) {
        this.resourceId = resourceId;
        this.resourceCode = resourceCode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.available = available;
        this.withinOperatingHours = withinOperatingHours;
        this.capacitySufficient = capacitySufficient;
        this.userEligible = userEligible;
        this.approvalRequired = approvalRequired;
        this.message = message;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isWithinOperatingHours() {
        return withinOperatingHours;
    }

    public void setWithinOperatingHours(boolean withinOperatingHours) {
        this.withinOperatingHours = withinOperatingHours;
    }

    public boolean isCapacitySufficient() {
        return capacitySufficient;
    }

    public void setCapacitySufficient(boolean capacitySufficient) {
        this.capacitySufficient = capacitySufficient;
    }

    public boolean isUserEligible() {
        return userEligible;
    }

    public void setUserEligible(boolean userEligible) {
        this.userEligible = userEligible;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
