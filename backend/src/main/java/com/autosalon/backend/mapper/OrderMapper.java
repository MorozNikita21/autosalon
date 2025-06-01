package com.autosalon.backend.mapper;

import com.autosalon.backend.dto.OrderDTO;
import com.autosalon.backend.entity.Orders;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderDTO toDTO(Orders entity);

    Orders toEntity(OrderDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(OrderDTO dto, @MappingTarget Orders entity);
}
