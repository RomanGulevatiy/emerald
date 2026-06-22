package me.romangulevatiy.emerald.mapper;

import me.romangulevatiy.emerald.dto.PageResponse;
import me.romangulevatiy.emerald.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public PageResponse<UserResponse> toPageResponse(Page<UserResponse> page) {
        return PageResponse.<UserResponse>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
