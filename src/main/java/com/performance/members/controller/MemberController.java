package com.performance.members.controller;

import com.performance.members.dto.LoginDto;
import com.performance.members.dto.MemberRequestDto;
import com.performance.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginDto loginDto) {
        return memberService.login(loginDto)
                .map(token -> ResponseEntity.ok()
                        .header("Authorization", token)
                        .body("success"));
    }

    @PostMapping("/member")
    public Mono<ResponseEntity<?>> save(@RequestBody MemberRequestDto request) {
        return memberService.saveMember(request)
                .map(ResponseEntity::ok);
    }
}
