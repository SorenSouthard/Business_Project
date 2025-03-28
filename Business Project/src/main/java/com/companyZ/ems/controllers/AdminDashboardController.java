package com.companyZ.ems.controllers;

import com.companyZ.ems.models.Employee;
import com.companyZ.ems.services.EmployeeService;
import com.companyZ.ems.services.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;

/**
 * Controller class for the Admin Dashboard.
 * Provides functionality for managing employees and generating reports.
 * Implements all CRUD operations and advanced search capabilities.
 */
public class AdminDashboardController {
    private static final Logger logger = LogManager.getLogger(AdminDashboardController.class);
    private final EmployeeService employeeService = new EmployeeService();
    private final ReportService reportService = new ReportService();

    // FXML injected fields for employee table
    @FXML
    private TableView<Employee> employeeTable;
    @FXML
    private TableColumn<Employee, String> empIdColumn;
    @FXML
    private TableColumn<Employee, String> nameColumn;
    @FXML
    private TableColumn<Employee, String> divisionColumn;
    @FXML
    private TableColumn<Employee, String> jobTitleColumn;

    // Search field
    @FXML
    private TextField searchField;

    // FXML injected fields for employee details
    @FXML
    private TextField empIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField ssnField;
    @FXML
    private DatePicker hireDatePicker;
    @FXML
    private TextField salaryField;
    @FXML
    private ComboBox<String> divisionComboBox;
    @FXML
    private ComboBox<String> jobTitleComboBox;

    // FXML injected fields for address
    @FXML
    private TextField streetField;
    @FXML
    private ComboBox<String> cityComboBox;
    @FXML
    private ComboBox<String> stateComboBox;
    @FXML
    private TextField zipField;

    // Status label
    @FXML
    private Label statusLabel;

    /**
     * Initializes the controller.
     * Sets up table columns and loads initial data.
     */
    @FXML
    public void initialize() {
        setupTableColumns();
        loadEmployeeData();
        loadComboBoxData();
        setupTableSelectionListener();
    }

    /**
     * Sets up the employee table columns with appropriate cell factories.
     */
    private void setupTableColumns() {
        empIdColumn.setCellValueFactory(new PropertyValueFactory<>("empId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        divisionColumn.setCellValueFactory(new PropertyValueFactory<>("division"));
        jobTitleColumn.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));
    }

    /**
     * Loads employee data into the table.
     */
    private void loadEmployeeData() {
        try {
            employeeTable.setItems(employeeService.getAllEmployees());
            updateStatus("Employee data loaded successfully");
        } catch (Exception e) {
            logger.error("Error loading employee data", e);
            showError("Failed to load employee data");
        }
    }

    /**
     * Loads data for division and job title combo boxes.
     */
    private void loadComboBoxData() {
        try {
            divisionComboBox.setItems(employeeService.getAllDivisions());
            jobTitleComboBox.setItems(employeeService.getAllJobTitles());
            cityComboBox.setItems(employeeService.getAllCities());
            stateComboBox.setItems(employeeService.getAllStates());
        } catch (Exception e) {
            logger.error("Error loading combo box data", e);
            showError("Failed to load reference data");
        }
    }

