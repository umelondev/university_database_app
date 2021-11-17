package com.foxminded.javaee.university.model.security;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String photo;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 5, max = 32, message = "invalid range! must be between 5 and 32 characters")
    private String username;

    private String password;

    private boolean active;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public User(Long id, String photo, String username, String password, boolean active, Role role) {
        this.id = id;
        this.photo = photo;
        this.username = username;
        this.password = password;
        this.active = active;
        this.role = role;
    }
}
