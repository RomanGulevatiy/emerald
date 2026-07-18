package me.romangulevatiy.emerald.dto.mapper;

import me.romangulevatiy.emerald.dto.response.PageResponse;
import me.romangulevatiy.emerald.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "pageNumber", expression = "java(page.getNumber())")
    @Mapping(target = "pageSize", expression = "java(page.getSize())")
    PageResponse<UserResponse> toPageResponse(Page<UserResponse> page);
}
