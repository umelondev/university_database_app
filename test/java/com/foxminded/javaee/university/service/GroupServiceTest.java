package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Subject;
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
class GroupServiceTest {

    @Autowired
    GroupService groupService;

    @Autowired
    SubjectService subjectService;

    @Test
    void addSubjectToGroup() {
        Subject subject = subjectService.findById(1L).get();
        Group group = groupService.findById(1L).get();

        groupService.addSubjectToGroup(subject.getId(), group.getId());
        assertEquals(List.of(subject), groupService.findById(1l).get().getSubjectList());
    }

    @Test
    void removeSubjectFromGroup() {
        Subject subject = subjectService.findById(1L).get();
        Group group = groupService.findById(1L).get();

        groupService.addSubjectToGroup(subject.getId(), group.getId());
        assertEquals(List.of(subject), groupService.findById(1l).get().getSubjectList());

        groupService.removeSubjectFromGroup(subject.getId(), group.getId());
        assertEquals(List.of(), groupService.findById(1l).get().getSubjectList());
    }
}
