package com.university.facility.service;

import com.university.facility.dto.CreateFacilityRequest;
import com.university.facility.dto.FacilityDTO;
import com.university.facility.dto.UpdateFacilityRequest;
import com.university.facility.exception.BadRequestException;
import com.university.facility.exception.DuplicateResourceException;
import com.university.facility.exception.ResourceNotFoundException;
import com.university.facility.model.Facility;
import com.university.facility.repository.FacilityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacilityServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityService facilityService;

    private Facility sampleFacility;

    @BeforeEach
    void setUp() {
        sampleFacility = new Facility(
                1L,
                "ENG-BLDG-A",
                "Engineering Building A",
                "North Campus",
                "Main Engineering Building",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                true
        );
    }

    @Test
    @DisplayName("Should return facility DTO when facility exists by ID")
    void getFacilityById_Success() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(sampleFacility));

        FacilityDTO dto = facilityService.getFacilityById(1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("ENG-BLDG-A", dto.getCode());
        assertEquals("Engineering Building A", dto.getName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when facility ID does not exist")
    void getFacilityById_NotFound() {
        when(facilityRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> facilityService.getFacilityById(99L));
    }

    @Test
    @DisplayName("Should create facility successfully when code is unique and operating hours are valid")
    void createFacility_Success() {
        CreateFacilityRequest req = new CreateFacilityRequest(
                "CS-BLDG",
                "Computer Science Building",
                "West Campus",
                "CS Labs and Classrooms",
                LocalTime.of(8, 0),
                LocalTime.of(20, 0),
                true
        );

        when(facilityRepository.existsByCode("CS-BLDG")).thenReturn(false);
        when(facilityRepository.save(any(Facility.class))).thenAnswer(invocation -> {
            Facility f = invocation.getArgument(0);
            f.setId(2L);
            return f;
        });

        FacilityDTO dto = facilityService.createFacility(req);

        assertNotNull(dto);
        assertEquals("CS-BLDG", dto.getCode());
        verify(facilityRepository, times(1)).save(any(Facility.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when facility code already exists")
    void createFacility_DuplicateCode() {
        CreateFacilityRequest req = new CreateFacilityRequest(
                "ENG-BLDG-A",
                "Duplicate Engineering",
                "North Campus",
                "Desc",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                true
        );

        when(facilityRepository.existsByCode("ENG-BLDG-A")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> facilityService.createFacility(req));
    }

    @Test
    @DisplayName("Should throw BadRequestException when operating start time is after end time")
    void createFacility_InvalidOperatingHours() {
        CreateFacilityRequest req = new CreateFacilityRequest(
                "BAD-HOURS",
                "Bad Hours Building",
                "North Campus",
                "Desc",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                true
        );

        when(facilityRepository.existsByCode("BAD-HOURS")).thenReturn(false);

        assertThrows(BadRequestException.class, () -> facilityService.createFacility(req));
    }
}
