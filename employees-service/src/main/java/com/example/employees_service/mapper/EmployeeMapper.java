package com.example.employees_service.mapper;

import com.example.employees_service.dtos.EmployeeDTO;
import com.example.employees_service.models.Employee;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface  EmployeeMapper {

    EmployeeDTO toDTO(Employee employee);
    Employee toEntity(EmployeeDTO dto);
    // 🔹 Actualización parcial
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(EmployeeDTO dto, @MappingTarget Employee employee);
}
