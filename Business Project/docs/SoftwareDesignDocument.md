# Software Design Document (SDD)
## Employee Management System - Company Z

## Table of Contents
1. [Introduction](#introduction)
2. [System Architecture](#system-architecture)
3. [Database Design](#database-design)
4. [User Interface Design](#user-interface-design)
5. [Security Design](#security-design)
6. [Class Design](#class-design)
7. [Test Cases](#test-cases)
8. [Sequence Diagrams](#sequence-diagrams)

## 1. Introduction
### 1.1 Purpose
This document provides a detailed software design for Company Z's Employee Management System, which will replace the current dBeaver and MySQL scripts-based system.

### 1.2 Scope
The system will manage approximately 40 full-time employees initially, with scalability to support up to 120 employees within 18 months.

### 1.3 System Overview
The system provides two user roles:
- Admin: Full CRUD access to employee data
- Employee: Read-only access to personal data

## 2. System Architecture
### 2.1 Architecture Overview
- Three-tier architecture:
  - Presentation Layer (JavaFX)
  - Business Logic Layer (Java)
  - Data Layer (MySQL)

### 2.2 Component Diagram
[To be added: Component diagram showing system architecture]

## 3. Database Design
### 3.1 Entity Relationship Diagram
[To be added: Database schema diagram]

### 3.2 Table Structures
#### Core Tables
1. employees
   - empid (PK)
   - name
   - ssn
   - hire_date
   - salary

2. address
   - empid (PK, FK)
   - street
   - city_id (FK)
   - state_id (FK)
   - zip
   - gender
   - race
   - dob
   - phone

3. city
   - city_id (PK)
   - city_name

4. state
   - state_id (PK)
   - state_name

5. employee_division
   - empid (FK)
   - div_ID (FK)

6. division
   - ID (PK)
   - division_name

7. employee_job_titles
   - empid (FK)
   - job_title_id (FK)

8. job_titles
   - job_title_id (PK)
   - title_name

9. payroll
   - payroll_id (PK)
   - empid (FK)
   - pay_date
   - amount

### 3.3 Relationships
[List of all primary/foreign key relationships as specified in requirements]

## 4. User Interface Design
### 4.1 Login Screen
- Username/password input
- Role selection
- Authentication feedback

### 4.2 Admin Dashboard
- Employee search functionality
- CRUD operations interface
- Report generation
- Salary management

### 4.3 Employee Dashboard
- Personal information view
- Pay statement history
- Profile settings

## 5. Security Design
### 5.1 Authentication
- JWT-based authentication
- Role-based access control
- Session management

### 5.2 Database Security
- MySQL user privileges
- Encrypted sensitive data
- Prepared statements for SQL injection prevention

## 6. Class Design
### 6.1 Core Classes
```java
// Abstract base class
public abstract class Person {
    protected String name;
    protected Date dob;
    protected String ssn;
    // Common methods
}

// Employee class
public class Employee extends Person {
    private String empId;
    private Date hireDate;
    private double salary;
    private Address address;
    // Employee-specific methods
}

// Address class
public class Address {
    private String street;
    private City city;
    private State state;
    private String zip;
    // Address methods
}

// Data access interfaces
public interface EmployeeDAO {
    Employee findById(String empId);
    List<Employee> search(SearchCriteria criteria);
    void update(Employee employee);
    // Other CRUD operations
}
```

## 7. Test Cases
### 7.1 Employee Data Update
#### Test Case 1: Valid Employee Update
- **Input**: Valid employee data
- **Expected**: Database updated successfully
- **Validation**: Retrieved data matches updated values

#### Test Case 2: Employee Search
- **Input**: Search criteria (name, DOB, SSN, or empid)
- **Expected**: Matching employee records returned
- **Validation**: Result count and data accuracy

#### Test Case 3: Salary Update
- **Input**: Salary range and increase percentage
- **Expected**: Salaries updated within specified range
- **Validation**: Salary changes reflect specified percentage

## 8. Sequence Diagrams
### 8.1 Salary Increase Process
[To be added: Sequence diagram for salary increase process]

### 8.2 New Employee Addition
[To be added: Sequence diagram for adding new employee]

## Appendix
### A. Technology Stack Details
- Java 17
- JavaFX 17
- MySQL 8.0
- Maven
- JUnit 5
- Log4j 2

### B. Development Guidelines
- Code style: Google Java Style Guide
- Documentation: Javadoc
- Testing: Unit tests required for all business logic
- Version Control: Git with feature branch workflow 