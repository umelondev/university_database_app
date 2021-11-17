package com.foxminded.javaee.university.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@Entity(name = "teachers")
public class Teacher implements Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 2, max = 32, message = "invalid range! must be between 2 and 32 characters")
    private String firstName;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 2, max = 32, message = "invalid range! must be between 2 and 32 characters")
    private String lastName;

    @NotEmpty(message = "the field must not be empty!")
    private String academicDegree;

    public Teacher() {
    }

    public Teacher(String firstName, String lastName, String academicDegree) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.academicDegree = academicDegree;
    }

    public Teacher(Long id, String firstName, String lastName, String academicDegree) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.academicDegree = academicDegree;
    }

    @Override
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
