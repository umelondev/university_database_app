package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Student;
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
class StudentServiceTest {

    @Autowired
    StudentService studentService;

    @Test
    void findByName() {
        List<Student> desiredList = List.of(new Student(1l, "John", "Smith", 1l));
        assertEquals(desiredList, studentService.findByFullname("John", "Smith"));
    }

    @Test
    void findAllStudentsInGroup() {
        List<Student> studentList = List.of(new Student(1l, "John", "Smith", 1l));
        assertEquals(studentList, studentService.findAllStudentsInGroup(1l));
    }
}
