package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.StudentView;
import com.foxminded.javaee.university.model.Student;
import com.foxminded.javaee.university.service.GroupService;
import com.foxminded.javaee.university.service.StudentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class StudentController {

    private final StudentService studentService;
    private final GroupService groupService;

    public StudentController(StudentService studentService, GroupService groupService) {
        this.studentService = studentService;
        this.groupService = groupService;
    }

    @GetMapping(path="/students")
    public String getAllObjects(Model model) {
        List<StudentView> studentViewList = new ArrayList<>();

        for(Student st: studentService.findAll()) {
            studentViewList.add(new StudentView(
                    st.getId(),
                    st.getFullName(),
                    groupService.findById(st.getGroupId()).get().getName()));
        }

        model.addAttribute("students", studentViewList);
        return "html/student/all-students";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/students/add")
    public String getNewObject(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("groupList", groupService.findAll());
        return "html/student/add-student";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/students/add")
    public String postNewObject(@ModelAttribute("student") @Valid Student student, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/student/add-student";

        studentService.save(student);
        return "redirect:/students";
    }

    @GetMapping("students/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/students");
        model.addAttribute("button", "Back to students list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Student> studentOptional = studentService.findById(Long.valueOf(id));

            if (studentOptional.isEmpty()) {
                model.addAttribute("message", String.format("Student with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("firstName", studentOptional.get().getFirstName());
            model.addAttribute("lastName", studentOptional.get().getLastName());
            model.addAttribute("groupId", studentOptional.get().getGroupId());
            return "html/student/detail-student";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("students/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/students");
        model.addAttribute("button", "Back to students list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Student> studentOptional = studentService.findById(Long.valueOf(id));
            model.addAttribute("student", studentOptional);
            model.addAttribute("groupList", groupService.findAll());

            if (studentOptional.isEmpty()) {
                model.addAttribute("message", String.format("Student with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("firstName", studentOptional.get().getFirstName());
            model.addAttribute("lastName", studentOptional.get().getLastName());
            model.addAttribute("groupId", studentOptional.get().getGroupId());
            return "html/student/edit-student";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/students/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Student student, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/student/edit-student";

        studentService.save(student);
        return "redirect:/students/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/students/{id}/delete")
    public String removeObject(@PathVariable(value = "id") long id) {
        Optional<Student> student = studentService.findById(id);

        if(student.isPresent()) studentService.deleteById(id);

        return "redirect:/students";
    }

    @PostMapping(path="/students/search/name")
    public String searchByName(@RequestParam String name, Model model) {
        List<StudentView> studentViewList = new ArrayList<>();
        String[] s = name.split(" ");

        for(Student st: studentService.findByFullname(s[0], s[1])) {
            studentViewList.add(new StudentView(
                    st.getId(),
                    st.getFullName(),
                    groupService.findById(st.getGroupId()).get().getName()));
        }
        model.addAttribute("students", studentViewList);
        return "html/student/all-students";
    }

    @PostMapping(path="/students/search/from-group")
    public String searchByGroup(@RequestParam String groupId, Model model) {
        List<StudentView> studentViewList = new ArrayList<>();

        for(Student st: studentService.findAllStudentsInGroup(Long.valueOf(groupId))) {
            studentViewList.add(new StudentView(
                    st.getId(),
                    st.getFullName(),
                    groupService.findById(st.getGroupId()).get().getName()));
        }
        model.addAttribute("students", studentViewList);
        return "html/student/all-students";
    }
}
