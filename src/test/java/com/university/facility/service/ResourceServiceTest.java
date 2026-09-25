package com.university.facility.service;

import com.university.facility.dto.CreateResourceRequest;
import com.university.facility.dto.ResourceDTO;
import com.university.facility.dto.ResourceValidationResponse;
import com.university.facility.exception.BadRequestException;
import com.university.facility.exception.DuplicateResourceException;
import com.university.facility.exception.ResourceNotFoundException;
import com.university.facility.model.Facility;
import com.university.facility.model.Resource;
import com.university.facility.model.ResourceType;
import com.university.facility.repository.FacilityRepository;
import com.university.facility.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private ResourceService resourceService;

    private Facility sampleFacility;
    private Resource sampleResource;

    @BeforeEach
    void setUp() {
        sampleFacility = new Facility(
                1L, "ENG-BLDG-A", "Engineering Building A", "North Campus", "Main Building",
                LocalTime.of(8, 0), LocalTime.of(22, 0), true
        );

        sampleResource = new Resource(
                10L, 1L, "LAB-101", "Computer Lab 101", ResourceType.LAB, "Room A-101", 30,
                true, true, true, LocalTime.of(8, 0), LocalTime.of(20, 0), "No food allowed"
        );
    }

    @Test
    @DisplayName("Should create resource successfully under existing facility")
    void createResource_Success() {
        CreateResourceRequest req = new CreateResourceRequest();
        req.setFacilityId(1L);
        req.setCode("LAB-102");
        req.setName("Computer Lab 102");
        req.setResourceType(ResourceType.LAB);
        req.setLocation("Room A-102");
        req.setCapacity(25);
        req.setOperatingHoursStart(LocalTime.of(8, 0));
        req.setOperatingHoursEnd(LocalTime.of(20, 0));

        when(facilityRepository.findById(1L)).thenReturn(Optional.of(sampleFacility));
        when(resourceRepository.existsByCode("LAB-102")).thenReturn(false);
        when(resourceRepository.save(any(Resource.class))).thenAnswer(inv -> {
            Resource r = inv.getArgument(0);
            r.setId(11L);
            return r;
        });

        ResourceDTO dto = resourceService.createResource(req);

        assertNotNull(dto);
        assertEquals(11L, dto.getId());
        assertEquals("LAB-102", dto.getCode());
        assertEquals("Engineering Building A", dto.getFacilityName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when creating resource for non-existent facility")
    void createResource_FacilityNotFound() {
        CreateResourceRequest req = new CreateResourceRequest();
        req.setFacilityId(999L);
        req.setCode("LAB-999");
        req.setName("Orphan Lab");
        req.setResourceType(ResourceType.LAB);
        req.setLocation("Room X");
        req.setCapacity(10);

        when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resourceService.createResource(req));
    }

    @Test
    @DisplayName("Should validate active and available resource successfully for reservation-service")
    void validateResource_Valid() {
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(sampleResource));
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(sampleFacility));

        ResourceValidationResponse response = resourceService.validateResource(10L);

        assertNotNull(response);
        assertTrue(response.isExists());
        assertTrue(response.isActive());
        assertTrue(response.isAvailable());
        assertTrue(response.isValidForReservation());
        assertTrue(response.isApprovalRequired());
        assertEquals(30, response.getCapacity());
        assertEquals(LocalTime.of(8, 0), response.getOperatingHoursStart());
        assertEquals(LocalTime.of(20, 0), response.getOperatingHoursEnd());
    }

    @Test
    @DisplayName("Should return validForReservation = false when resource is marked unavailable")
    void validateResource_Unavailable() {
        sampleResource.setAvailable(false);
        when(resourceRepository.findById(10L)).thenReturn(Optional.of(sampleResource));
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(sampleFacility));

        ResourceValidationResponse response = resourceService.validateResource(10L);

        assertNotNull(response);
        assertTrue(response.isExists());
        assertFalse(response.isAvailable());
        assertFalse(response.isValidForReservation());
        assertTrue(response.getMessage().contains("unavailable"));
    }

    @Test
    @DisplayName("Should evaluate availability and eligibility rules by date and time correctly")
    void checkAvailability_Success() {
        com.university.facility.dto.CheckAvailabilityRequest req = new com.university.facility.dto.CheckAvailabilityRequest(
                10L,
                java.time.LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                20,
                "STUDENT"
        );

        when(resourceRepository.findById(10L)).thenReturn(Optional.of(sampleResource));
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(sampleFacility));

        com.university.facility.dto.CheckAvailabilityResponse resp = resourceService.checkAvailability(req);

        assertNotNull(resp);
        assertTrue(resp.isAvailable());
        assertTrue(resp.isWithinOperatingHours());
        assertTrue(resp.isCapacitySufficient());
        assertTrue(resp.isUserEligible());
        assertTrue(resp.isApprovalRequired());
    }
}
