package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Subject;
import com.foxminded.javaee.university.repo.SubjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Subject> findById(Long id) {
        log.debug("Getting Subject by id={}:", id);

        if(subjectRepository.findById(id).isEmpty()) {
            log.warn("Subject id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Subject successfully founded!");
            return subjectRepository.findById(id);
        }
    }

    @Transactional(readOnly = true)
    public Set<Subject> findAllSubjectsInGroup(Long group_id) {
        log.debug("Getting Subjects from Group id={}:", group_id);

        List<Subject> subjectsList = subjectRepository.findByGroupId(group_id);

        Set<Subject> subjects = Set.copyOf(subjectsList);

        if (!subjects.isEmpty()) {
            log.debug("List of Subjects received!\n");
            return subjects;
        }
        return Set.of();
    }

    public void save(Subject subject) {
        subjectRepository.save(subject);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Subject id={}:", id);

        Optional<Subject> subject = findById(id);

        if (subject.isPresent()) {
            subjectRepository.deleteById(id);
            log.debug("Removed Subject id={}!\n", id);
        }
    }
}
