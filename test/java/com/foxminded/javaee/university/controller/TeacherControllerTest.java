package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.TeacherView;
import com.foxminded.javaee.university.model.Teacher;
import com.foxminded.javaee.university.service.TeacherService;
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

import java.util.List;

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
class TeacherControllerTest {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        List<TeacherView> tvList = List.of(
                new TeacherView(1L, "Mr Bean", "academicDegree1"),
                new TeacherView(2L, "Uncle Wassermann", "academicDegree2"));

        this.mockMvc.perform(get("/teachers"))
                .andExpect(view().name("html/teacher/all-teachers"))
                .andExpect(model().attribute("teachers", tvList))
                .andExpect(xpath("//*[@id='teachers-table']/tbody/tr")
                        .nodeCount(teacherService.findAll().size()));
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/teachers/add"))
                .andExpect(view().name("html/teacher/add-teacher"))
                .andExpect(content().string(containsString("New teacher")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Teacher teacher = teacherService.findById(1L).get();
        this.mockMvc.perform(get("/teachers/1"))
                .andExpect(view().name("html/teacher/detail-teacher"))
                .andExpect(model().attribute("id", String.valueOf(teacher.getId())))
                .andExpect(model().attribute("firstName", teacher.getFirstName()))
                .andExpect(model().attribute("lastName", teacher.getLastName()))
                .andExpect(model().attribute("academicDegree", teacher.getAcademicDegree()));
    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/teachers/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/teachers"))
                .andExpect(model().attribute("button", "Back to teachers list"))
                .andExpect(model().attribute("message",
                        String.format("Teacher with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/teachers/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/teachers"))
                .andExpect(model().attribute("button", "Back to teachers list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void GET_returnCorrectAutocompleteFields() throws Exception {
        Teacher teacher = teacherService.findById(1L).get();
        this.mockMvc.perform(get("/teachers/1/edit"))
                .andExpect(view().name("html/teacher/edit-teacher"))
                .andExpect(xpath("//input[@id='fname-edit']/@value").string(teacher.getFirstName()))
                .andExpect(xpath("//input[@id='lname-edit']/@value").string(teacher.getLastName()));
    }

    @Test
    void POST_sendFormForUpdateObject() throws Exception {
        this.mockMvc.perform(post("/teachers/1/edit?firstName=Luke&lastName=Smith&" +
                "academicDegree=Doctoral Degree"))
                .andExpect(redirectedUrl("/teachers/1"));
    }

    @Test
    void POST_returnCorrectListAfterSearchByName() throws Exception {
        List<TeacherView> tvList = List.of(
                new TeacherView(1L, "Mr Bean", "academicDegree1"));

        this.mockMvc.perform(post("/teachers/search/name?name=Mr Bean"))
                .andExpect(view().name("html/teacher/all-teachers"))
                .andExpect(model().attribute("teachers", tvList))
                .andExpect(xpath("//*[@id='teachers-table']/tbody/tr").nodeCount(1));
    }

    @Test
    void POST_returnCorrectListAfterSearchByDegree() throws Exception {
        List<TeacherView> tvList = List.of(
                new TeacherView(1L, "Mr Bean", "academicDegree1"));

        this.mockMvc.perform(post("/teachers/search/degree?degree=academicDegree1"))
                .andExpect(view().name("html/teacher/all-teachers"))
                .andExpect(model().attribute("teachers", tvList))
                .andExpect(xpath("//*[@id='teachers-table']/tbody/tr").nodeCount(1));
    }
}
