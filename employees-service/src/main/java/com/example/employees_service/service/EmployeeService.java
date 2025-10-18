package com.example.employees_service.service;

import com.example.employees_service.client.GameClient;
import com.example.employees_service.client.TicketClient;
import com.example.employees_service.dtos.*;
import com.example.employees_service.exception.EmployeeNotFoundException;
import com.example.employees_service.mapper.EmployeeMapper;
import com.example.employees_service.models.Employee;
import com.example.employees_service.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GameClient gameClient;

    @Autowired
    private TicketClient ticketClient;

    @Autowired
    private EmployeeMapper employeeMapper;

    //-----------CRUD OPERATIONS------------
    //Esto asegura que cualquier caché dependiente de la lista
    // o datos relacionados se limpie cuando creás/eliminás empleados.
    @CacheEvict(value = {"employees", "employeesWithGames", "employeesWithTickets"},
            allEntries = true)
    public Employee createEmployee(Employee employee){
        return employeeRepository.save(employee);
    }
    @Cacheable(value = "employees", key = "#id")
    public Employee findById(String id){
        return employeeRepository.findById(id)
                .orElseThrow(()-> new EmployeeNotFoundException(id));
    }

    //Esto evita que findAll devuelva datos obsoletos.
    @CacheEvict(value = "employees", allEntries = true)
    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }
    @CacheEvict(value = {"employees", "employeesWithGames", "employeesWithTickets"},
            allEntries = true)
    public void removeEmployee(String id){
        Employee employee= findById(id);
        employeeRepository.deleteById(employee.getId());
    }
    @CachePut(value = "employees", key = "#id")
    public EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO) {
        // 1️⃣ Buscar el empleado existente en la base
        Employee existing = findById(id);

        // 2️⃣ Actualizar solo los campos no nulos del DTO
        employeeMapper.updateFromDTO(employeeDTO, existing);

        // 3️⃣ Guardar los cambios
        Employee saved = employeeRepository.save(existing);

        // 4️⃣ Devolver el DTO actualizado
        return employeeMapper.toDTO(saved);
    }


    //------ OTHER OPERATIONS----------
    // 🔹 Obtener empleados con su juego asignado
    //En caso de que actualizan juegos o asignaciones de empleados.
    @CacheEvict(value = "employeesWithGames", allEntries = true)
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
    //🔹Obtener empleados y contar cuántos tickets vendieron
    @Cacheable(value = "employeesWithTickets")
    public List<EmployeeWithTicketsDTO> findEmployeesWithTickets(){
        // Traemos todos los tickets de todos los juegos desde ticketService
        List<TicketDTO> allTickets = ticketClient.getAll(); // suponer que hay un endpoint global

        return employeeRepository.findAll().stream()
                .map(emp -> {
                    // Contar tickets que correspondan al gameId del empleado
                    long count = allTickets.stream()
                            .filter(ticket -> ticket.getGameId().equals(emp.getGameId()))
                            .count();

                    return EmployeeWithTicketsDTO.builder()
                            .id(emp.getId())
                            .firstName(emp.getFirstName())
                            .lastName(emp.getLastName())
                            .gameId(emp.getGameId())
                            .ticketsSoldInGame(count)
                            .build();
                })
                .sorted(Comparator.comparingLong(EmployeeWithTicketsDTO::getTicketsSoldInGame).reversed())
                .toList();
    }


}
