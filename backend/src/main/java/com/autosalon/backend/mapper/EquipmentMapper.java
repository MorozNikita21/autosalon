package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.EquipmentDTO;
import com.autosalon.backend.entity.Equipment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EquipmentMapper {

    EquipmentDTO toDTO(Equipment entity);

    Equipment toEntity(EquipmentDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EquipmentDTO dto, @MappingTarget Equipment entity);
}
