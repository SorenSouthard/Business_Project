-- Create database
CREATE DATABASE IF NOT EXISTS company_z_ems;
USE company_z_ems;

-- Create state table
CREATE TABLE state (
    state_id INT AUTO_INCREMENT PRIMARY KEY,
    state_name VARCHAR(50) NOT NULL UNIQUE
);

-- Create city table
CREATE TABLE city (
    city_id INT AUTO_INCREMENT PRIMARY KEY,
    city_name VARCHAR(100) NOT NULL,
    state_id INT,
    FOREIGN KEY (state_id) REFERENCES state(state_id)
);

-- Create division table
CREATE TABLE division (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    division_name VARCHAR(100) NOT NULL UNIQUE
);

-- Create job_titles table
CREATE TABLE job_titles (
    job_title_id INT AUTO_INCREMENT PRIMARY KEY,
    title_name VARCHAR(100) NOT NULL UNIQUE
);

-- Create employees table
CREATE TABLE employees (
    empid VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    ssn VARCHAR(11) NOT NULL UNIQUE,
    hire_date DATE NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create address table with demographic information
CREATE TABLE address (
    empid VARCHAR(10) PRIMARY KEY,
    street VARCHAR(200) NOT NULL,
    city_id INT NOT NULL,
    state_id INT NOT NULL,
    zip VARCHAR(10) NOT NULL,
    gender VARCHAR(20),
    race VARCHAR(50),
    dob DATE NOT NULL,
    phone VARCHAR(15),
    FOREIGN KEY (empid) REFERENCES employees(empid),
    FOREIGN KEY (city_id) REFERENCES city(city_id),
    FOREIGN KEY (state_id) REFERENCES state(state_id)
);

-- Create employee_division table
CREATE TABLE employee_division (
    empid VARCHAR(10),
    div_ID INT,
    PRIMARY KEY (empid, div_ID),
    FOREIGN KEY (empid) REFERENCES employees(empid),
    FOREIGN KEY (div_ID) REFERENCES division(ID)
);

-- Create employee_job_titles table
CREATE TABLE employee_job_titles (
    empid VARCHAR(10),
    job_title_id INT,
    PRIMARY KEY (empid, job_title_id),
    FOREIGN KEY (empid) REFERENCES employees(empid),
    FOREIGN KEY (job_title_id) REFERENCES job_titles(job_title_id)
);

-- Create payroll table
CREATE TABLE payroll (
    payroll_id INT AUTO_INCREMENT PRIMARY KEY,
    empid VARCHAR(10),
    pay_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (empid) REFERENCES employees(empid)
);

-- Create users table for authentication
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'EMPLOYEE') NOT NULL,
    empid VARCHAR(10),
    FOREIGN KEY (empid) REFERENCES employees(empid)
);

-- Create indexes for performance
CREATE INDEX idx_employee_name ON employees(name);
CREATE INDEX idx_employee_ssn ON employees(ssn);
CREATE INDEX idx_employee_salary ON employees(salary);
CREATE INDEX idx_payroll_date ON payroll(pay_date);

-- Create views for reporting
CREATE VIEW employee_pay_statement AS
SELECT 
    e.empid,
    e.name,
    p.pay_date,
    p.amount,
    jt.title_name as job_title,
    d.division_name
FROM 
    employees e
    JOIN payroll p ON e.empid = p.empid
    JOIN employee_job_titles ejt ON e.empid = ejt.empid
    JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
    JOIN employee_division ed ON e.empid = ed.empid
    JOIN division d ON ed.div_ID = d.ID;

CREATE VIEW division_monthly_pay AS
SELECT 
    d.division_name,
    DATE_FORMAT(p.pay_date, '%Y-%m') as pay_month,
    SUM(p.amount) as total_amount
FROM 
    division d
    JOIN employee_division ed ON d.ID = ed.div_ID
    JOIN payroll p ON ed.empid = p.empid
GROUP BY 
    d.division_name, 
    DATE_FORMAT(p.pay_date, '%Y-%m');

CREATE VIEW job_title_monthly_pay AS
SELECT 
    jt.title_name,
    DATE_FORMAT(p.pay_date, '%Y-%m') as pay_month,
    SUM(p.amount) as total_amount
FROM 
    job_titles jt
    JOIN employee_job_titles ejt ON jt.job_title_id = ejt.job_title_id
    JOIN payroll p ON ejt.empid = p.empid
GROUP BY 
    jt.title_name, 
    DATE_FORMAT(p.pay_date, '%Y-%m');

-- Create stored procedures for common operations
DELIMITER //

CREATE PROCEDURE update_salary_range(
    IN min_salary DECIMAL(10,2),
    IN max_salary DECIMAL(10,2),
    IN increase_percentage DECIMAL(5,2)
)
BEGIN
    UPDATE employees 
    SET salary = salary * (1 + increase_percentage/100)
    WHERE salary >= min_salary AND salary < max_salary;
END //

CREATE PROCEDURE get_employee_details(
    IN p_empid VARCHAR(10)
)
BEGIN
    SELECT 
        e.*,
        a.street,
        c.city_name,
        s.state_name,
        a.zip,
        a.gender,
        a.race,
        a.dob,
        a.phone,
        jt.title_name as job_title,
        d.division_name
    FROM 
        employees e
        JOIN address a ON e.empid = a.empid
        JOIN city c ON a.city_id = c.city_id
        JOIN state s ON a.state_id = s.state_id
        JOIN employee_job_titles ejt ON e.empid = ejt.empid
        JOIN job_titles jt ON ejt.job_title_id = jt.job_title_id
        JOIN employee_division ed ON e.empid = ed.empid
        JOIN division d ON ed.div_ID = d.ID
    WHERE 
        e.empid = p_empid;
END //

DELIMITER ; 