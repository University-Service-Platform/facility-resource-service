package com.university.facility.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI facilityResourceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Facility & Resource Management Service API")
                        .description("REST APIs for managing facilities, resources, types, operating hours, and resource validation for reservation-service (Group 6, 7 & 8).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("University Architecture Team - Group 6")
                                .email("backend1@university.edu"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
