package com.foxminded.javaee.university.controller.security;

import com.foxminded.javaee.university.model.security.Role;
import com.foxminded.javaee.university.model.security.User;
import com.foxminded.javaee.university.service.*;
import com.foxminded.javaee.university.service.data.DataGenerator;
import com.foxminded.javaee.university.service.security.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

@Controller
public class UserController {

    private final UserService userService;

    private final ClassroomService classroomService;

    private final GroupService groupService;

    private final ScheduleService scheduleService;

    private final StudentService studentService;

    private final SubjectService subjectService;

    private final TeacherService teacherService;

    private DataGenerator generator;

    public UserController(ClassroomService classroomService, GroupService groupService,
                          ScheduleService scheduleService, StudentService studentService,
                          SubjectService subjectService, TeacherService teacherService,
                          UserService userService) {
        this.classroomService = classroomService;
        this.groupService = groupService;
        this.scheduleService = scheduleService;
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.teacherService = teacherService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        generator = new DataGenerator(groupService, subjectService, studentService, teacherService,
                classroomService, scheduleService);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(path="/users")
    public String getAllObjects(Model model) {
        model.addAttribute("users", userService.findAll());
        return "html/user/all-users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("users/{id}")
    public String showDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/users");
        model.addAttribute("button", "Back to users list");

        if(Pattern.matches("\\d+", id)) {
            Optional<User> userOptional = userService.findById(Long.valueOf(id));

            if (userOptional.isEmpty()) {
                model.addAttribute("message", String.format("User with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("username", userOptional.get().getUsername());
            model.addAttribute("role", userOptional.get().getRole());
            return "html/user/detail-user";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("users/{id}/edit")
    public String editDetails(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("link", "/users");
        model.addAttribute("button", "Back to users list");

        if(Pattern.matches("\\d+", id)) {
            Optional<User> userOptional = userService.findById(Long.valueOf(id));
            model.addAttribute("user", userOptional);

            if (userOptional.isEmpty()) {
                model.addAttribute("message", String.format("User with id=%s does not exist or was deleted.", id));
                return "error/500";
            }

            model.addAttribute("id", id);
            model.addAttribute("username", userOptional.get().getUsername());
            model.addAttribute("roles", Role.values());
            return "html/user/edit-user";

        } else model.addAttribute("message", String.format("Number was expected, but was entered: '%s'.", id));
        return "error/500";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/users/{id}/edit")
    public String updateObject(@PathVariable(value = "id") long id, @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "html/user/edit-user";

        userService.save(user);
        return "redirect:/users/{id}";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/generator")
    public String generateDB() {
        generator.generateClassrooms(7);
        generator.generateGroups(10);
        generator.generateTeachers(15);
        generator.generateSubjects();
        generator.referenceGroupSubjects();
        generator.generateStudents(200);
        generator.generateSchedule("2020-09-01", 1);
        return "redirect:/";
    }

    @GetMapping(path = "/profile")
    public String viewProfile(HttpServletRequest request, Model model) {
        User user = userService.findByUsername(request.getRemoteUser());
        model.addAttribute("user", user);
        model.addAttribute("role", user.getRole().name());
        return "html/user/profile";
    }

    @PostMapping(path = "/profile")
    public String updateProfile(@RequestParam("username") String username, @RequestParam("password") String newPassword,
                                @RequestParam("fileImage") MultipartFile fileImage, @Valid User user,
                                BindingResult bindingResult, RedirectAttributes redirAttrs) throws IOException {
        if (bindingResult.hasErrors()) return "html/user/profile";

        if (!userService.isValidUsername(user, username)) {
            redirAttrs.addFlashAttribute("usernameExist", String.format("username \"%s\" is already in use!",
                    user.getUsername()));
            return "redirect:/profile";
        }

        if (!userService.isValidPassword(user, newPassword)) {
            redirAttrs.addFlashAttribute("invalidPassword", "invalid password!");
            return "redirect:/profile";
        }

        userService.uploadPhoto(user, fileImage);

        userService.save(user);
        updateSession(user);
        redirAttrs.addFlashAttribute("success", "Profile successfully updated!");

        return "redirect:/profile";
    }

    private void updateSession(User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), auth.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
