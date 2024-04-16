package com.performance.members.repository;

import com.performance.members.entity.Members;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MembersRepository extends R2dbcRepository<Members, Long> {
    Mono<Members> findByEmail(String email);
}
