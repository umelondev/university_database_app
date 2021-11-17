package com.foxminded.javaee.university.repo;

import com.foxminded.javaee.university.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<Teacher> findByFirstNameAndLastName(String firstName, String lastName);

    List<Teacher> findByAcademicDegree(String academic_degree);
}
