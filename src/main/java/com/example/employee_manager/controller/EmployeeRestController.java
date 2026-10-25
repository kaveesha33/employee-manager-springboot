
package com.example.employee_manager.controller;

import com.example.employee_manager.model.Employee;
import com.example.employee_manager.service.EmployeeService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeRestController {

    private final EmployeeService employeeService;

    public EmployeeRestController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // READ: Get all employees
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // READ: Get one employee
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(
            @PathVariable Long id) {

        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREATE: Save a new employee
    @PostMapping
    public ResponseEntity<?> createEmployee(
            @Valid @RequestBody Employee employee) {

        String email = employee.getEmail().trim();

        if (employeeService.emailExists(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "message",
                        "This email is already registered."
                    ));
        }

        employee.setEmail(email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.saveEmployee(employee));
    }

    // UPDATE: Modify an existing employee
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody Employee employee) {

        if (!employeeService.employeeExists(id)) {
            return ResponseEntity.notFound().build();
        }

        String email = employee.getEmail().trim();

        if (employeeService.emailExistsForAnotherEmployee(email, id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "message",
                        "This email is already registered."
                    ));
        }

        employee.setId(id);
        employee.setEmail(email);

        return ResponseEntity.ok(
                employeeService.saveEmployee(employee));
    }

    // DELETE: Remove an employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable Long id) {

        if (!employeeService.employeeExists(id)) {
            return ResponseEntity.notFound().build();
        }

        employeeService.deleteEmployeeById(id);

        return ResponseEntity.noContent().build();
    }
}
