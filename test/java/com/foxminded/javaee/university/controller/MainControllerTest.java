package com.foxminded.javaee.university.controller;

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
class MainControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(view().name("index"))
                .andExpect(xpath("//div[@id='clss-card']/h4").string(String.valueOf(2)))
                .andExpect(xpath("//div[@id='grp-card']/h4").string(String.valueOf(2)))
                .andExpect(xpath("//div[@id='sdl-card']/h4").string(String.valueOf(2)))
                .andExpect(xpath("//div[@id='stdt-card']/h4").string(String.valueOf(2)))
                .andExpect(xpath("//div[@id='sbjct-card']/h4").string(String.valueOf(2)))
                .andExpect(xpath("//div[@id='tchr-card']/h4").string(String.valueOf(2)));
    }

    @Test
    void GET_return404WhenPageNotFound() throws Exception {
        this.mockMvc.perform(get("/qwerty"))
                .andExpect(status().isNotFound());
    }
}
