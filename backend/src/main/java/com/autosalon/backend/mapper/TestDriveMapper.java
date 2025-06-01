package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.TestDriveDTO;
import com.autosalon.backend.entity.TestDrive;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TestDriveMapper {

    TestDriveDTO toDTO(TestDrive entity);

    TestDrive toEntity(TestDriveDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(TestDriveDTO dto, @MappingTarget TestDrive entity);
}
