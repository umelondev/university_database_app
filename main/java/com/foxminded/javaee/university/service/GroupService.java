package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Subject;
import com.foxminded.javaee.university.repo.GroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;

    private final SubjectService subjectService;

    public GroupService(GroupRepository groupRepository, SubjectService subjectService) {
        this.groupRepository = groupRepository;
        this.subjectService = subjectService;
    }

    @Transactional(readOnly = true)
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Group> findById(Long id) {
        log.debug("Getting Group by id={}:", id);

        if (groupRepository.findById(id).isEmpty()) {
            log.warn("Group id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Group successfully founded!");
            return groupRepository.findById(id);
        }
    }

    public void addSubjectToGroup(Long subject_id, Long group_id) {
        log.debug("Adding Subject id={} to Group id={}:", subject_id, group_id);

        Group group = findById(group_id).get();
        Subject subject = subjectService.findById(subject_id).get();

        group.getSubjectList().add(subject);

        log.debug("Subject id={} has been added to Group id={}!\n", subject_id, group_id);
    }

    public void removeSubjectFromGroup(Long subject_id, Long group_id) {
        log.debug("Removing Subject id={} from Group id={}:", subject_id, group_id);

        Group group = findById(group_id).get();
        Subject subject = subjectService.findById(subject_id).get();

        group.getSubjectList().remove(subject);

        log.debug("Subject id={} has been removed from Group id={}!\n", subject_id, group_id);
    }

    public void save(Group group) {
        groupRepository.save(group);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Group id={}:", id);

        Optional<Group> group = findById(id);

        if (group.isPresent()) {
            groupRepository.deleteById(id);
            log.debug("Removed Group id={}!\n", id);
        }
    }
}
