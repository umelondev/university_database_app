package com.foxminded.javaee.university.controller.security;

import com.foxminded.javaee.university.model.security.User;
import com.foxminded.javaee.university.service.security.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
@WithMockUser(username = "admin", authorities = "ADMIN", password = "admin4ik")
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        this.mockMvc.perform(get("/users"))
                .andExpect(view().name("html/user/all-users"))
                .andExpect(model().attribute("users", userService.findAll()))
                .andExpect(xpath("//*[@id='users-table']/tbody/tr")
                        .nodeCount(2));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        User user = userService.findById(1L).get();
        this.mockMvc.perform(get("/users/1"))
                .andExpect(view().name("html/user/detail-user"))
                .andExpect(model().attribute("username", user.getUsername()));

    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/users/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/users"))
                .andExpect(model().attribute("button", "Back to users list"))
                .andExpect(model().attribute("message",
                        String.format("User with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/users/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/users"))
                .andExpect(model().attribute("button", "Back to users list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }
}