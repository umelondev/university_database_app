package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.service.GroupService;
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
class GroupControllerTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/groups/add"))
                .andExpect(view().name("html/group/add-group"))
                .andExpect(content().string(containsString("New group")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Group group = groupService.findById(1L).get();
        this.mockMvc.perform(get("/groups/1"))
                .andExpect(view().name("html/group/detail-group"))
                .andExpect(model().attribute("id", String.valueOf(group.getId())))
                .andExpect(model().attribute("name", group.getName()));
    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/groups/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/groups"))
                .andExpect(model().attribute("button", "Back to groups list"))
                .andExpect(model().attribute("message",
                        String.format("Group with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/groups/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/groups"))
                .andExpect(model().attribute("button", "Back to groups list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void GET_returnCorrectAutocompleteFields() throws Exception {
        Group group = groupService.findById(1L).get();
        this.mockMvc.perform(get("/groups/1/edit"))
                .andExpect(view().name("html/group/edit-group"))
                .andExpect(xpath("//input[@id='name-edit']/@value").string(group.getName()));
    }

    @Test
    void POST_sendFormForRemoveSubjectFromGroup() throws Exception {
        this.mockMvc.perform(post("/groups/1/edit/remove-subject/1"))
                .andExpect(redirectedUrl("/groups/1/edit"));
    }

    @Test
    void GET_returnCorrectPageForAddSubjectToGroup() throws Exception {
        Group group = groupService.findById(1L).get();

        this.mockMvc.perform(get("/groups/1/add-subject"))
                .andExpect(view().name("html/group/add-subject-to-group"))
                .andExpect(model().attribute("id", group.getId()));
    }

    @Test
    void POST_sendFormForAddSubjectToGroup() throws Exception {
        this.mockMvc.perform(post("/groups/1/add-subject?subjectId=1"))
                .andExpect(redirectedUrl("/groups/1"));
    }
}