    /**
     * Sets up the listener for employee table selection.
     */
    private void setupTableSelectionListener() {
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        displayEmployeeDetails(newSelection);
                    }
                });
    }

    /**
     * Handles the save button action.
     * Creates or updates an employee record.
     */
    @FXML
    private void handleSave() {
        try {
            Employee employee = getEmployeeFromForm();
            if (employee.getEmpId() == null || employee.getEmpId().isEmpty()) {
                employeeService.createEmployee(employee);
                updateStatus("Employee created successfully");
            } else {
                employeeService.updateEmployee(employee);
                updateStatus("Employee updated successfully");
            }
            loadEmployeeData();
        } catch (Exception e) {
            logger.error("Error saving employee", e);
            showError("Failed to save employee: " + e.getMessage());
        }
    }

    /**
     * Handles the delete button action.
     */
    @FXML
    private void handleDelete() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select an employee to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this employee?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    employeeService.deleteEmployee(selected.getEmpId());
                    loadEmployeeData();
                    clearForm();
                    updateStatus("Employee deleted successfully");
                } catch (Exception e) {
                    logger.error("Error deleting employee", e);
                    showError("Failed to delete employee");
                }
            }
        });
    }

    /**
     * Handles the search button action.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        try {
            employeeTable.setItems(employeeService.searchEmployees(searchText));
            updateStatus("Search completed");
        } catch (Exception e) {
            logger.error("Error searching employees", e);
            showError("Search failed");
        }
    }

    /**
     * Generates and displays the pay statement report.
     */
    @FXML
    private void handlePayStatementReport() {
        try {
            reportService.generatePayStatementReport();
            updateStatus("Pay statement report generated");
        } catch (Exception e) {
            logger.error("Error generating pay statement report", e);
            showError("Failed to generate report");
        }
    }

    /**
     * Generates and displays the employee roster report.
     */
    @FXML
    private void handleEmployeeRosterReport() {
        try {
            String filePath = "reports/employee_roster_"
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            reportService.generateEmployeeRoster(filePath);
            updateStatus("Employee roster report generated");
            openReportDirectory();
        } catch (Exception e) {
            logger.error("Error generating employee roster report", e);
            showError("Failed to generate report");
        }
    }

    /**
     * Generates and displays the division summary report.
     */
    @FXML
    private void handleDivisionSummaryReport() {
        try {
            String filePath = "reports/division_summary_"
                    + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
            reportService.generateDivisionSummary(filePath);
            updateStatus("Division summary report generated");
            openReportDirectory();
        } catch (Exception e) {
            logger.error("Error generating division summary report", e);
            showError("Failed to generate report");
        }
    }

    /**
     * Opens the reports directory in the system's file explorer.
     */
    private void openReportDirectory() {
        try {
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }

            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb;

            if (os.contains("win")) {
                pb = new ProcessBuilder("explorer.exe", reportsDir.getAbsolutePath());
            } else if (os.contains("mac")) {
                pb = new ProcessBuilder("open", reportsDir.getAbsolutePath());
            } else {
                pb = new ProcessBuilder("xdg-open", reportsDir.getAbsolutePath());
            }

            pb.start();
        } catch (Exception e) {
            logger.error("Error opening reports directory", e);
        }
    }

    /**
     * Updates the status message.
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Shows an error message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    /**
     * Clears all form fields.
     */
    @FXML
    private void handleClear() {
        clearForm();
        updateStatus("Form cleared");
    }

    /**
     * Clears all form fields.
     */
    private void clearForm() {
        empIdField.clear();
        nameField.clear();
        ssnField.clear();
        hireDatePicker.setValue(null);
        salaryField.clear();
        divisionComboBox.getSelectionModel().clearSelection();
        jobTitleComboBox.getSelectionModel().clearSelection();
        streetField.clear();
        cityComboBox.getSelectionModel().clearSelection();
        stateComboBox.getSelectionModel().clearSelection();
        zipField.clear();
    }

    /**
     * Creates an Employee object from form data.
     */
    private Employee getEmployeeFromForm() {
        Employee employee = new Employee();
        employee.setEmpId(empIdField.getText());
        employee.setName(nameField.getText());
        employee.setSsn(ssnField.getText());
        employee.setHireDate(hireDatePicker.getValue());
        employee.setSalary(Double.parseDouble(salaryField.getText()));
        employee.setDivision(divisionComboBox.getValue());
        employee.setJobTitle(jobTitleComboBox.getValue());
        // Set address information
        employee.setStreet(streetField.getText());
        employee.setCity(cityComboBox.getValue());
        employee.setState(stateComboBox.getValue());
        employee.setZip(zipField.getText());
        return employee;
    }

    /**
     * Displays employee details in the form.
     */
    private void displayEmployeeDetails(Employee employee) {
        empIdField.setText(employee.getEmpId());
        nameField.setText(employee.getName());
        ssnField.setText(employee.getSsn());
        hireDatePicker.setValue(employee.getHireDate());
        salaryField.setText(String.valueOf(employee.getSalary()));
        divisionComboBox.setValue(employee.getDivision());
        jobTitleComboBox.setValue(employee.getJobTitle());
        streetField.setText(employee.getStreet());
        cityComboBox.setValue(employee.getCity());
        stateComboBox.setValue(employee.getState());
        zipField.setText(employee.getZip());
    }
}