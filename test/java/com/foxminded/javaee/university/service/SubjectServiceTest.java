package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@Sql(value = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/clean-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
class SubjectServiceTest {

    @Autowired
    SubjectService subjectService;

    @Autowired
    GroupService groupService;

    @Test
    void findAllSubjectsInGroup() {
        Group group = groupService.findById(1l).get();
        Subject subject = subjectService.findById(1l).get();
        groupService.addSubjectToGroup(subject.getId(), group.getId());

        Set<Subject> subjectSet = Set.of(subject);
        assertEquals(subjectSet, subjectService.findAllSubjectsInGroup(1l));
    }
}
