package com.performance.members.dto;

import lombok.Builder;

public record MemberResponseDto(Long id, String email, String created) {

    @Builder
    public MemberResponseDto {
    }
}
