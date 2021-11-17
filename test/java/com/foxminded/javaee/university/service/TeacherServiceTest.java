package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class TeacherServiceTest {

    @Autowired
    TeacherService teacherService;

    @Test
    void findByName() {
        List<Teacher> desiredList = List.of(new Teacher(1l, "Mr", "Bean", "academicDegree1"));
        assertEquals(desiredList, teacherService.findByFirstNameAndLastName("Mr", "Bean"));
    }

    @Test
    void findByAcademicDegree() {
        List<Teacher> desiredList = List.of(new Teacher(1l, "Mr", "Bean", "academicDegree1"));
        assertEquals(desiredList, teacherService.findByAcademicDegree("academicDegree1"));
    }
}
