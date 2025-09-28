package com.example.employees_service.controller;

import com.example.employees_service.dtos.EmployeeWithGameDTO;
import com.example.employees_service.models.Employee;
import com.example.employees_service.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee) {
        Employee saved = employeeService.createEmployee(employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable String id){
        return employeeService.findById(id);
    }

    @GetMapping("/all")
    public List<Employee> getAll(){
        return  employeeService.findAll();
    }

    @GetMapping("/with-games")
    public List<EmployeeWithGameDTO> getEmployeesWithGames() {
        return employeeService.findEmployeesWithGames();
    }
}
