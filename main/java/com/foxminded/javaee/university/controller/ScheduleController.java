package com.foxminded.javaee.university.controller;

import com.foxminded.javaee.university.controller.pojo.ScheduleView;
import com.foxminded.javaee.university.model.Schedule;
import com.foxminded.javaee.university.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final GroupService groupService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final ClassroomService classroomService;

    public ScheduleController(ScheduleService scheduleService, GroupService groupService,
                              TeacherService teacherService, SubjectService subjectService,
                              ClassroomService classroomService) {
        this.scheduleService = scheduleService;
        this.groupService = groupService;
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.classroomService = classroomService;
    }

    @GetMapping(path="/schedules")
    public String getAllObjects(Model model) {
        List<ScheduleView> scheduleViews = new ArrayList<>();

        for(Schedule sch: scheduleService.findAll()) {
            scheduleViews.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    groupService.findById(sch.getGroupId()).get().getName(),
                    teacherService.findById(sch.getTeacherId()).get().getFullName(),
                    sch.getOrderTime(),
                    subjectService.findById(sch.getSubjectId()).get().getName(),
                    classroomService.findById(sch.getClassroomId()).get().getName()));
        }

        model.addAttribute("schedules", scheduleViews);
        return "html/schedule/all-schedules";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/schedules/add")
    public String getNewObject(Model model) {
        model.addAttribute("schedule", new Schedule());
        model.addAttribute("groupList", groupService.findAll());
        model.addAttribute("subjectList", subjectService.findAll());
        model.addAttribute("teacherList", teacherService.findAll());
        model.addAttribute("classroomList", classroomService.findAll());

        return "html/schedule/add-schedule";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/schedules/add")
    public String postNewObject(@ModelAttribute("schedule") @Valid Schedule schedule, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/schedule/add-schedule";

        scheduleService.save(schedule);
        return "redirect:/schedules";
    }

    @GetMapping("schedules/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/schedules");
        model.addAttribute("button", "Back to schedules list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Schedule> scheduleOptional = scheduleService.findById(Long.valueOf(id));

            if (scheduleOptional.isEmpty()) {
                model.addAttribute("message", String.format("Schedule with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("date", scheduleOptional.get().getCalendarDate());
            model.addAttribute("time", scheduleOptional.get().getOrderTime());
            model.addAttribute("groupId", scheduleOptional.get().getGroupId());
            model.addAttribute("subjectId", scheduleOptional.get().getSubjectId());
            model.addAttribute("teacherId", scheduleOptional.get().getTeacherId());
            model.addAttribute("classroomId", scheduleOptional.get().getClassroomId());
            return "html/schedule/detail-schedule";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("schedules/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/schedules");
        model.addAttribute("button", "Back to schedules list");

        if(Pattern.matches("\\d+", id)) {
            Optional<Schedule> scheduleOptional = scheduleService.findById(Long.valueOf(id));
            model.addAttribute("schedule", scheduleOptional);
            model.addAttribute("groupList", groupService.findAll());
            model.addAttribute("subjectList", subjectService.findAll());
            model.addAttribute("teacherList", teacherService.findAll());
            model.addAttribute("classroomList", classroomService.findAll());

            if (scheduleOptional.isEmpty()) {
                model.addAttribute("message", String.format("Schedule with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("date", scheduleOptional.get().getCalendarDate());
            model.addAttribute("time", scheduleOptional.get().getOrderTime());
            model.addAttribute("groupId", scheduleOptional.get().getGroupId());
            model.addAttribute("subjectId", scheduleOptional.get().getSubjectId());
            model.addAttribute("teacherId", scheduleOptional.get().getTeacherId());
            model.addAttribute("classroomId", scheduleOptional.get().getClassroomId());
            return "html/schedule/edit-schedule";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/schedules/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid Schedule schedule,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/schedule/edit-schedule";

        scheduleService.save(schedule);
        return "redirect:/schedules/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/schedules/{id}/delete")
    public String removeObject(@PathVariable(value = "id") long id) {
        Optional<Schedule> schedule = scheduleService.findById(id);

        if(schedule.isPresent()) scheduleService.deleteById(id);

        return "redirect:/schedules";
    }

    @PostMapping(path="/schedules/search/date")
    public String searchByDate(@RequestParam String date, Model model) {
        List<ScheduleView> scheduleViews = new ArrayList<>();

        for(Schedule sch: scheduleService.findByDate(Date.valueOf(date))) {
            scheduleViews.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    groupService.findById(sch.getGroupId()).get().getName(),
                    teacherService.findById(sch.getTeacherId()).get().getFullName(),
                    sch.getOrderTime(),
                    subjectService.findById(sch.getSubjectId()).get().getName(),
                    classroomService.findById(sch.getClassroomId()).get().getName()));
        }

        model.addAttribute("schedules", scheduleViews);
        return "html/schedule/all-schedules";
    }

    @PostMapping(path="/schedules/search/by-group")
    public String searchByGroup(@RequestParam String groupId, Model model) {
        List<ScheduleView> scheduleViews = new ArrayList<>();

        for(Schedule sch: scheduleService.findByGroupId(Long.valueOf(groupId))) {
            scheduleViews.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    groupService.findById(sch.getGroupId()).get().getName(),
                    teacherService.findById(sch.getTeacherId()).get().getFullName(),
                    sch.getOrderTime(),
                    subjectService.findById(sch.getSubjectId()).get().getName(),
                    classroomService.findById(sch.getClassroomId()).get().getName()));
        }

        model.addAttribute("schedules", scheduleViews);
        return "html/schedule/all-schedules";
    }

    @PostMapping(path="/schedules/search/by-teacher")
    public String searchByTeacher(@RequestParam String teacherId, Model model) {
        List<ScheduleView> scheduleViews = new ArrayList<>();

        for(Schedule sch: scheduleService.findByTeacherId(Long.valueOf(teacherId))) {
            scheduleViews.add(new ScheduleView(
                    sch.getId(),
                    sch.getCalendarDate(),
                    groupService.findById(sch.getGroupId()).get().getName(),
                    teacherService.findById(sch.getTeacherId()).get().getFullName(),
                    sch.getOrderTime(),
                    subjectService.findById(sch.getSubjectId()).get().getName(),
                    classroomService.findById(sch.getClassroomId()).get().getName()));
        }

        model.addAttribute("schedules", scheduleViews);
        return "html/schedule/all-schedules";
    }
}
