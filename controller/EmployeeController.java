package com.examly.springapp.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.examly.springapp.model.Employee;
import com.examly.springapp.repository.EmployeeRepository;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.status(201).body(employeeRepository.save(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/groupBy/designation")
    public ResponseEntity<Map<String, List<Employee>>> getEmployeesGroupByDesignation() {
        List<Employee> employees = employeeRepository.findAll();
        Map<String, List<Employee>> groupedEmployees = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDesignation));
        return ResponseEntity.ok(groupedEmployees);
    }

    @GetMapping("/findBy/{attribute}")
    public ResponseEntity<List<Employee>> getEmployeesByAttribute(
            @PathVariable String attribute,
            @RequestParam String value) {
        List<Employee> employees;
        switch (attribute.toLowerCase()) {
            case "name":
                employees = employeeRepository.findByName(value);
                break;
            case "designation":
                employees = employeeRepository.findByDesignation(value);
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/salaryRange")
    public ResponseEntity<List<Employee>> getEmployeesBySalaryRange(
            @RequestParam double minSalary,
            @RequestParam double maxSalary) {
        return ResponseEntity.ok(employeeRepository.findBySalaryBetween(minSalary, maxSalary));
    }
} 