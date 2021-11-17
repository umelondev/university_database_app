package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Student;
import com.foxminded.javaee.university.repo.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    private final GroupService groupService;

    public StudentService(StudentRepository studentRepository, GroupService groupService) {
        this.studentRepository = studentRepository;
        this.groupService = groupService;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        log.debug("Getting Student by id={}:", id);

        if(studentRepository.findById(id).isEmpty()) {
            log.warn("Student id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Student successfully founded!");
            return studentRepository.findById(id);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> findByFullname(String firstName, String lastName) {
        log.debug("Getting Student by name: {} {}", firstName, lastName);

        List<Student> studentList = studentRepository.findByFirstNameAndLastName(firstName, lastName);

        if (!studentList.isEmpty()) {
            log.debug("List of Students received!\n");
            return studentList;
        } else {
            log.warn("Student with name: {} {}, does not exist!\n", firstName, lastName);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Student> findAllStudentsInGroup(Long group_id) {
        log.debug("Getting Students from Group id={}:", group_id);

        Optional<Group> group = groupService.findById(group_id);

        if (group.isPresent()) {
            log.debug("List of Students received!\n");
            return studentRepository.findByGroupId(group_id);
        }
        return List.of();
    }

    public void save(Student student) {
        studentRepository.save(student);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Student id={}:", id);

        Optional<Student> student = findById(id);

        if (student.isPresent()) {
            studentRepository.deleteById(id);
            log.debug("Removed Student id={}!\n", id);
        }
    }
}
