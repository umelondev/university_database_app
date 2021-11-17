package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.StudentView;
import com.foxminded.javaee.university.controller.pojo.TeacherView;
import com.foxminded.javaee.university.model.Degree;
import com.foxminded.javaee.university.model.Student;
import com.foxminded.javaee.university.model.Teacher;
import com.foxminded.javaee.university.service.TeacherService;
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
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping(path="/teachers")
    public String getAllObjects(Model model) {
        List<TeacherView> teacherViewList = new ArrayList<>();

        for(Teacher t: teacherService.findAll()) {
            teacherViewList.add(new TeacherView(
                    t.getId(),
                    t.getFullName(),
                    t.getAcademicDegree()));
        }

        model.addAttribute("teachers", teacherViewList);
        return "html/teacher/all-teachers";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/teachers/add")
    public String getNewObject(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("degree", Degree.values());
        return "html/teacher/add-teacher";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/teachers/add")
    public String postNewObject(@ModelAttribute("teacher") @Valid Teacher teacher, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/teacher/add-teacher";

        teacherService.save(teacher);
        return "redirect:/teachers";
    }

    @GetMapping("teachers/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/teachers");
        model.addAttribute("button", "Back to teachers list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Teacher> teacherOptional = teacherService.findById(Long.valueOf(id));

            if (teacherOptional.isEmpty()) {
                model.addAttribute("message", String.format("Teacher with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("firstName", teacherOptional.get().getFirstName());
            model.addAttribute("lastName", teacherOptional.get().getLastName());
            model.addAttribute("academicDegree", teacherOptional.get().getAcademicDegree());
            return "html/teacher/detail-teacher";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("teachers/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/teachers");
        model.addAttribute("button", "Back to teachers list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Teacher> teacherOptional = teacherService.findById(Long.valueOf(id));
            model.addAttribute("teacher", teacherOptional);
            model.addAttribute("degree", Degree.values());

            if (teacherOptional.isEmpty()) {
                model.addAttribute("message", String.format("Teacher with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("firstName", teacherOptional.get().getFirstName());
            model.addAttribute("lastName", teacherOptional.get().getLastName());
            model.addAttribute("academicDegree", teacherOptional.get().getAcademicDegree());
            return "html/teacher/edit-teacher";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/teachers/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Teacher teacher, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/teacher/edit-teacher";

        teacherService.save(teacher);
        return "redirect:/teachers/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/teachers/{id}/delete")
    public String removeObject(@PathVariable(value = "id") long id) {

        Optional<Teacher> teacher = teacherService.findById(id);

        if(teacher.isPresent()) teacherService.deleteById(id);
        else return "html/teachers/all-teachers";
        return "redirect:/teachers";
    }

    @PostMapping(path="/teachers/search/name")
    public String searchByName(@RequestParam String name, Model model) {
        List<TeacherView> teacherViewList = new ArrayList<>();
        String[] s = name.split(" ");

        for(Teacher t: teacherService.findByFirstNameAndLastName(s[0], s[1])) {
            teacherViewList.add(new TeacherView(
                    t.getId(),
                    t.getFullName(),
                    t.getAcademicDegree()));
        }

        model.addAttribute("teachers", teacherViewList);
        return "html/teacher/all-teachers";
    }

    @PostMapping(path="/teachers/search/degree")
    public String searchByDegree(@RequestParam String degree, Model model) {
        List<TeacherView> teacherViewList = new ArrayList<>();

        for(Teacher t: teacherService.findByAcademicDegree(degree)) {
            teacherViewList.add(new TeacherView(
                    t.getId(),
                    t.getFullName(),
                    t.getAcademicDegree()));
        }

        model.addAttribute("teachers", teacherViewList);
        return "html/teacher/all-teachers";
    }
}
