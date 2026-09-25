package com.university.facility.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.university.facility.config.SecurityConfig;
import com.university.facility.dto.ResourceDTO;
import com.university.facility.dto.ResourceValidationResponse;
import com.university.facility.model.ResourceType;
import com.university.facility.service.ResourceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceService resourceService;

    @Test
    @DisplayName("GET /api/resources should return list of resources with 200 OK")
    void searchResources_Success() throws Exception {
        ResourceDTO dto = new ResourceDTO();
        dto.setId(1L);
        dto.setCode("LAB-101");
        dto.setName("HPC Lab");
        dto.setResourceType(ResourceType.LAB);
        dto.setCapacity(30);

        when(resourceService.searchResources(any(), any(), any(), any(), any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/resources")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].code").value("LAB-101"))
                .andExpect(jsonPath("$.data[0].capacity").value(30));
    }
}
