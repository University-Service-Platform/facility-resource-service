package com.university.facility.controller;

import com.university.facility.common.ApiResponse;
import com.university.facility.dto.CreateResourceRequest;
import com.university.facility.dto.ResourceDTO;
import com.university.facility.dto.UpdateResourceRequest;
import com.university.facility.model.ResourceType;
import com.university.facility.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resource Management", description = "Endpoints for managing rooms, labs, equipment, capacity, and availability state")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    @Operation(summary = "Search & Filter resources", description = "Query resources by facilityId, resourceType, minCapacity, active, and available flags")
    public ResponseEntity<ApiResponse<List<ResourceDTO>>> searchResources(
            @RequestParam(required = false) Long facilityId,
            @RequestParam(required = false) ResourceType resourceType,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean available) {
        List<ResourceDTO> resources = resourceService.searchResources(facilityId, resourceType, minCapacity, active, available);
        return ResponseEntity.ok(ApiResponse.success("Resources retrieved successfully", resources));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Retrieve specific resource by unique ID")
    public ResponseEntity<ApiResponse<ResourceDTO>> getResourceById(@PathVariable Long id) {
        ResourceDTO resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(ApiResponse.success(resource));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get resource by unique code", description = "Retrieve resource details by code (e.g. LAB-101)")
    public ResponseEntity<ApiResponse<ResourceDTO>> getResourceByCode(@PathVariable String code) {
        ResourceDTO resource = resourceService.getResourceByCode(code);
        return ResponseEntity.ok(ApiResponse.success(resource));
    }

    @GetMapping("/facility/{facilityId}")
    @Operation(summary = "Get resources in facility", description = "Retrieve all resources belonging to a given facility ID")
    public ResponseEntity<ApiResponse<List<ResourceDTO>>> getResourcesByFacility(@PathVariable Long facilityId) {
        List<ResourceDTO> resources = resourceService.getResourcesByFacility(facilityId);
        return ResponseEntity.ok(ApiResponse.success(resources));
    }

    @PostMapping
    @Operation(summary = "Create a new resource", description = "Add a new room, lab, or equipment to a facility")
    public ResponseEntity<ApiResponse<ResourceDTO>> createResource(@Valid @RequestBody CreateResourceRequest request) {
        ResourceDTO createdResource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Resource created successfully", createdResource));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resource", description = "Modify details, capacity, active state, or availability of a resource")
    public ResponseEntity<ApiResponse<ResourceDTO>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody UpdateResourceRequest request) {
        ResourceDTO updatedResource = resourceService.updateResource(id, request);
        return ResponseEntity.ok(ApiResponse.success("Resource updated successfully", updatedResource));
    }

    @GetMapping("/types")
    @Operation(summary = "List all supported resource types", description = "Retrieve list of all supported ResourceType enum values (USMG6-21)")
    public ResponseEntity<ApiResponse<ResourceType[]>> getResourceTypes() {
        return ResponseEntity.ok(ApiResponse.success("Resource types retrieved successfully", ResourceType.values()));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate resource", description = "Explicitly toggle resource active status (USMG6-20)")
    public ResponseEntity<ApiResponse<ResourceDTO>> updateResourceStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        ResourceDTO updated = resourceService.updateResourceStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Resource active status updated", updated));
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle resource availability status", description = "Explicitly toggle resource available status (USMG6-24)")
    public ResponseEntity<ApiResponse<ResourceDTO>> updateResourceAvailability(
            @PathVariable Long id,
            @RequestParam boolean available) {
        ResourceDTO updated = resourceService.updateResourceAvailability(id, available);
        return ResponseEntity.ok(ApiResponse.success("Resource availability updated", updated));
    }

    @PatchMapping("/{id}/approval-requirement")
    @Operation(summary = "Toggle resource approval requirement", description = "Explicitly toggle whether resource requires approval for reservations (USMG6-25)")
    public ResponseEntity<ApiResponse<ResourceDTO>> updateResourceApprovalRequirement(
            @PathVariable Long id,
            @RequestParam boolean required) {
        ResourceDTO updated = resourceService.updateResourceApprovalRequirement(id, required);
        return ResponseEntity.ok(ApiResponse.success("Resource approval requirement updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate/De-list a resource", description = "Set active and available flags of resource to false")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource deactivated successfully", null));
    }
}
