package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.CarDTO;
import com.autosalon.backend.entity.Car;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CarMapper {

    CarDTO toDTO(Car entity);

    Car toEntity(CarDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CarDTO dto, @MappingTarget Car entity);
}
