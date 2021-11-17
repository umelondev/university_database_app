package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Classroom;
import com.foxminded.javaee.university.service.ClassroomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class ClassroomController {

    private final ClassroomService classroomService;

    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    @GetMapping(path="/classrooms")
    public String getAllObjects(Model model) {
        model.addAttribute("classrooms", classroomService.findAll());
        return "html/classroom/all-classrooms";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/classrooms/add")
    public String getNewObject(@ModelAttribute("classroom") Classroom classroom) {
        return "html/classroom/add-classroom";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/classrooms/add")
    public String postNewObject(@ModelAttribute("classroom") @Valid Classroom classroom, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/classroom/add-classroom";

        classroomService.save(classroom);
        return "redirect:/classrooms";
    }

    @GetMapping("classrooms/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/classrooms");
        model.addAttribute("button", "Back to classrooms list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Classroom> classroomOptional = classroomService.findById(Long.valueOf(id));

            if (classroomOptional.isEmpty()) {
                model.addAttribute("message", String.format("Classroom with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("name", classroomOptional.get().getName());
            model.addAttribute("capacity", classroomOptional.get().getCapacity());
            return "html/classroom/detail-classroom";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("classrooms/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/classrooms");
        model.addAttribute("button", "Back to classrooms list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Classroom> classroomOptional = classroomService.findById(Long.valueOf(id));
            model.addAttribute("classroom", classroomOptional);

            if (classroomOptional.isEmpty()) {
                model.addAttribute("message", String.format("Classroom with id=%s does not exist or was deleted.", id));
                return "error/500";
            }
            model.addAttribute("id", id);
            return "html/classroom/edit-classroom";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/classrooms/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Classroom classroom,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/classroom/edit-classroom";
        classroomService.save(classroom);
        return "redirect:/classrooms/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/classrooms/{id}/delete")
    public String removeObject(@PathVariable(value = "id") long id) {
        Optional<Classroom> classroom = classroomService.findById(id);

        classroomService.deleteById(id);
        return "redirect:/classrooms";
    }
}
