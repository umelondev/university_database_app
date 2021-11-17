package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Teacher;
import com.foxminded.javaee.university.repo.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Long id) {
        log.debug("Getting Teacher by id={}:", id);

        if(teacherRepository.findById(id).isEmpty()) {
            log.warn("Teacher id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Teacher successfully founded!");
            return teacherRepository.findById(id);
        }
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByFirstNameAndLastName(String firstName, String lastName) {
        log.debug("Getting Teacher by name: {} {}", firstName, lastName);

        List<Teacher> teacherList = teacherRepository.findByFirstNameAndLastName(firstName, lastName);

        if (!teacherList.isEmpty()) {
            log.debug("List of teachers received!\n");
            return teacherList;
        } else {
            log.warn("Teacher with name: {} {}, does not exist!\n", firstName, lastName);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Teacher> findByAcademicDegree(String academicDegree) {
        log.debug("Getting Teacher by degree: {}", academicDegree);

        List<Teacher> teacherList = teacherRepository.findByAcademicDegree(academicDegree);

        if (!teacherList.isEmpty()) {
            log.debug("List of teachers received!\n");
            return teacherList;
        } else {
            log.warn("Teacher with academic degree: {}, does not exist!\n", academicDegree);
            return List.of();
        }
    }

    public void save(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Teacher id={}:", id);

        Optional<Teacher> teacher = findById(id);

        if (teacher.isPresent()) {
            teacherRepository.deleteById(id);
            log.debug("Removed Teacher id={}!\n", id);
        }
    }
}
