package com.foxminded.javaee.university.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Entity(name = "classrooms")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 1, max = 32, message = "invalid range! must be between 1 and 32 characters")
    private String name;

    @Positive(message = "must be greater than zero")
    @Max(value = 32, message = "must be no more than 32")
    private int capacity;

    public Classroom() {
    }

    public Classroom(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public Classroom(Long id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }
}
