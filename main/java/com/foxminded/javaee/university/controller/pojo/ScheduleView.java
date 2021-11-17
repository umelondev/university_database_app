package com.foxminded.javaee.university.controller.pojo;

import lombok.Data;

import java.sql.Date;

@Data
public class ScheduleView {
    private Long id;
    private Date calendarDate;
    private String group;
    private String teacher;
    private String orderTime;
    private String subject;
    private String classroom;

    public ScheduleView(Long id, Date calendarDate, String group, String teacher, String orderTime, String subject,
                        String classroom) {
        this.id = id;
        this.calendarDate = calendarDate;
        this.group = group;
        this.teacher = teacher;
        this.orderTime = orderTime;
        this.subject = subject;
        this.classroom = classroom;
    }
}
