package com.performance.members.mapper;

import com.performance.members.dto.MemberRequestDto;
import com.performance.members.dto.MemberResponseDto;
import com.performance.members.entity.Members;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {
    public Members toEntity(MemberRequestDto dto) {
        return Members.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

    public MemberResponseDto toDto(Members member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .created(member.getCreated().toString())
                .build();
    }
}
