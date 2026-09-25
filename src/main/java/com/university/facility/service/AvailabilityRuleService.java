package com.university.facility.service;

import com.university.facility.dto.AvailabilityRuleDTO;
import com.university.facility.dto.CreateAvailabilityRuleRequest;
import com.university.facility.exception.ResourceNotFoundException;
import com.university.facility.model.AvailabilityRule;
import com.university.facility.repository.AvailabilityRuleRepository;
import com.university.facility.repository.ResourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvailabilityRuleService {

    private final AvailabilityRuleRepository availabilityRuleRepository;
    private final ResourceRepository resourceRepository;

    public AvailabilityRuleService(AvailabilityRuleRepository availabilityRuleRepository,
                                   ResourceRepository resourceRepository) {
        this.availabilityRuleRepository = availabilityRuleRepository;
        this.resourceRepository = resourceRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityRuleDTO> getRulesByResource(Long resourceId) {
        if (!resourceRepository.existsById(resourceId)) {
            throw new ResourceNotFoundException("Resource not found with ID: " + resourceId);
        }
        return availabilityRuleRepository.findByResourceId(resourceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvailabilityRuleDTO createRule(CreateAvailabilityRuleRequest request) {
        if (!resourceRepository.existsById(request.getResourceId())) {
            throw new ResourceNotFoundException("Resource not found with ID: " + request.getResourceId());
        }

        AvailabilityRule rule = new AvailabilityRule();
        rule.setResourceId(request.getResourceId());
        rule.setRuleName(request.getRuleName());
        rule.setRuleType(request.getRuleType());
        rule.setDayOfWeek(request.getDayOfWeek());
        rule.setStartTime(request.getStartTime());
        rule.setEndTime(request.getEndTime());
        rule.setAllowedRoles(request.getAllowedRoles());
        rule.setActive(request.getActive() != null ? request.getActive() : true);

        AvailabilityRule saved = availabilityRuleRepository.save(rule);
        return mapToDTO(saved);
    }

    public void deleteRule(Long ruleId) {
        AvailabilityRule rule = availabilityRuleRepository.findById(ruleId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability rule not found with ID: " + ruleId));
        rule.setActive(false);
        availabilityRuleRepository.save(rule);
    }

    private AvailabilityRuleDTO mapToDTO(AvailabilityRule rule) {
        return new AvailabilityRuleDTO(
                rule.getId(),
                rule.getResourceId(),
                rule.getRuleName(),
                rule.getRuleType(),
                rule.getDayOfWeek(),
                rule.getStartTime(),
                rule.getEndTime(),
                rule.getAllowedRoles(),
                rule.isActive(),
                rule.getCreatedAt()
        );
    }
}
