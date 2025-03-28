# Employee Management System (EMS) - Company Z

## Project Overview
This is a comprehensive employee management system designed for Company Z to manage their employee data, payroll, and HR operations. The system supports both administrative and employee access with different permission levels.

## Features
- User Authentication & Authorization (Admin/Employee roles)
- Employee Data Management (CRUD operations)
- Advanced Search Functionality
- Salary Management
- Reporting System
- Secure Database Integration

## Technology Stack
- Backend: Java
- Database: MySQL
- Frontend: JavaFX
- Authentication: JWT (JSON Web Tokens)
- Build Tool: Maven

## Project Structure
```
employee-management-system/
├── docs/                    # Documentation files
│   ├── design/             # UML diagrams and design docs
│   └── database/           # Database schemas and scripts
├── src/
│   ├── main/
│   │   ├── java/          # Java source files
│   │   ├── resources/     # Configuration files
│   │   └── ui/           # JavaFX UI files
│   └── test/             # Test files
└── pom.xml               # Maven configuration
```

## Setup Instructions
1. Install Java JDK 17 or higher
2. Install MySQL 8.0 or higher
3. Clone the repository
4. Configure database connection in `src/main/resources/application.properties`
5. Run `mvn clean install` to build the project
6. Execute `mvn javafx:run` to start the application

## Database Configuration
The system uses MySQL as its database. Initial setup scripts are provided in the `docs/database` directory.

## Security
- Role-based access control (RBAC)
- Encrypted password storage
- Secure session management
- Input validation and sanitization

## Documentation
Detailed documentation including:
- Software Design Document (SDD)
- Database Schema
- API Documentation
- User Manual
- Test Cases

## Team Members
[To be filled with team members]

## License
Proprietary - Company Z 