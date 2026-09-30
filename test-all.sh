#!/bin/bash
set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

echo -e "${BLUE}===================================================${NC}"
echo -e "${BLUE}  UNIVERSITY SERVICES PLATFORM: ALL-IN-ONE E2E TEST ${NC}"
echo -e "${BLUE}===================================================${NC}\n"

PASS=0
FAIL=0

run_test() {
    local name="$1"
    local cmd="$2"
    local expected="$3"
    
    echo -n "Testing $name... "
    local output
    output=$(eval "$cmd" 2>&1)
    
    if echo "$output" | grep -q "$expected"; then
        echo -e "${GREEN}PASS [OK]${NC}"
        PASS=$((PASS + 1))
    else
        echo -e "${RED}FAIL${NC}"
        echo "   Output: $output"
        FAIL=$((FAIL + 1))
    fi
}

echo -e "--- 1. FACILITY-RESOURCE-SERVICE (Port 8081) ---"
run_test "List Facilities" "curl -s http://localhost:8081/api/facilities" "ENG-BLDG-A"
run_test "List Resources" "curl -s http://localhost:8081/api/resources" "LAB-101"
run_test "Validate Resource 1 (Cross-Service Contract)" "curl -s http://localhost:8081/api/resources/1/validate" '"validForReservation":true'
run_test "Check Availability POST" 'curl -s -X POST http://localhost:8081/api/resources/check-availability -H "Content-Type: application/json" -d "{\"resourceId\":1,\"date\":\"2026-12-01\",\"startTime\":\"09:00:00\",\"endTime\":\"11:00:00\",\"attendees\":10,\"userRole\":\"STUDENT\"}"' '"available":true'

echo -e "\n--- 2. RESERVATION-SERVICE (Port 8082) & CROSS-SERVICE INTEGRATION ---"
# Generate unique timeslot to prevent 409 conflict
RAND_OFFSET=$(( RANDOM % 1000 + 1 ))
START_HOUR=$(( (RANDOM % 8) + 8 ))
END_HOUR=$(( START_HOUR + 1 ))
START_TIME=$(printf "2027-01-%02dT%02d:00:00" $(( (RAND_OFFSET % 25) + 1 )) $START_HOUR)
END_TIME=$(printf "2027-01-%02dT%02d:00:00" $(( (RAND_OFFSET % 25) + 1 )) $END_HOUR)

CREATE_RES=$(curl -s -X POST "http://localhost:8082/api/v1/reservations" \
  -H "Authorization: Bearer dev-token" \
  -H "Content-Type: application/json" \
  -d "{
    \"resourceId\": 1,
    \"requesterId\": \"student-e2e\",
    \"startTime\": \"$START_TIME\",
    \"endTime\": \"$END_TIME\",
    \"purpose\": \"Automated E2E Test Booking\",
    \"expectedAttendees\": 10
  }")

RES_ID=$(echo "$CREATE_RES" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)

if [ -n "$RES_ID" ]; then
    echo -e "Testing Create Reservation (Calls Facility 8081)... ${GREEN}PASS [OK] (Created Reservation ID: $RES_ID)${NC}"
    PASS=$((PASS + 1))
    run_test "List All Reservations" "curl -s -H 'Authorization: Bearer dev-token' http://localhost:8082/api/v1/reservations" "$RES_ID"
    run_test "Approve Reservation #$RES_ID" "curl -s -X POST http://localhost:8082/api/v1/reservations/$RES_ID/approve -H 'Authorization: Bearer dev-token' -H 'Content-Type: application/json' -d '{\"actionBy\":\"manager-alex\",\"reason\":\"Automated Approval\"}'" '"status":"APPROVED"'
    run_test "View History #$RES_ID" "curl -s -H 'Authorization: Bearer dev-token' http://localhost:8082/api/v1/reservations/$RES_ID/history" '"action":"APPROVED"'
else
    echo -e "Testing Create Reservation... ${RED}FAIL${NC}"
    echo "   Output: $CREATE_RES"
    FAIL=$((FAIL + 1))
fi

run_test "Reservation Status Summary" "curl -s -H 'Authorization: Bearer dev-token' http://localhost:8082/api/v1/reservations/summary/status" "approved"

echo -e "\n${BLUE}===================================================${NC}"
echo -e "Final Results: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"
echo -e "${BLUE}===================================================${NC}"
