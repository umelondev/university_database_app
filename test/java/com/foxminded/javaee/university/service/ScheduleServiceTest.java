package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @Test
    void findScheduleByDate() {
        List<Schedule> desiredList = List.of(
                new Schedule(1l, Date.valueOf("2018-10-20"), 1l, 1l,
                "8:00-9:20", 1l, 1l));

        assertEquals(desiredList, scheduleService.findByDate(Date.valueOf("2018-10-20")));
    }

    @Test
    void findScheduleByGroup() {
        List<Schedule> scheduleList =List.of(
                new Schedule(2l, Date.valueOf("2019-11-25"), 2l, 2l,
                        "9:35-10:55", 2l, 2l));

        assertEquals(scheduleList, scheduleService.findByGroupId(2l));
    }

    @Test
    void findScheduleByTeacher() {
        List<Schedule> scheduleList =List.of(
                new Schedule(2l, Date.valueOf("2019-11-25"), 2l, 2l,
                "9:35-10:55", 2l, 2l));

        assertEquals(scheduleList, scheduleService.findByTeacherId(2l));
    }
}
