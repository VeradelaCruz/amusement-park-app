package com.example.employees_service.controller;

import com.example.employees_service.dtos.EmployeeDTO;
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

    @PostMapping("/add")
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String id){
        employeeService.removeEmployee(id);
        return ResponseEntity.ok().body("Employee removed successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String id,
            @RequestBody EmployeeDTO employeeDTO) {

        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        return ResponseEntity.ok(updated);
    }

    
    @GetMapping("/with-games")
    public List<EmployeeWithGameDTO> getEmployeesWithGames() {
        return employeeService.findEmployeesWithGames();
    }
}
