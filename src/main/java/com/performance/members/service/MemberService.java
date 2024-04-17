package com.performance.members.service;

import com.performance.members.mapper.MemberMapper;
import com.performance.members.repository.MembersRepository;
import com.performance.members.dto.LoginDto;
import com.performance.members.dto.MemberRequestDto;
import com.performance.members.dto.MemberResponseDto;
import com.performance.members.entity.Members;
import com.performance.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MembersRepository membersRepository;
    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public Mono<Long> saveMember(MemberRequestDto memberRequestDto) {
        Mono<MemberRequestDto> memberReq = membersRepository.findByEmail(memberRequestDto.email())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("already exist"));
                    } else {
                        return Mono.just(memberRequestDto);
                    }
                });
        Mono<Members> members = memberReq
                .map(memberMapper::toEntity)
                .map(member -> {
                    member.encodePassword(passwordEncoder.encode(member.getPassword()));
                    return member;
                });
        return members.flatMap(membersRepository::save)
                .doOnSuccess(m -> log.info("saved success"))
                .map(Members::getId);

    }

    public Mono<String> login(LoginDto loginDto) {
        return membersRepository.findByEmail(loginDto.email())
                .filter(member -> passwordEncoder.matches(loginDto.password(), member.getPassword()))
                .doOnSuccess(m -> log.info("pw match"))
                .flatMap(members -> jwtTokenProvider.createAccessToken(members.getEmail(), members.getRole()))
                .switchIfEmpty(Mono.error(new NoSuchElementException("unauthorized")))
                .doOnSuccess(m -> log.info("saved success"));
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
