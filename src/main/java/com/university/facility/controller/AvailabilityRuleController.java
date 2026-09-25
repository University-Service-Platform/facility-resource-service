package com.university.facility.controller;

import com.university.facility.common.ApiResponse;
import com.university.facility.dto.AvailabilityRuleDTO;
import com.university.facility.dto.CreateAvailabilityRuleRequest;
import com.university.facility.service.AvailabilityRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availability-rules")
@Tag(name = "Availability Rules Management", description = "Endpoints for defining structured availability, blackout, and role-based booking rules (USMG6-104, USMG6-28)")
public class AvailabilityRuleController {

    private final AvailabilityRuleService availabilityRuleService;

    public AvailabilityRuleController(AvailabilityRuleService availabilityRuleService) {
        this.availabilityRuleService = availabilityRuleService;
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get availability rules for resource", description = "Retrieve all structured rules configured for a resource")
    public ResponseEntity<ApiResponse<List<AvailabilityRuleDTO>>> getRulesByResource(@PathVariable Long resourceId) {
        List<AvailabilityRuleDTO> rules = availabilityRuleService.getRulesByResource(resourceId);
        return ResponseEntity.ok(ApiResponse.success("Availability rules retrieved successfully", rules));
    }

    @PostMapping
    @Operation(summary = "Define availability rule", description = "Create a structured blackout, role restriction, or operating hours rule")
    public ResponseEntity<ApiResponse<AvailabilityRuleDTO>> createRule(@Valid @RequestBody CreateAvailabilityRuleRequest request) {
        AvailabilityRuleDTO createdRule = availabilityRuleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Availability rule created successfully", createdRule));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate availability rule", description = "Deactivate a specific availability rule")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable Long id) {
        availabilityRuleService.deleteRule(id);
        return ResponseEntity.ok(ApiResponse.success("Availability rule deactivated successfully", null));
    }
}
