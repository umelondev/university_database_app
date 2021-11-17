package com.foxminded.javaee.university.service;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Schedule;
import com.foxminded.javaee.university.model.Teacher;
import com.foxminded.javaee.university.repo.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final GroupService groupService;

    private final TeacherService teacherService;

    public ScheduleService(ScheduleRepository scheduleRepository, GroupService groupService, TeacherService teacherService) {
        this.scheduleRepository = scheduleRepository;
        this.groupService = groupService;
        this.teacherService = teacherService;
    }

    @Transactional(readOnly = true)
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Schedule> findById(Long id) {
        log.debug("Getting Schedule by id={}:", id);

        if(scheduleRepository.findById(id).isEmpty()) {
            log.warn("Schedule id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("Schedule successfully founded!");
            return scheduleRepository.findById(id);
        }
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByDate(Date date) {
        log.debug("Getting Schedule by date: {}", date);
        boolean isDateExists = false;

        for (Schedule schedule : scheduleRepository.findAll()) {
            if (schedule.getCalendarDate().equals(date)) {
                isDateExists = true; break;
            }
        }

        if (isDateExists) {
            log.debug("Schedule received!\n");
            return scheduleRepository.findByCalendarDate(date);
        } else {
            log.warn("There is no schedule for this date\n");
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByGroupId(Long groupId) {
        log.debug("Getting Schedule for Group id={}:", groupId);

        Optional<Group> group = groupService.findById(groupId);

        if (group.isPresent()) {
            log.debug("Schedule received!\n");
            return scheduleRepository.findByGroupId(groupId);
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public List<Schedule> findByTeacherId(Long teacher_id) {
        log.debug("Getting Schedule by Teacher id={}:", teacher_id);

        Optional<Teacher> teacher = teacherService.findById(teacher_id);

        if (teacher.isPresent()) {
            log.debug("Schedule received!\n");
            return scheduleRepository.findByTeacherId(teacher_id);
        }
        return List.of();
    }

    public void save(Schedule schedule) {
        scheduleRepository.save(schedule);
    }

    public void deleteById(Long id) {
        log.debug("Trying to remove Schedule id={}:", id);

        Optional<Schedule> schedule = findById(id);

        if (schedule.isPresent()) {
            scheduleRepository.deleteById(id);
            log.debug("Removed Schedule id={}!\n", id);
        }
    }
}
