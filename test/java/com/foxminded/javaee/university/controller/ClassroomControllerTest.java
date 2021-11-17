package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Classroom;
import com.foxminded.javaee.university.service.ClassroomService;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
@WithMockUser(username = "admin", authorities = "ADMIN", password = "admin4ik")
class ClassroomControllerTest {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        this.mockMvc.perform(get("/classrooms"))
                .andExpect(view().name("html/classroom/all-classrooms"))
                .andExpect(model().attribute("classrooms", classroomService.findAll()))
                .andExpect(xpath("//*[@id='classrooms-table']/tbody/tr")
                        .nodeCount(2));
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/classrooms/add"))
                .andExpect(view().name("html/classroom/add-classroom"))
                .andExpect(content().string(containsString("New classroom")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Classroom classroom = classroomService.findById(1L).get();
        this.mockMvc.perform(get("/classrooms/1"))
                .andExpect(view().name("html/classroom/detail-classroom"))
                .andExpect(model().attribute("id", String.valueOf(classroom.getId())))
                .andExpect(model().attribute("name", classroom.getName()))
                .andExpect(model().attribute("capacity", classroom.getCapacity()));

    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/classrooms/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/classrooms"))
                .andExpect(model().attribute("button", "Back to classrooms list"))
                .andExpect(model().attribute("message",
                        String.format("Classroom with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/classrooms/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/classrooms"))
                .andExpect(model().attribute("button", "Back to classrooms list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void GET_returnCorrectAutocompleteFields() throws Exception {
        Classroom classroom = classroomService.findById(1L).get();
        this.mockMvc.perform(get("/classrooms/1/edit"))
                .andExpect(view().name("html/classroom/edit-classroom"))
                .andExpect(xpath("//input[@id='name-edit']/@value").string(classroom.getName()))
                .andExpect(xpath("//input[@id='capacity-edit']/@value")
                        .string(String.valueOf(classroom.getCapacity())));
    }

    @Test
    void POST_sendFormForUpdateObject() throws Exception {
        this.mockMvc.perform(post("/classrooms/1/edit?name=newClassroomName&capacity=32"))
                .andExpect(redirectedUrl("/classrooms/1"));
    }
}
