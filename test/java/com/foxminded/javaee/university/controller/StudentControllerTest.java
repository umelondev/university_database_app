package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.StudentView;
import com.foxminded.javaee.university.model.Student;
import com.foxminded.javaee.university.service.StudentService;
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

import java.util.ArrayList;
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
class StudentControllerTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        List<StudentView> svList = List.of(
                new StudentView(1L, "John Smith", "group1"),
                new StudentView(2L, "Enthony McLovin", "group2"));

        this.mockMvc.perform(get("/students"))
                .andExpect(view().name("html/student/all-students"))
                .andExpect(model().attribute("students", svList))
                .andExpect(xpath("//*[@id='students-table']/tbody/tr")
                        .nodeCount(studentService.findAll().size()));
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/students/add"))
                .andExpect(view().name("html/student/add-student"))
                .andExpect(content().string(containsString("New student")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Student student = studentService.findById(1L).get();
        this.mockMvc.perform(get("/students/1"))
                .andExpect(view().name("html/student/detail-student"))
                .andExpect(model().attribute("id", String.valueOf(student.getId())))
                .andExpect(model().attribute("firstName", student.getFirstName()))
                .andExpect(model().attribute("lastName", student.getLastName()))
                .andExpect(model().attribute("groupId", student.getGroupId()));
    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/students/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/students"))
                .andExpect(model().attribute("button", "Back to students list"))
                .andExpect(model().attribute("message",
                        String.format("Student with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/students/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/students"))
                .andExpect(model().attribute("button", "Back to students list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void GET_returnCorrectAutocompleteFields() throws Exception {
        Student student = studentService.findById(1L).get();
        this.mockMvc.perform(get("/students/1/edit"))
                .andExpect(view().name("html/student/edit-student"))
                .andExpect(xpath("//input[@id='fname-edit']/@value").string(student.getFirstName()))
                .andExpect(xpath("//input[@id='lname-edit']/@value").string(student.getLastName()));
    }

    @Test
    void POST_sendFormForUpdateObject() throws Exception {
        this.mockMvc.perform(post("/students/1/edit?firstName=Luke&lastName=Smith&groupId=1"))
                .andExpect(redirectedUrl("/students/1"));
    }

    @Test
    void POST_returnCorrectListAfterSearchByName() throws Exception {
        List<StudentView> svList = new ArrayList<>();

        for (Student st: studentService.findByFullname("John", "Smith")) {
            svList.add(new StudentView(
                    st.getId(),
                    st.getFullName(),
                    "group1"));
        }

        this.mockMvc.perform(post("/students/search/name?name=John Smith"))
                .andExpect(view().name("html/student/all-students"))
                .andExpect(model().attribute("students", svList))
                .andExpect(xpath("//*[@id='students-table']/tbody/tr").nodeCount(1));
    }

    @Test
    void POST_returnCorrectListAfterSearchByGroup() throws Exception {
        List<StudentView> svList = new ArrayList<>();

        for (Student st: studentService.findAllStudentsInGroup(1L)) {
            svList.add(new StudentView(
                    st.getId(),
                    st.getFullName(),
                    "group1"));
        }

        this.mockMvc.perform(post("/students/search/from-group?groupId=1"))
                .andExpect(view().name("html/student/all-students"))
                .andExpect(model().attribute("students", svList))
                .andExpect(xpath("//*[@id='students-table']/tbody/tr").nodeCount(1));
    }
}
