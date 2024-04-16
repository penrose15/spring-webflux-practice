package com.performance.members.service;

import com.performance.members.mapper.MemberMapper;
import com.performance.members.repository.MembersRepository;
import com.performance.members.dto.LoginDto;
import com.performance.members.dto.MemberRequestDto;
import com.performance.members.dto.MemberResponseDto;
import com.performance.members.entity.Members;
import com.performance.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MembersRepository membersRepository;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<Long> saveMember(MemberRequestDto memberRequestDto) {
        return membersRepository
                .save(memberMapper.toEntity(memberRequestDto))
                .flatMap(member -> Mono.just(member.getId()));
    }

    public Mono<String> login(LoginDto loginDto) {
        return membersRepository.findByEmail(loginDto.email())
                .filter(member -> passwordEncoder.matches(loginDto.password(), member.getPassword()))
                .flatMap(members -> Mono.just(jwtTokenProvider.createAccessToken(members.getName(), members.getRole())))
                .switchIfEmpty(Mono.error(new NoSuchElementException("unauthorized")));
    }

    public Mono<MemberResponseDto> getMemberById(Long id) {
        return findById(id)
                .map(memberMapper::toDto);
    }

    public Mono<Members> findById(Long id) {
        return membersRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("member not found")));
    }

    public Mono<Members> findByEmail(String email) {
        return membersRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new NoSuchElementException("member not found")));
    }
}
