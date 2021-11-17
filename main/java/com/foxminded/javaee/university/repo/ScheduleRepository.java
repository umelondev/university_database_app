package com.foxminded.javaee.university.repo;

import com.foxminded.javaee.university.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByCalendarDate(Date calendar_date);

    List<Schedule> findByGroupId(Long group_id);

    List<Schedule> findByTeacherId(Long teacher_id);
}
