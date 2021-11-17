package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.service.*;
import com.foxminded.javaee.university.service.data.DataGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

@Controller
@RequestMapping("/")
public class MainController {

    private final ClassroomService classroomService;

    private final GroupService groupService;

    private final ScheduleService scheduleService;

    private final StudentService studentService;

    private final SubjectService subjectService;

    private final TeacherService teacherService;

    private DataGenerator generator;

    public MainController(ClassroomService classroomService, GroupService groupService,
                          ScheduleService scheduleService, StudentService studentService,
                          SubjectService subjectService, TeacherService teacherService) {
        this.classroomService = classroomService;
        this.groupService = groupService;
        this.scheduleService = scheduleService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
    }

    @PostConstruct
    public void init() {
        generator = new DataGenerator(groupService, subjectService, studentService, teacherService,
                classroomService, scheduleService);
    }

    @GetMapping(path={"/", "/index"})
    public String index(Model model) {
        model.addAttribute("classrooms_count", classroomService.findAll().size());
        model.addAttribute("groups_count", groupService.findAll().size());
        model.addAttribute("schedules_count", scheduleService.findAll().size());
        model.addAttribute("students_count", studentService.findAll().size());
        model.addAttribute("subjects_count", subjectService.findAll().size());
        model.addAttribute("teachers_count", teacherService.findAll().size());
        return "index";
    }
}
