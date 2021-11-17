package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Classroom;
import com.foxminded.javaee.university.repo.ClassroomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ClassroomService {
    private final ClassroomRepository classroomRepository;

    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    @Transactional(readOnly = true)
    public List<Classroom> findAll() {
        return classroomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Classroom> findById(Long id) {
        log.debug("Getting Classroom by id={}:", id);

        if (classroomRepository.findById(id).isEmpty()) {
            log.warn("Classroom id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Classroom successfully founded!");
            return classroomRepository.findById(id);
        }
    }

    public void save(Classroom classroom) {
        classroomRepository.save(classroom);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Classroom id={}:", id);

        Optional<Classroom> classroom = findById(id);

        if (classroom.isPresent()) {
            classroomRepository.deleteById(id);
            log.debug("Removed Classroom id={}!\n", id);
        }
    }
}
