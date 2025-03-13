package com.examly.springapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examly.springapp.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDesignation(String designation);
    List<Employee> findByName(String name);
    List<Employee> findBySalaryBetween(double minSalary, double maxSalary);
} 