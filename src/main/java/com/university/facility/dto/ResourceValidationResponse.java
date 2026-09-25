package com.university.facility.dto;

import java.time.LocalTime;

public class ResourceValidationResponse {
    private Long resourceId;
    private String resourceCode;
    private Long facilityId;
    private boolean exists;
    private boolean active;
    private boolean available;
    private Integer capacity;
    private boolean approvalRequired;
    private LocalTime operatingHoursStart;
    private LocalTime operatingHoursEnd;
    private boolean validForReservation;
    private String message;

    public ResourceValidationResponse() {
    }

    public ResourceValidationResponse(Long resourceId, String resourceCode, Long facilityId, boolean exists,
                                      boolean active, boolean available, Integer capacity, boolean approvalRequired,
                                      LocalTime operatingHoursStart, LocalTime operatingHoursEnd,
                                      boolean validForReservation, String message) {
        this.resourceId = resourceId;
        this.resourceCode = resourceCode;
        this.facilityId = facilityId;
        this.exists = exists;
        this.active = active;
        this.available = available;
        this.capacity = capacity;
        this.approvalRequired = approvalRequired;
        this.operatingHoursStart = operatingHoursStart;
        this.operatingHoursEnd = operatingHoursEnd;
        this.validForReservation = validForReservation;
        this.message = message;
    }

    public static ResourceValidationResponse notFound(Long resourceId) {
        ResourceValidationResponse resp = new ResourceValidationResponse();
        resp.setResourceId(resourceId);
        resp.setExists(false);
        resp.setValidForReservation(false);
        resp.setMessage("Resource with ID " + resourceId + " does not exist");
        return resp;
    }

    public static ResourceValidationResponse notFound(String code) {
        ResourceValidationResponse resp = new ResourceValidationResponse();
        resp.setResourceCode(code);
        resp.setExists(false);
        resp.setValidForReservation(false);
        resp.setMessage("Resource with code " + code + " does not exist");
        return resp;
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

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public LocalTime getOperatingHoursStart() {
        return operatingHoursStart;
    }

    public void setOperatingHoursStart(LocalTime operatingHoursStart) {
        this.operatingHoursStart = operatingHoursStart;
    }

    public LocalTime getOperatingHoursEnd() {
        return operatingHoursEnd;
    }

    public void setOperatingHoursEnd(LocalTime operatingHoursEnd) {
        this.operatingHoursEnd = operatingHoursEnd;
    }

    public boolean isValidForReservation() {
        return validForReservation;
    }

    public void setValidForReservation(boolean validForReservation) {
        this.validForReservation = validForReservation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
