package com.university.facility.service;

import com.university.facility.dto.CreateFacilityRequest;
import com.university.facility.dto.FacilityDTO;
import com.university.facility.dto.UpdateFacilityRequest;
import com.university.facility.exception.BadRequestException;
import com.university.facility.exception.DuplicateResourceException;
import com.university.facility.exception.ResourceNotFoundException;
import com.university.facility.model.Facility;
import com.university.facility.repository.FacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityService(FacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    @Transactional(readOnly = true)
    public List<FacilityDTO> getAllFacilities(Boolean activeOnly) {
        List<Facility> facilities = Boolean.TRUE.equals(activeOnly)
                ? facilityRepository.findByActiveTrue()
                : facilityRepository.findAll();

        return facilities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FacilityDTO getFacilityById(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + id));
        return mapToDTO(facility);
    }

    @Transactional(readOnly = true)
    public FacilityDTO getFacilityByCode(String code) {
        Facility facility = facilityRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with code: " + code));
        return mapToDTO(facility);
    }

    public FacilityDTO createFacility(CreateFacilityRequest request) {
        if (facilityRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Facility code already exists: " + request.getCode());
        }

        if (request.getOperatingHoursStart().isAfter(request.getOperatingHoursEnd()) ||
            request.getOperatingHoursStart().equals(request.getOperatingHoursEnd())) {
            throw new BadRequestException("Operating start time must be before end time");
        }

        Facility facility = new Facility();
        facility.setCode(request.getCode());
        facility.setName(request.getName());
        facility.setLocation(request.getLocation());
        facility.setDescription(request.getDescription());
        facility.setOperatingHoursStart(request.getOperatingHoursStart());
        facility.setOperatingHoursEnd(request.getOperatingHoursEnd());
        facility.setActive(request.getActive() != null ? request.getActive() : true);

        Facility savedFacility = facilityRepository.save(facility);
        return mapToDTO(savedFacility);
    }

    public FacilityDTO updateFacility(Long id, UpdateFacilityRequest request) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + id));

        if (request.getName() != null) {
            facility.setName(request.getName());
        }
        if (request.getLocation() != null) {
            facility.setLocation(request.getLocation());
        }
        if (request.getDescription() != null) {
            facility.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            facility.setActive(request.getActive());
        }

        if (request.getOperatingHoursStart() != null || request.getOperatingHoursEnd() != null) {
            var start = request.getOperatingHoursStart() != null ? request.getOperatingHoursStart() : facility.getOperatingHoursStart();
            var end = request.getOperatingHoursEnd() != null ? request.getOperatingHoursEnd() : facility.getOperatingHoursEnd();

            if (start.isAfter(end) || start.equals(end)) {
                throw new BadRequestException("Operating start time must be before end time");
            }

            facility.setOperatingHoursStart(start);
            facility.setOperatingHoursEnd(end);
        }

        Facility updatedFacility = facilityRepository.save(facility);
        return mapToDTO(updatedFacility);
    }

    public FacilityDTO updateFacilityStatus(Long id, boolean active) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + id));
        facility.setActive(active);
        Facility updatedFacility = facilityRepository.save(facility);
        return mapToDTO(updatedFacility);
    }

    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facility not found with ID: " + id));
        facility.setActive(false);
        facilityRepository.save(facility);
    }

    private FacilityDTO mapToDTO(Facility facility) {
        return new FacilityDTO(
                facility.getId(),
                facility.getCode(),
                facility.getName(),
                facility.getLocation(),
                facility.getDescription(),
                facility.getOperatingHoursStart(),
                facility.getOperatingHoursEnd(),
                facility.isActive(),
                facility.getCreatedAt(),
                facility.getUpdatedAt()
        );
    }
}
