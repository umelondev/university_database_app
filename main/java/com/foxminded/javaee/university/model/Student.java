package com.foxminded.javaee.university.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Entity(name = "students")
public class Student implements Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 2, max = 32, message = "invalid range! must be between 2 and 32 characters")
    private String firstName;

    @NotEmpty(message = "the field must not be empty!")
    @Size(min = 2, max = 32, message = "invalid range! must be between 2 and 32 characters")
    private String lastName;

    @NotNull(message = "the field must not be empty!")
    @Positive(message = "must be greater than zero")
    private Long groupId;

    public Student() {
    }

    public Student(String firstName, String lastName, Long groupId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupId = groupId;
    }

    public Student(Long id, String firstName, String lastName, Long groupId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.groupId = groupId;
    }

    @Override
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
