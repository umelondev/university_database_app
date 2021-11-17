package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.model.Group;
import com.foxminded.javaee.university.model.Subject;
import com.foxminded.javaee.university.service.GroupService;
import com.foxminded.javaee.university.service.SubjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class GroupController {

    private final GroupService groupService;

    private final SubjectService subjectService;

    public GroupController(GroupService groupService, SubjectService subjectService) {
        this.groupService = groupService;
        this.subjectService = subjectService;
    }

    @GetMapping(path="/groups")
    public String getAllObjects(Model model) {
        model.addAttribute("groups", groupService.findAll());
        return "html/group/all-groups";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/groups/add")
    public String getNewObject(@ModelAttribute("group") Group group) {
        return "html/group/add-group";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/groups/add")
    public String postNewObject(@ModelAttribute("group") @Valid Group group, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/group/add-group";

        groupService.save(group);
        return "redirect:/groups";
    }

    @GetMapping("groups/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/groups");
        model.addAttribute("button", "Back to groups list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Group> groupOptional = groupService.findById(Long.valueOf(id));

            if (groupOptional.isEmpty()) {
                model.addAttribute("message", String.format("Group with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("name", groupOptional.get().getName());
            model.addAttribute("subjects", subjectService.findAllSubjectsInGroup(Long.valueOf(id)));
            return "html/group/detail-group";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("groups/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/groups");
        model.addAttribute("button", "Back to groups list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Group> groupOptional = groupService.findById(Long.valueOf(id));
            model.addAttribute("group", groupOptional);

            if (groupOptional.isEmpty()) {
                model.addAttribute("message", String.format("Group with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("name", groupOptional.get().getName());
            model.addAttribute("subjects", subjectService.findAllSubjectsInGroup(Long.valueOf(id)));
            return "html/group/edit-group";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/groups/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Group group, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/group/edit-group";

        groupService.save(group);
        return "redirect:/groups/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/groups/{gId}/edit/remove-subject/{sId}")
    public String removeSubjectFromGroup(@PathVariable(value = "gId") long groupId,
                                         @PathVariable(value = "sId") long subjectId) {

        Optional<Subject> subject = subjectService.findById(subjectId);
        Optional<Group> group = groupService.findById(groupId);

        if(subject.isPresent() && group.isPresent()) groupService.removeSubjectFromGroup(subjectId, groupId);
        else {
            return "redirect:/groups/{gId}";
        }

        return "redirect:/groups/{gId}/edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path = "/groups/{gId}/add-subject")
    public String getAddSubjectToGroup(@PathVariable(value = "gId") long groupId, Model model) {
        model.addAttribute("id", groupId);

        Group group = groupService.findById(groupId).get();
        List<Subject> subjectList = subjectService.findAll();
        subjectList.removeAll(group.getSubjectList());

        model.addAttribute("subjectList", subjectList);
        return "html/group/add-subject-to-group";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/groups/{gId}/add-subject")
    public String postAddSubjectToGroup(@PathVariable(value = "gId") long groupId,
                                         @RequestParam String subjectId) {

        Optional<Subject> subject = subjectService.findById(Long.valueOf(subjectId));
        Optional<Group> group = groupService.findById(groupId);

        if(subject.isPresent() && group.isPresent())
            groupService.addSubjectToGroup(Long.valueOf(subjectId), groupId);

        return "redirect:/groups/{gId}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/groups/{id}/delete")
    public String deleteObject(@PathVariable(value = "id") long id) {
        Optional<Group> group = groupService.findById(id);

        if(group.isPresent()) groupService.deleteById(id);
        return "redirect:/groups";
    }
}
