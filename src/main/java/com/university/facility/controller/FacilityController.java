package com.university.facility.controller;

import com.university.facility.common.ApiResponse;
import com.university.facility.dto.CreateFacilityRequest;
import com.university.facility.dto.FacilityDTO;
import com.university.facility.dto.UpdateFacilityRequest;
import com.university.facility.service.FacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facilities")
@Tag(name = "Facility Management", description = "Endpoints for creating, reading, updating, and managing campus facilities")
public class FacilityController {

    private final FacilityService facilityService;

    public FacilityController(FacilityService facilityService) {
        this.facilityService = facilityService;
    }

    @GetMapping
    @Operation(summary = "Get all facilities", description = "Retrieve a list of all facilities, optionally filtering active facilities only")
    public ResponseEntity<ApiResponse<List<FacilityDTO>>> getAllFacilities(
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        List<FacilityDTO> facilities = facilityService.getAllFacilities(activeOnly);
        return ResponseEntity.ok(ApiResponse.success("Facilities retrieved successfully", facilities));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get facility by ID", description = "Retrieve facility details by unique facility ID")
    public ResponseEntity<ApiResponse<FacilityDTO>> getFacilityById(@PathVariable Long id) {
        FacilityDTO facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(ApiResponse.success(facility));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get facility by unique code", description = "Retrieve facility details by unique code (e.g. ENG-BLDG-A)")
    public ResponseEntity<ApiResponse<FacilityDTO>> getFacilityByCode(@PathVariable String code) {
        FacilityDTO facility = facilityService.getFacilityByCode(code);
        return ResponseEntity.ok(ApiResponse.success(facility));
    }

    @PostMapping
    @Operation(summary = "Create a new facility", description = "Register a new campus facility")
    public ResponseEntity<ApiResponse<FacilityDTO>> createFacility(@Valid @RequestBody CreateFacilityRequest request) {
        FacilityDTO createdFacility = facilityService.createFacility(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Facility created successfully", createdFacility));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing facility", description = "Modify details of an existing facility")
    public ResponseEntity<ApiResponse<FacilityDTO>> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFacilityRequest request) {
        FacilityDTO updatedFacility = facilityService.updateFacility(id, request);
        return ResponseEntity.ok(ApiResponse.success("Facility updated successfully", updatedFacility));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate facility", description = "Explicitly toggle facility active status (USMG6-14)")
    public ResponseEntity<ApiResponse<FacilityDTO>> updateFacilityStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        FacilityDTO updatedFacility = facilityService.updateFacilityStatus(id, active);
        return ResponseEntity.ok(ApiResponse.success("Facility status updated successfully", updatedFacility));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete / deactivate facility", description = "Set facility active state to false")
    public ResponseEntity<ApiResponse<Void>> deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.ok(ApiResponse.success("Facility deactivated successfully", null));
    }
}
