package com.foxminded.javaee.university.controller.pojo;

import lombok.Data;

@Data
public class TeacherView {
    private Long id;
    private String fullName;
    private String academicDegree;

    public TeacherView(Long id, String fullName, String academicDegree) {
        this.id = id;
        this.fullName = fullName;
        this.academicDegree = academicDegree;
    }
}
