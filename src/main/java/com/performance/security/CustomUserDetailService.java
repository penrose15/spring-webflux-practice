package com.performance.security;

import com.performance.members.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements ReactiveUserDetailsService {
    private final MembersRepository membersRepository;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("customUserDetailService.findByUsername({})", username);

        return membersRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new NoSuchElementException("member not found")))
                .map(members -> User.builder()
                        .username(members.getEmail())
                        .password(members.getPassword())
                        .roles(members.getRole())
                        .build());
    }
}
