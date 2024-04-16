package com.performance.security;

import com.performance.members.repository.MembersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class CustomUserDetailService implements ReactiveUserDetailsService {
    private final MembersRepository membersRepository;
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return membersRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(new NoSuchElementException("member not found")))
                .map(members -> new CustomUserDetails(members.getEmail(), members.getRole(), members.getPassword()));
    }
}
