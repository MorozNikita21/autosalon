package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.ScoreDTO;
import com.autosalon.backend.entity.Score;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ScoreMapper {

    ScoreDTO toDTO(Score entity);

    Score toEntity(ScoreDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ScoreDTO dto, @MappingTarget Score entity);
}
