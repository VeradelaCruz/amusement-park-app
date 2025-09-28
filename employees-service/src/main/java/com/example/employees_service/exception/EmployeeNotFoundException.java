package com.example.employees_service.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String id) {
        super("Employee with id: " +id + "not found.");
    }
}
