package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Subject;
import com.foxminded.javaee.university.service.SubjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping(path="/subjects")
    public String getAllObjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());
        return "html/subject/all-subjects";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/subjects/add")
    public String getNewObject(@ModelAttribute("subject") Subject subject) {
        return "html/subject/add-subject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/subjects/add")
    public String postNewObject(@ModelAttribute("subject") @Valid Subject subject, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/subject/add-subject";

        subjectService.save(subject);
        return "redirect:/subjects";
    }

    @GetMapping("subjects/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/subjects");
        model.addAttribute("button", "Back to subjects list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Subject> subjectOptional = subjectService.findById(Long.valueOf(id));

            if (subjectOptional.isEmpty()) {
                model.addAttribute("message", String.format("Subject with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("name", subjectOptional.get().getName());
            model.addAttribute("description", subjectOptional.get().getDescription());
            return "html/subject/detail-subject";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("subjects/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/subjects");
        model.addAttribute("button", "Back to subjects list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Subject> subjectOptional = subjectService.findById(Long.valueOf(id));
            model.addAttribute("subject", subjectOptional);

            if (subjectOptional.isEmpty()) {
                model.addAttribute("message", String.format("Subject with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("name", subjectOptional.get().getName());
            model.addAttribute("description", subjectOptional.get().getDescription());
            return "html/subject/edit-subject";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/subjects/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Subject subject, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/subject/edit-subject";

        subjectService.save(subject);
        return "redirect:/subjects/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/subjects/{id}/delete")
    public String removeObject(@PathVariable(value = "id") long id) {

        Optional<Subject> subject = subjectService.findById(id);

        if(subject.isPresent()) subjectService.deleteById(id);
        else return "html/subjects/all-subjects";
        return "redirect:/subjects";
    }

    @PostMapping(path="/subjects/search/by-group")
    public String searchByGroup(@RequestParam String groupId, Model model) {
        model.addAttribute("subjects", subjectService.findAllSubjectsInGroup(Long.valueOf(groupId)));
        return "html/subject/all-subjects";
    }
}
