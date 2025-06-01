package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.ModelDTO;
import com.autosalon.backend.entity.Model;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelMapper {

    ModelDTO toDTO(Model entity);

    Model toEntity(ModelDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ModelDTO dto, @MappingTarget Model entity);
}
