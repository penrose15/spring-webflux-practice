package com.performance.members.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Table("members")
public class Members {

    @Id
    private Long id;

    private String name;

    private String email;

    private String password;

    @Column(value = "roles")
    private String role;

    private LocalDateTime created;

    @Builder
    public Members(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "USER";
        this.created = LocalDateTime.now();
    }

    public void encodePassword(String password) {
        if(password != null) this.password = password;
    }
}
