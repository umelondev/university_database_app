package com.foxminded.javaee.university.controller.pojo;

import lombok.Data;

@Data
public class StudentView {
    private Long id;
    private String fullName;
    private String group;

    public StudentView(Long id, String fullName, String group) {
        this.id = id;
        this.fullName = fullName;
        this.group = group;
    }
}
