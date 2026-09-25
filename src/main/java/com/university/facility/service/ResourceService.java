package com.university.facility.service;

import com.university.facility.dto.*;
import com.university.facility.exception.BadRequestException;
import com.university.facility.exception.DuplicateResourceException;
import com.university.facility.exception.ResourceNotFoundException;
import com.university.facility.model.Facility;
import com.university.facility.model.Resource;
import com.university.facility.model.ResourceType;
import com.university.facility.repository.FacilityRepository;
import com.university.facility.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final FacilityRepository facilityRepository;

    public ResourceService(ResourceRepository resourceRepository, FacilityRepository facilityRepository) {
        this.resourceRepository = resourceRepository;
        this.facilityRepository = facilityRepository;
    }

    @Transactional(readOnly = true)
    public List<ResourceDTO> searchResources(Long facilityId, ResourceType type, Integer minCapacity,
                                             Boolean active, Boolean available) {
        List<Resource> resources = resourceRepository.searchResources(facilityId, type, minCapacity, active, available);
        Map<Long, String> facilityNameMap = getFacilityNamesMap(resources);

        return resources.stream()
                .map(r -> mapToDTO(r, facilityNameMap.get(r.getFacilityId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResourceDTO getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        String facilityName = facilityRepository.findById(resource.getFacilityId())
                .map(Facility::getName)
                .orElse("Unknown Facility");
        return mapToDTO(resource, facilityName);
    }

    @Transactional(readOnly = true)
    public ResourceDTO getResourceByCode(String code) {
        Resource resource = resourceRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with code: " + code));
        String facilityName = facilityRepository.findById(resource.getFacilityId())
                .map(Facility::getName)
                .orElse("Unknown Facility");
        return mapToDTO(resource, facilityName);
    }

    @Transactional(readOnly = true)
    public List<ResourceDTO> getResourcesByFacility(Long facilityId) {
        if (!facilityRepository.existsById(facilityId)) {
            throw new ResourceNotFoundException("Facility not found with ID: " + facilityId);
        }
        List<Resource> resources = resourceRepository.findByFacilityId(facilityId);
        String facilityName = facilityRepository.findById(facilityId).map(Facility::getName).orElse("Unknown Facility");

        return resources.stream()
                .map(r -> mapToDTO(r, facilityName))
                .collect(Collectors.toList());
    }

    public ResourceDTO createResource(CreateResourceRequest request) {
        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + request.getFacilityId()));

        if (resourceRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Resource code already exists: " + request.getCode());
        }

        LocalTime start = request.getOperatingHoursStart() != null ? request.getOperatingHoursStart() : facility.getOperatingHoursStart();
        LocalTime end = request.getOperatingHoursEnd() != null ? request.getOperatingHoursEnd() : facility.getOperatingHoursEnd();

        if (start.isAfter(end) || start.equals(end)) {
            throw new BadRequestException("Operating hours start time must be before end time");
        }

        Resource resource = new Resource();
        resource.setFacilityId(facility.getId());
        resource.setCode(request.getCode());
        resource.setName(request.getName());
        resource.setResourceType(request.getResourceType());
        resource.setLocation(request.getLocation());
        resource.setCapacity(request.getCapacity());
        resource.setActive(request.getActive() != null ? request.getActive() : true);
        resource.setAvailable(request.getAvailable() != null ? request.getAvailable() : true);
        resource.setApprovalRequired(request.getApprovalRequired() != null ? request.getApprovalRequired() : false);
        resource.setOperatingHoursStart(request.getOperatingHoursStart());
        resource.setOperatingHoursEnd(request.getOperatingHoursEnd());
        resource.setRulesDescription(request.getRulesDescription());
        resource.setAllowedUserRoles(request.getAllowedUserRoles());

        Resource savedResource = resourceRepository.save(resource);
        return mapToDTO(savedResource, facility.getName());
    }

    public ResourceDTO updateResource(Long id, UpdateResourceRequest request) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));

        Facility facility = facilityRepository.findById(resource.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Associated facility not found with ID: " + resource.getFacilityId()));

        if (request.getName() != null) {
            resource.setName(request.getName());
        }
        if (request.getResourceType() != null) {
            resource.setResourceType(request.getResourceType());
        }
        if (request.getLocation() != null) {
            resource.setLocation(request.getLocation());
        }
        if (request.getCapacity() != null) {
            resource.setCapacity(request.getCapacity());
        }
        if (request.getActive() != null) {
            resource.setActive(request.getActive());
        }
        if (request.getAvailable() != null) {
            resource.setAvailable(request.getAvailable());
        }
        if (request.getApprovalRequired() != null) {
            resource.setApprovalRequired(request.getApprovalRequired());
        }
        if (request.getRulesDescription() != null) {
            resource.setRulesDescription(request.getRulesDescription());
        }
        if (request.getAllowedUserRoles() != null) {
            resource.setAllowedUserRoles(request.getAllowedUserRoles());
        }

        if (request.getOperatingHoursStart() != null || request.getOperatingHoursEnd() != null) {
            LocalTime start = request.getOperatingHoursStart() != null ? request.getOperatingHoursStart()
                    : (resource.getOperatingHoursStart() != null ? resource.getOperatingHoursStart() : facility.getOperatingHoursStart());
            LocalTime end = request.getOperatingHoursEnd() != null ? request.getOperatingHoursEnd()
                    : (resource.getOperatingHoursEnd() != null ? resource.getOperatingHoursEnd() : facility.getOperatingHoursEnd());

            if (start.isAfter(end) || start.equals(end)) {
                throw new BadRequestException("Operating start time must be before end time");
            }

            resource.setOperatingHoursStart(request.getOperatingHoursStart());
            resource.setOperatingHoursEnd(request.getOperatingHoursEnd());
        }

        Resource updatedResource = resourceRepository.save(resource);
        return mapToDTO(updatedResource, facility.getName());
    }

    public ResourceDTO updateResourceStatus(Long id, boolean active) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        resource.setActive(active);
        if (!active) {
            resource.setAvailable(false);
        }
        Resource updated = resourceRepository.save(resource);
        String facilityName = facilityRepository.findById(updated.getFacilityId()).map(Facility::getName).orElse("Unknown Facility");
        return mapToDTO(updated, facilityName);
    }

    public ResourceDTO updateResourceAvailability(Long id, boolean available) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        resource.setAvailable(available);
        Resource updated = resourceRepository.save(resource);
        String facilityName = facilityRepository.findById(updated.getFacilityId()).map(Facility::getName).orElse("Unknown Facility");
        return mapToDTO(updated, facilityName);
    }

    public ResourceDTO updateResourceApprovalRequirement(Long id, boolean required) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        resource.setApprovalRequired(required);
        Resource updated = resourceRepository.save(resource);
        String facilityName = facilityRepository.findById(updated.getFacilityId()).map(Facility::getName).orElse("Unknown Facility");
        return mapToDTO(updated, facilityName);
    }

    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        resource.setActive(false);
        resource.setAvailable(false);
        resourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    public CheckAvailabilityResponse checkAvailability(CheckAvailabilityRequest request) {
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + request.getResourceId()));
        Facility facility = facilityRepository.findById(resource.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent facility not found with ID: " + resource.getFacilityId()));

        LocalTime start = resource.getOperatingHoursStart() != null ? resource.getOperatingHoursStart() : facility.getOperatingHoursStart();
        LocalTime end = resource.getOperatingHoursEnd() != null ? resource.getOperatingHoursEnd() : facility.getOperatingHoursEnd();

        boolean withinOperatingHours = !request.getStartTime().isBefore(start) && !request.getEndTime().isAfter(end);
        boolean capacitySufficient = request.getRequestedCapacity() == null || request.getRequestedCapacity() <= resource.getCapacity();

        boolean userEligible = true;
        if (resource.getAllowedUserRoles() != null && !resource.getAllowedUserRoles().isBlank() && request.getUserRole() != null) {
            userEligible = resource.getAllowedUserRoles().toUpperCase().contains(request.getUserRole().toUpperCase());
        }

        boolean available = resource.isActive() && resource.isAvailable() && facility.isActive()
                && withinOperatingHours && capacitySufficient && userEligible;

        StringBuilder message = new StringBuilder();
        if (!facility.isActive()) message.append("Parent facility is inactive. ");
        if (!resource.isActive()) message.append("Resource is inactive. ");
        if (!resource.isAvailable()) message.append("Resource is marked unavailable. ");
        if (!withinOperatingHours) message.append("Requested time violates operating hours (").append(start).append("-").append(end).append("). ");
        if (!capacitySufficient) message.append("Requested capacity (").append(request.getRequestedCapacity()).append(") exceeds max capacity (").append(resource.getCapacity()).append("). ");
        if (!userEligible) message.append("User role '").append(request.getUserRole()).append("' is not eligible for this resource. ");

        if (available) {
            message.append("Resource is available for reservation.");
        }

        return new CheckAvailabilityResponse(
                resource.getId(),
                resource.getCode(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                available,
                withinOperatingHours,
                capacitySufficient,
                userEligible,
                resource.isApprovalRequired(),
                message.toString().trim()
        );
    }

    @Transactional(readOnly = true)
    public ResourceValidationResponse validateResource(Long id) {
        Optional<Resource> resourceOpt = resourceRepository.findById(id);
        if (resourceOpt.isEmpty()) {
            return ResourceValidationResponse.notFound(id);
        }
        return buildValidationResponse(resourceOpt.get());
    }

    @Transactional(readOnly = true)
    public ResourceValidationResponse validateResourceByCode(String code) {
        Optional<Resource> resourceOpt = resourceRepository.findByCode(code);
        if (resourceOpt.isEmpty()) {
            return ResourceValidationResponse.notFound(code);
        }
        return buildValidationResponse(resourceOpt.get());
    }

    @Transactional(readOnly = true)
    public List<ResourceValidationResponse> validateBatch(List<Long> resourceIds) {
        return resourceIds.stream()
                .map(this::validateResource)
                .collect(Collectors.toList());
    }

    private ResourceValidationResponse buildValidationResponse(Resource resource) {
        Facility facility = facilityRepository.findById(resource.getFacilityId()).orElse(null);

        boolean facilityActive = facility != null && facility.isActive();
        boolean validForReservation = resource.isActive() && resource.isAvailable() && facilityActive;

        LocalTime start = resource.getOperatingHoursStart() != null ? resource.getOperatingHoursStart()
                : (facility != null ? facility.getOperatingHoursStart() : null);
        LocalTime end = resource.getOperatingHoursEnd() != null ? resource.getOperatingHoursEnd()
                : (facility != null ? facility.getOperatingHoursEnd() : null);

        String message;
        if (!resource.isActive()) {
            message = "Resource is inactive";
        } else if (!resource.isAvailable()) {
            message = "Resource is currently marked unavailable";
        } else if (!facilityActive) {
            message = "Parent facility is inactive";
        } else {
            message = "Resource is valid and available for reservation";
        }

        return new ResourceValidationResponse(
                resource.getId(),
                resource.getCode(),
                resource.getFacilityId(),
                true,
                resource.isActive(),
                resource.isAvailable(),
                resource.getCapacity(),
                resource.isApprovalRequired(),
                start,
                end,
                validForReservation,
                message
        );
    }

    private Map<Long, String> getFacilityNamesMap(List<Resource> resources) {
        List<Long> facilityIds = resources.stream()
                .map(Resource::getFacilityId)
                .distinct()
                .collect(Collectors.toList());
        return facilityRepository.findAllById(facilityIds).stream()
                .collect(Collectors.toMap(Facility::getId, Facility::getName));
    }

    private ResourceDTO mapToDTO(Resource resource, String facilityName) {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(resource.getId());
        dto.setFacilityId(resource.getFacilityId());
        dto.setFacilityName(facilityName);
        dto.setCode(resource.getCode());
        dto.setName(resource.getName());
        dto.setResourceType(resource.getResourceType());
        dto.setLocation(resource.getLocation());
        dto.setCapacity(resource.getCapacity());
        dto.setActive(resource.isActive());
        dto.setAvailable(resource.isAvailable());
        dto.setApprovalRequired(resource.isApprovalRequired());

        Facility facility = facilityRepository.findById(resource.getFacilityId()).orElse(null);
        dto.setOperatingHoursStart(resource.getOperatingHoursStart() != null ? resource.getOperatingHoursStart()
                : (facility != null ? facility.getOperatingHoursStart() : null));
        dto.setOperatingHoursEnd(resource.getOperatingHoursEnd() != null ? resource.getOperatingHoursEnd()
                : (facility != null ? facility.getOperatingHoursEnd() : null));

        dto.setRulesDescription(resource.getRulesDescription());
        dto.setAllowedUserRoles(resource.getAllowedUserRoles());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        return dto;
    }
}
