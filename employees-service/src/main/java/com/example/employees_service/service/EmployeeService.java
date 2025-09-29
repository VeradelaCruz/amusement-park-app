package com.example.employees_service.service;

import com.example.employees_service.client.GameClient;
import com.example.employees_service.dtos.EmployeeWithGameDTO;
import com.example.employees_service.dtos.GameDTO;
import com.example.employees_service.exception.EmployeeNotFoundException;
import com.example.employees_service.models.Employee;
import com.example.employees_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GameClient gameClient;

    //CRUD OPERATIONS:
    public Employee createEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee findById(String id){
        return employeeRepository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));
    }

    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }

    public void removeEmployee(String id){
        employeeRepository.deleteById(id);
    }

    // 🔹 Metodo que devuelve la lista de empleados con su juego asignado
    public List<EmployeeWithGameDTO> findEmployeesWithGames() {
        return employeeRepository.findAll().stream()

                // 1️⃣ Nos quedamos solo con los empleados que tienen un juego asignado
                .filter(e -> e.getGameId() != null)

                // 2️⃣ Transformamos cada Employee en un DTO que combine datos de empleado + juego
                .map(e -> {
                    // Llamamos al microservicio de juegos para traer el nombre
                    GameDTO game = gameClient.getGameById(e.getGameId());

                    // Creamos el DTO de respuesta
                    return EmployeeWithGameDTO.builder()
                            .id(e.getId())
                            .username(e.getUsername())
                            .gameName(game.getGameName())
                            .build();
                })

                // 3️⃣ Convertimos el stream en una lista final
                .collect(Collectors.toList());
    }

}
