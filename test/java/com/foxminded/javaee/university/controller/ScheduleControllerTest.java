package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.ScheduleView;
import com.foxminded.javaee.university.model.Schedule;
import com.foxminded.javaee.university.service.ScheduleService;
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

import java.sql.Date;
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
class ScheduleControllerTest {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void GET_returnAllObjects() throws Exception {
        this.mockMvc.perform(get("/schedules"))
                .andExpect(view().name("html/schedule/all-schedules"))
                .andExpect(xpath("//*[@id='schedules-table']/tbody/tr")
                        .nodeCount(scheduleService.findAll().size()));
    }

    @Test
    void GET_returnCorrectPageForNewObject() throws Exception {
        this.mockMvc.perform(get("/schedules/add"))
                .andExpect(view().name("html/schedule/add-schedule"))
                .andExpect(content().string(containsString("New schedule")));
    }

    @Test
    void GET_returnCorrectPageForDetails() throws Exception {
        Schedule schedule = scheduleService.findById(1L).get();
        this.mockMvc.perform(get("/schedules/1"))
                .andExpect(view().name("html/schedule/detail-schedule"))
                .andExpect(model().attribute("id", String.valueOf(schedule.getId())))
                .andExpect(model().attribute("date", schedule.getCalendarDate()))
                .andExpect(model().attribute("time", schedule.getOrderTime()))
                .andExpect(model().attribute("groupId", schedule.getGroupId()))
                .andExpect(model().attribute("subjectId", schedule.getSubjectId()))
                .andExpect(model().attribute("teacherId", schedule.getTeacherId()))
                .andExpect(model().attribute("classroomId", schedule.getClassroomId()));
    }

    @Test
    void GET_return500WhenIdNotExist() throws Exception {
        long id = 1000;
        this.mockMvc.perform(get("/schedules/".concat(String.valueOf(id))))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/schedules"))
                .andExpect(model().attribute("button", "Back to schedules list"))
                .andExpect(model().attribute("message",
                        String.format("Schedule with id=%s does not exist or was deleted.", id)));
    }

    @Test
    void GET_return500WhenCharsInsteadId() throws Exception {
        String id = "qwerty";
        this.mockMvc.perform(get("/schedules/".concat(id)))
                .andExpect(view().name("error/500"))
                .andExpect(model().attribute("link", "/schedules"))
                .andExpect(model().attribute("button", "Back to schedules list"))
                .andExpect(model().attribute("message", String.format("Number was expected, but was entered: '%s'.", id)));
    }

    @Test
    void GET_returnCorrectAutocompleteFields() throws Exception {
        Schedule schedule = scheduleService.findById(1L).get();
        this.mockMvc.perform(get("/schedules/1/edit"))
                .andExpect(view().name("html/schedule/edit-schedule"))
                .andExpect(xpath("//input[@id='date-edit']/@value").string(String.valueOf(
                        schedule.getCalendarDate())))
                .andExpect(xpath("//input[@id='time-edit']/@value").string(String.valueOf(
                        schedule.getOrderTime())));
    }

    @Test
    void POST_sendFormForRemoveObject() throws Exception {
        this.mockMvc.perform(post("/schedules/1/delete"))
                .andExpect(redirectedUrl("/schedules"));
    }

    @Test
    void POST_returnCorrectListAfterSearchByDate() throws Exception {
        List<ScheduleView> svList = new ArrayList<>();

        for (Schedule sch: scheduleService.findByDate(Date.valueOf("2019-11-25"))) {
            svList.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    "group2",
                    "Uncle Wassermann",
                    sch.getOrderTime(),
                    "subject2",
                    "classroom2"));
        }

        this.mockMvc.perform(post("/schedules/search/date?date=2019-11-25"))
                .andExpect(view().name("html/schedule/all-schedules"))
                .andExpect(model().attribute("schedules", svList))
                .andExpect(xpath("//*[@id='schedules-table']/tbody/tr").nodeCount(1));
    }

    @Test
    void POST_returnCorrectListAfterSearchByGroup() throws Exception {
        List<ScheduleView> svList = new ArrayList<>();

        for (Schedule sch: scheduleService.findByGroupId(2L)) {
            svList.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    "group2",
                    "Uncle Wassermann",
                    sch.getOrderTime(),
                    "subject2",
                    "classroom2"));
        }

        this.mockMvc.perform(post("/schedules/search/by-group?groupId=2"))
                .andExpect(view().name("html/schedule/all-schedules"))
                .andExpect(model().attribute("schedules", svList))
                .andExpect(xpath("//*[@id='schedules-table']/tbody/tr").nodeCount(1));
    }

    @Test
    void POST_returnCorrectListAfterSearchByTeacher() throws Exception {
        List<ScheduleView> svList = new ArrayList<>();

        for (Schedule sch: scheduleService.findByTeacherId(2L)) {
            svList.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    "group2",
                    "Uncle Wassermann",
                    sch.getOrderTime(),
                    "subject2",
                    "classroom2"));
        }

        this.mockMvc.perform(post("/schedules/search/by-teacher?teacherId=2"))
                .andExpect(view().name("html/schedule/all-schedules"))
                .andExpect(model().attribute("schedules", svList))
                .andExpect(xpath("//*[@id='schedules-table']/tbody/tr").nodeCount(1));
    }
}
