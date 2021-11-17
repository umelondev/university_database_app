package com.foxminded.javaee.university.repo;

import com.foxminded.javaee.university.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT s FROM groups g JOIN g.subjectList s WHERE g.id = :group_id")
    List<Subject> findByGroupId(@Param("group_id") Long group_id);
}
