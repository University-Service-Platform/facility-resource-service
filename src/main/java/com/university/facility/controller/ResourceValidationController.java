package com.university.facility.controller;

import com.university.facility.common.ApiResponse;
import com.university.facility.dto.CheckAvailabilityRequest;
import com.university.facility.dto.CheckAvailabilityResponse;
import com.university.facility.dto.ResourceValidationResponse;
import com.university.facility.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
@Tag(name = "Resource Validation (Cross-Service APIs)", description = "APIs consumed by reservation-service and Groups 7 & 8 to validate resource existence, active status, availability, capacity, and operating hours")
public class ResourceValidationController {

    private final ResourceService resourceService;

    public ResourceValidationController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate resource by ID", description = "Checks existence, active status, availability, operating hours, capacity, and approval-required flag for reservation-service")
    public ResponseEntity<ApiResponse<ResourceValidationResponse>> validateResourceById(@PathVariable Long id) {
        ResourceValidationResponse response = resourceService.validateResource(id);
        return ResponseEntity.ok(ApiResponse.success("Resource validation completed", response));
    }

    @GetMapping("/code/{code}/validate")
    @Operation(summary = "Validate resource by Code", description = "Checks existence, active status, and availability using resource code")
    public ResponseEntity<ApiResponse<ResourceValidationResponse>> validateResourceByCode(@PathVariable String code) {
        ResourceValidationResponse response = resourceService.validateResourceByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Resource validation completed", response));
    }

    @PostMapping("/check-availability")
    @Operation(summary = "Check resource availability by date, time, capacity, and role", description = "Evaluates whether a resource is available for a requested date, time slot, capacity, and user role (USMG6-29, USMG6-32, USMG6-106)")
    public ResponseEntity<ApiResponse<CheckAvailabilityResponse>> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequest request) {
        CheckAvailabilityResponse response = resourceService.checkAvailability(request);
        return ResponseEntity.ok(ApiResponse.success("Availability check completed", response));
    }

    @GetMapping("/{id}/validate/group7")
    @Operation(summary = "Validate facility/resource for Group 7", description = "Exposes facility/resource validation contract specifically for Group 7 integration (USMG6-65)")
    public ResponseEntity<ApiResponse<ResourceValidationResponse>> validateForGroup7(@PathVariable Long id) {
        ResourceValidationResponse response = resourceService.validateResource(id);
        return ResponseEntity.ok(ApiResponse.success("Group 7 resource validation completed", response));
    }

    @GetMapping("/{id}/validate/group8")
    @Operation(summary = "Validate venue/resource for Group 8", description = "Exposes venue/resource validation contract specifically for Group 8 integration (USMG6-66)")
    public ResponseEntity<ApiResponse<ResourceValidationResponse>> validateForGroup8(@PathVariable Long id) {
        ResourceValidationResponse response = resourceService.validateResource(id);
        return ResponseEntity.ok(ApiResponse.success("Group 8 resource validation completed", response));
    }

    @PostMapping("/validate-batch")
    @Operation(summary = "Batch validate resources", description = "Validates multiple resource IDs in a single request for bulk processing")
    public ResponseEntity<ApiResponse<List<ResourceValidationResponse>>> validateBatch(@RequestBody List<Long> resourceIds) {
        List<ResourceValidationResponse> responses = resourceService.validateBatch(resourceIds);
        return ResponseEntity.ok(ApiResponse.success("Batch validation completed", responses));
    }
}
