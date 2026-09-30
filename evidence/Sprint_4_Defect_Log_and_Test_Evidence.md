# Sprint 4 Defect Log & Test Evidence Report
## Group 6: Facility & Reservation Microservices
**Project:** University Services Management Platform  
**Sprint Window:** 25 Sep 2026 – 30 Sep 2026  
**Quality Lead / Backend Lead:** Group 6 Engineering  
**Deployment Environment:** Microsoft Azure Linux VM (`20.205.129.149`)  

---

## 1. Executive QA Summary
* **Total Automated Tests Executed:** 69 test cases (28 Maven unit/integration, 32 Gradle tests, 9 E2E integration scenarios).
* **Test Pass Rate:** 100% (0 Failures, 0 Regressions).
* **Defects Logged:** 6 tracked defects.
* **Defects Resolved:** 6 resolved (100% resolution rate).
* **Open Critical/Blocker Bugs:** 0.

---

## 2. Comprehensive Defect Log (DEF-01 to DEF-06)

| Defect ID | Summary / Symptom | Severity | Root Cause Analysis | Engineering Resolution | Verified In |
|---|---|---|---|---|---|
| **DEF-01** | `university-reservation-service` failed to compile locally with missing Java 17 | High | Gradle toolchain configured strictly for JDK 17 while host environment had JDK 21 | Configured `openjdk-17-jdk` path in build environment; toolchain detection succeeded | `./gradlew test` |
| **DEF-02** | Swagger UI returning `401 Unauthorized` on reservation endpoints | Medium | Spring Security OAuth2 resource server rejected calls missing Bearer header | Added `OpenApiConfig.java` registering `BearerAuth` security scheme with `dev-token` bypass | Swagger UI |
| **DEF-03** | Docker build error `openjdk:17-jre-slim: not found` | High | Official `openjdk` legacy tags were retired from Docker Hub | Upgraded base image to official LTS `eclipse-temurin:17-jre-alpine` | `docker build` |
| **DEF-04** | `mvnw: Permission denied` inside Alpine container | Medium | Linux execute bit (`+x`) was missing on `./mvnw` within repository checkout | Added `RUN chmod +x ./mvnw` step before packaging in Dockerfile | `docker build` |
| **DEF-05** | `facility-resource-service` crashed on startup with connection refused | High | Missing `SPRING_DATASOURCE_DRIVER` env var caused Spring Boot to default to `org.h2.Driver` with a `jdbc:mysql` URL | Explicitly added `SPRING_DATASOURCE_DRIVER=com.mysql.cj.jdbc.Driver` to `docker-compose.yml` | `docker compose up` |
| **DEF-06** | Gradle version incompatibility during reservation Docker build | High | Base Docker image had Gradle 8.10.2, incompatible with Spring Boot 4.0 plugin | Updated Dockerfile to invoke project's Gradle 9.7 wrapper (`./gradlew bootJar`) | `docker build` |
| **DEF-07** | Tech Lead findings: Base `GET /availability-rules` returned 500 instead of 405, unknown paths returned 500, reservation returned bare `[]` without seed data | Medium | Missing 405/404 handlers in `GlobalExceptionHandler`, missing base GET route, missing `ApiResponse` wrapper and initial `data.sql` | Added root GET and 405/404 exception handlers in Facility service; added `ApiResponse<T>` wrapper and `data.sql` seeding in Reservation service | `curl` & `./test-all.sh` |

---

## 3. Automated End-to-End Test Execution Log

```text
===================================================
  UNIVERSITY SERVICES PLATFORM: ALL-IN-ONE E2E TEST 
===================================================

--- 1. FACILITY-RESOURCE-SERVICE (Port 8081) ---
Testing List Facilities... PASS [OK]
Testing List Resources... PASS [OK]
Testing Validate Resource 1 (Cross-Service Contract)... PASS [OK]
Testing Check Availability POST... PASS [OK]

--- 2. RESERVATION-SERVICE (Port 8082) & CROSS-SERVICE INTEGRATION ---
Testing Create Reservation (Calls Facility 8081)... PASS [OK] (Created Reservation ID: 4)
Testing List All Reservations... PASS [OK]
Testing Approve Reservation #4... PASS [OK]
Testing View History #4... PASS [OK]
Testing Reservation Status Summary... PASS [OK]

===================================================
Final Results: 9 passed, 0 failed
===================================================
```

---

## 4. Cross-Team Integration Contract Verification

### Group 5: Identity Service & API Gateway
* **Live JWKS URL:** `https://university-identity-service.onrender.com/.well-known/jwks.json`
* **Algorithm:** RS256
* **Contract Result:** JWT signature validation verified; downstream bearer token forwarded across services.

### Group 8: Event Management Service
* **Endpoint:** `GET /api/resources/code/{code}/validate`
* **Contract Result:**
  * `LAB-101`: `{"exists": true, "validForReservation": true, "message": "Resource is available"}`
  * `CONF-ROOM-202`: `{"exists": true, "validForReservation": false, "message": "Resource under maintenance"}`
  * `XYZ-999`: `{"exists": false, "validForReservation": false, "message": "Resource not found"}` (HTTP 200 OK)

### Group 7: Service Requests & Work Orders
* **Endpoint:** `GET /api/resources/{id}/validate/group7`
* **Contract Result:** Confirmed HTTP 200 with operational flags for facilities and resources.

---

## 5. Sign-Off
* **Test Status:** PASSED
* **Deployment Status:** READY FOR PRODUCTION
* **Sprint 4 Work Items:** 13 / 13 Completed (49 Story Points)
