package com.example.employees_service.service;

import com.example.employees_service.models.Employee;
import com.example.employees_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    public Employee findById(String id){
        return employeeRepository.findById(id).orElseThrow();
    }

}
