-- Initialize the Employee Management System database with test data
-- This script populates the database with initial required data including:
-- - Geographic data (states and cities)
-- - Organizational structure (divisions and job titles)
-- - Admin user account for system access

USE company_z_ems;

-- Populate geographic data
-- States table: Contains US states where employees are located
INSERT INTO state (state_name) VALUES 
('California'),  -- State ID: 1
('New York'),    -- State ID: 2
('Texas');       -- State ID: 3

-- Cities table: Major cities within the states above
-- References state IDs from the states table
INSERT INTO city (city_name, state_id) VALUES 
('San Francisco', 1),  -- City ID: 1, California
('Los Angeles', 1),    -- City ID: 2, California
('New York City', 2),  -- City ID: 3, New York
('Austin', 3);         -- City ID: 4, Texas

-- Organizational structure
-- Divisions: Main business units of the company
INSERT INTO division (division_name) VALUES 
('Engineering'),       -- Division ID: 1
('Sales'),            -- Division ID: 2
('Human Resources'),   -- Division ID: 3
('Marketing');        -- Division ID: 4

-- Job titles: Available positions within the company
INSERT INTO job_titles (title_name) VALUES 
('Software Engineer'),    -- Job Title ID: 1
('Sales Manager'),       -- Job Title ID: 2
('HR Manager'),          -- Job Title ID: 3
('Marketing Specialist'); -- Job Title ID: 4

-- System Administrator Account Setup
-- Create admin employee record
INSERT INTO employees (empid, name, ssn, hire_date, salary) VALUES 
('ADMIN001', 'System Administrator', '000-00-0000', '2024-01-01', 100000.00);

-- Admin's address and demographic information
-- Uses San Francisco (city_id: 1) and California (state_id: 1)
INSERT INTO address (empid, street, city_id, state_id, zip, gender, race, dob, phone) VALUES 
('ADMIN001', '123 Admin St', 1, 1, '94105', 'Not Specified', 'Not Specified', '1990-01-01', '555-0000');

-- Assign admin to HR Manager role and HR division
INSERT INTO employee_job_titles (empid, job_title_id) VALUES 
('ADMIN001', 3);  -- Job Title ID 3 = HR Manager

INSERT INTO employee_division (empid, div_ID) VALUES 
('ADMIN001', 3);  -- Division ID 3 = Human Resources

-- Create admin user account
-- Username: admin
-- Password: admin123 (hashed)
-- The password hash is pre-computed using SecurityUtils.hashPassword()
INSERT INTO users (username, password_hash, role, empid) VALUES 
('admin', 'YWRtaW4xMjM=$8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN', 'ADMIN001'); 