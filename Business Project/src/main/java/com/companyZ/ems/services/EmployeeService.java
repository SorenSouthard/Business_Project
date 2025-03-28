package com.companyZ.ems.services;

import com.companyZ.ems.models.Employee;
import com.companyZ.ems.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 * Service class for handling employee-related operations.
 * Provides methods for CRUD operations and data retrieval.
 */
public class EmployeeService {
    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

    /**
     * Retrieves all employees from the database.
     *
     * @return ObservableList of all employees
     * @throws Exception if database operation fails
     */
    public ObservableList<Employee> getAllEmployees() throws Exception {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String sql = "SELECT e.*, a.street, a.zip, c.city_name, s.state_name, " +
                "d.division_name, j.title_name " +
                "FROM employees e " +
                "JOIN address a ON e.empid = a.empid " +
                "JOIN city c ON a.city_id = c.city_id " +
                "JOIN state s ON a.state_id = s.state_id " +
                "JOIN employee_division ed ON e.empid = ed.empid " +
                "JOIN division d ON ed.div_ID = d.ID " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles j ON ejt.job_title_id = j.job_title_id";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                employees.add(createEmployeeFromResultSet(rs));
            }
        }
        return employees;
    }

    /**
     * Creates a new employee record.
     *
     * @param employee The employee to create
     * @throws Exception if database operation fails
     */
    public void createEmployee(Employee employee) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Insert employee record
            String sql = "INSERT INTO employees (empid, name, ssn, hire_date, salary) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, employee.getEmpId());
                stmt.setString(2, employee.getName());
                stmt.setString(3, employee.getSsn());
                stmt.setObject(4, employee.getHireDate());
                stmt.setDouble(5, employee.getSalary());
                stmt.executeUpdate();
            }

            // Insert address record
            insertAddress(conn, employee);

            // Insert division and job title associations
            insertDivisionAndJobTitle(conn, employee);

            conn.commit();
            logger.info("Employee created successfully: {}", employee.getEmpId());
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            logger.error("Error creating employee", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Updates an existing employee record.
     *
     * @param employee The employee to update
     * @throws Exception if database operation fails
     */
    public void updateEmployee(Employee employee) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update employee record
            String sql = "UPDATE employees SET name=?, ssn=?, hire_date=?, salary=? " +
                    "WHERE empid=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, employee.getName());
                stmt.setString(2, employee.getSsn());
                stmt.setObject(3, employee.getHireDate());
                stmt.setDouble(4, employee.getSalary());
                stmt.setString(5, employee.getEmpId());
                stmt.executeUpdate();
            }

            // Update address, division, and job title
            updateAddress(conn, employee);
            updateDivisionAndJobTitle(conn, employee);

            conn.commit();
            logger.info("Employee updated successfully: {}", employee.getEmpId());
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            logger.error("Error updating employee", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Deletes an employee record.
     *
     * @param empId The ID of the employee to delete
     * @throws Exception if database operation fails
     */
    public void deleteEmployee(String empId) throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Delete associated records first
            deleteAssociatedRecords(conn, empId);

            // Delete employee record
            String sql = "DELETE FROM employees WHERE empid=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, empId);
                stmt.executeUpdate();
            }

            conn.commit();
            logger.info("Employee deleted successfully: {}", empId);
        } catch (Exception e) {
            if (conn != null) {
                conn.rollback();
            }
            logger.error("Error deleting employee", e);
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                DatabaseConnection.closeConnection(conn);
            }
        }
    }

    /**
     * Searches for employees based on search criteria.
     *
     * @param searchText The text to search for
     * @return ObservableList of matching employees
     * @throws Exception if database operation fails
     */
    public ObservableList<Employee> searchEmployees(String searchText) throws Exception {
        ObservableList<Employee> employees = FXCollections.observableArrayList();
        String sql = "SELECT e.*, a.street, a.zip, c.city_name, s.state_name, " +
                "d.division_name, j.title_name " +
                "FROM employees e " +
                "JOIN address a ON e.empid = a.empid " +
                "JOIN city c ON a.city_id = c.city_id " +
                "JOIN state s ON a.state_id = s.state_id " +
                "JOIN employee_division ed ON e.empid = ed.empid " +
                "JOIN division d ON ed.div_ID = d.ID " +
                "JOIN employee_job_titles ejt ON e.empid = ejt.empid " +
                "JOIN job_titles j ON ejt.job_title_id = j.job_title_id " +
                "WHERE e.empid LIKE ? OR e.name LIKE ? OR e.ssn LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchText + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    employees.add(createEmployeeFromResultSet(rs));
                }
            }
        }
        return employees;
    }

    /**
     * Creates an Employee object from a ResultSet row.
     */
    private Employee createEmployeeFromResultSet(ResultSet rs) throws Exception {
        Employee employee = new Employee();
        employee.setEmpId(rs.getString("empid"));
        employee.setName(rs.getString("name"));
        employee.setSsn(rs.getString("ssn"));
        employee.setHireDate(rs.getObject("hire_date", LocalDate.class));
        employee.setSalary(rs.getDouble("salary"));
        employee.setDivision(rs.getString("division_name"));
        employee.setJobTitle(rs.getString("title_name"));
        employee.setStreet(rs.getString("street"));
        employee.setCity(rs.getString("city_name"));
        employee.setState(rs.getString("state_name"));
        employee.setZip(rs.getString("zip"));
        return employee;
    }

    /**
     * Retrieves all divisions from the database.
     */
    public ObservableList<String> getAllDivisions() throws Exception {
        ObservableList<String> divisions = FXCollections.observableArrayList();
        String sql = "SELECT division_name FROM division ORDER BY division_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                divisions.add(rs.getString("division_name"));
            }
        }
        return divisions;
    }

    /**
     * Retrieves all job titles from the database.
     */
    public ObservableList<String> getAllJobTitles() throws Exception {
        ObservableList<String> titles = FXCollections.observableArrayList();
        String sql = "SELECT title_name FROM job_titles ORDER BY title_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                titles.add(rs.getString("title_name"));
            }
        }
        return titles;
    }

    /**
     * Retrieves all cities from the database.
     */
    public ObservableList<String> getAllCities() throws Exception {
        ObservableList<String> cities = FXCollections.observableArrayList();
        String sql = "SELECT city_name FROM city ORDER BY city_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                cities.add(rs.getString("city_name"));
            }
        }
        return cities;
    }

    /**
     * Retrieves all states from the database.
     */
    public ObservableList<String> getAllStates() throws Exception {
        ObservableList<String> states = FXCollections.observableArrayList();
        String sql = "SELECT state_name FROM state ORDER BY state_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                states.add(rs.getString("state_name"));
            }
        }
        return states;
    }

    // Helper methods for database operations
    private void insertAddress(Connection conn, Employee employee) throws Exception {
        // Implementation details for inserting address
    }

    private void insertDivisionAndJobTitle(Connection conn, Employee employee) throws Exception {
        // Implementation details for inserting division and job title associations
    }

    private void updateAddress(Connection conn, Employee employee) throws Exception {
        // Implementation details for updating address
    }

    private void updateDivisionAndJobTitle(Connection conn, Employee employee) throws Exception {
        // Implementation details for updating division and job title associations
    }

    private void deleteAssociatedRecords(Connection conn, String empId) throws Exception {
        // Implementation details for deleting associated records
    }
}