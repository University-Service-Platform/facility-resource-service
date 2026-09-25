-- Seed data for Facilities
INSERT INTO facilities (id, code, name, location, description, operating_hours_start, operating_hours_end, active, created_at, updated_at)
VALUES (1, 'ENG-BLDG-A', 'Engineering Complex - Block A', 'North Campus, Sector 4', 'Main engineering building with computer labs and lecture halls', '08:00:00', '22:00:00', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO facilities (id, code, name, location, description, operating_hours_start, operating_hours_end, active, created_at, updated_at)
VALUES (2, 'LIB-CENTRAL', 'Central University Library', 'Central Plaza', 'University library with quiet study rooms and multimedia labs', '07:00:00', '23:00:00', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO facilities (id, code, name, location, description, operating_hours_start, operating_hours_end, active, created_at, updated_at)
VALUES (3, 'SPORTS-CENTER', 'Student Sports Complex', 'South Campus', 'Indoor sports facility with courts and gym equipment', '06:00:00', '21:00:00', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

-- Seed data for Resources
INSERT INTO resources (id, facility_id, code, name, resource_type, location, capacity, active, available, approval_required, operating_hours_start, operating_hours_end, rules_description, created_at, updated_at)
VALUES (1, 1, 'LAB-101', 'High Performance Computing Lab', 'LAB', 'Room A-101', 30, true, true, true, '08:00:00', '20:00:00', 'Lab supervisor approval required. No food or drinks.', NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO resources (id, facility_id, code, name, resource_type, location, capacity, active, available, approval_required, operating_hours_start, operating_hours_end, rules_description, created_at, updated_at)
VALUES (2, 1, 'AUD-MAIN', 'Engineering Main Auditorium', 'AUDITORIUM', 'Block A Ground Floor', 250, true, true, true, '08:00:00', '22:00:00', 'Prior approval required for large university events.', NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO resources (id, facility_id, code, name, resource_type, location, capacity, active, available, approval_required, operating_hours_start, operating_hours_end, rules_description, created_at, updated_at)
VALUES (3, 2, 'STUDY-POD-01', 'Quiet Study Pod 1', 'STUDY_POD', '2nd Floor Quiet Zone', 4, true, true, false, '07:00:00', '23:00:00', 'Maximum 2 hours booking per student per day.', NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;

INSERT INTO resources (id, facility_id, code, name, resource_type, location, capacity, active, available, approval_required, operating_hours_start, operating_hours_end, rules_description, created_at, updated_at)
VALUES (4, 3, 'BASKETBALL-COURT-1', 'Indoor Basketball Court 1', 'SPORTS_FIELD', 'Main Hall', 20, true, true, false, '06:00:00', '21:00:00', 'Appropriate footwear mandatory.', NOW(), NOW())
ON DUPLICATE KEY UPDATE code=code;
