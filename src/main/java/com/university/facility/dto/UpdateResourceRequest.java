package com.university.facility.dto;

import com.university.facility.model.ResourceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;

public class UpdateResourceRequest {

    @Size(max = 100, message = "Resource name cannot exceed 100 characters")
    private String name;

    private ResourceType resourceType;

    @Size(max = 150, message = "Location cannot exceed 150 characters")
    private String location;

    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    private Boolean active;
    private Boolean available;
    private Boolean approvalRequired;

    private LocalTime operatingHoursStart;
    private LocalTime operatingHoursEnd;

    @Size(max = 500, message = "Rules description cannot exceed 500 characters")
    private String rulesDescription;

    @Size(max = 200, message = "Allowed user roles string cannot exceed 200 characters")
    private String allowedUserRoles;

    public UpdateResourceRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
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

    public String getRulesDescription() {
        return rulesDescription;
    }

    public void setRulesDescription(String rulesDescription) {
        this.rulesDescription = rulesDescription;
    }

    public String getAllowedUserRoles() {
        return allowedUserRoles;
    }

    public void setAllowedUserRoles(String allowedUserRoles) {
        this.allowedUserRoles = allowedUserRoles;
    }
}
