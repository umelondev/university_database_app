package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Subject;
import com.foxminded.javaee.university.service.SubjectService;
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
class SubjectControllerTest {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        this.mockMvc.perform(get("/subjects"))
                .andExpect(view().name("html/subject/all-subjects"))
                .andExpect(model().attribute("subjects", subjectService.findAll()))
                .andExpect(xpath("//*[@id='subjects-table']/tbody/tr")
                        .nodeCount(subjectService.findAll().size()));
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/subjects/add"))
                .andExpect(view().name("html/subject/add-subject"))
                .andExpect(content().string(containsString("New subject")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Subject subject = subjectService.findById(1L).get();
        this.mockMvc.perform(get("/subjects/1"))
                .andExpect(view().name("html/subject/detail-subject"))
                .andExpect(model().attribute("id", String.valueOf(subject.getId())))
                .andExpect(model().attribute("name", subject.getName()))
                .andExpect(model().attribute("description", subject.getDescription()));
    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/subjects/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/subjects"))
                .andExpect(model().attribute("button", "Back to subjects list"))
                .andExpect(model().attribute("message",
                        String.format("Subject with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/subjects/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/subjects"))
                .andExpect(model().attribute("button", "Back to subjects list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void POST_sendFormForUpdateObject() throws Exception {
        this.mockMvc.perform(post("/subjects/1/edit?name=subject1&description=updatedDescription"))
                .andExpect(redirectedUrl("/subjects/1"));
    }

    @Test
    void POST_returnCorrectListAfterSearchByGroup() throws Exception {
        this.mockMvc.perform(post("/subjects/search/by-group?groupId=1"))
                .andExpect(view().name("html/subject/all-subjects"))
                .andExpect(model().attribute("subjects", subjectService.findAllSubjectsInGroup(1L)))
                .andExpect(xpath("//*[@id='subjects-table']/tbody/tr").nodeCount(0));
    }
}
