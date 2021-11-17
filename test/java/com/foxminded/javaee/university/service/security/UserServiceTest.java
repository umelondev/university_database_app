package com.foxminded.javaee.university.service.security;

import com.foxminded.javaee.university.model.Student;
import com.foxminded.javaee.university.model.security.Role;
import com.foxminded.javaee.university.model.security.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void findByUsername() {
        User user = new User(1l, "images/user/admin_default.png", "admin", "admin4ik",
                true, Role.ADMIN);
        assertEquals(user, userService.findByUsername("admin"));
    }
}