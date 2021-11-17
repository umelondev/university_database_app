package com.foxminded.javaee.university.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Date;

@Data
@Entity(name = "schedules")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date calendarDate;

    @Positive(message = "must be greater than zero")
    @NotNull(message = "the field must not be empty!")
    private Long groupId;

    @Positive(message = "must be greater than zero")
    @NotNull(message = "the field must not be empty!")
    private Long teacherId;
    private String orderTime;

    @Positive(message = "must be greater than zero")
    @NotNull(message = "the field must not be empty!")
    private Long subjectId;

    @Positive(message = "must be greater than zero")
    @NotNull(message = "the field must not be empty!")
    private Long classroomId;

    public Schedule() {
    }

    public Schedule(Date calendarDate, Long groupId, Long teacherId, String orderTime, Long subjectId, Long classroomId) {
        this.calendarDate = calendarDate;
        this.groupId = groupId;
        this.teacherId = teacherId;
        this.orderTime = orderTime;
        this.subjectId = subjectId;
        this.classroomId = classroomId;
    }

    public Schedule(Long id, Date calendarDate, Long groupId, Long teacherId, String orderTime, Long subjectId, Long classroomId) {
        this.id = id;
        this.calendarDate = calendarDate;
        this.groupId = groupId;
        this.teacherId = teacherId;
        this.orderTime = orderTime;
        this.subjectId = subjectId;
        this.classroomId = classroomId;
    }
}
