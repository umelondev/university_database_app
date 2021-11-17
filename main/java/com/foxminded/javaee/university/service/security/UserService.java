package com.foxminded.javaee.university.service.security;

import com.foxminded.javaee.university.model.security.Role;
import com.foxminded.javaee.university.model.security.User;
import com.foxminded.javaee.university.repo.security.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        log.debug("Getting User by id={}:", id);

        if(userRepository.findById(id).isEmpty()) {
            log.warn("User id={} does not exist!\n", id);
            return Optional.empty();
        } else {
            log.debug("User successfully founded!");
            return userRepository.findById(id);
        }
    }

    @Transactional(readOnly = true)
    public boolean isUserExist(User user) {
        User userFromDB = findByUsername(user.getUsername());
        if (userFromDB != null) return true;
        else return false;
    }
    @Transactional(readOnly = true)
    public boolean isValidUsername(User userFromForm, String newUsername) {
        List<String> userNameList = findAll().stream()
                .map(User::getUsername).collect(Collectors.toList());
        userNameList.remove(findById(userFromForm.getId()).get().getUsername());

        if (userNameList.isEmpty()) return true;
        else for (String s : userNameList)
            if (!newUsername.equals(s)) {
                userFromForm.setUsername(newUsername);
                return true;
            }
        return false;
    }
    @Transactional(readOnly = true)
    public boolean isValidPassword(User userFromForm, String newPassword) {
        String curPassword = findById(userFromForm.getId()).get().getPassword();
        if (!newPassword.isEmpty()) {
            userFromForm.setPassword(passwordEncoder.encode(newPassword));
            return true;
        } else {
            userFromForm.setPassword(curPassword);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public void uploadPhoto(User userFromForm, MultipartFile file) throws IOException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            String fileName = "%s.%s".formatted(UUID.randomUUID(), StringUtils.cleanPath(file.getOriginalFilename()));
            Path uploadPath = Paths.get("/upload/" + userFromForm.getId());

            if (!Files.exists(uploadPath))
                Files.createDirectories(uploadPath);

            InputStream is = file.getInputStream();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);

            userFromForm.setPhoto(filePath.toString().substring(1));
        }
    }

    public void save(User user) {
        if (!user.isActive()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            user.setRole(Role.USER);
            user.setPhoto("images/user/usr_default.png");
        }
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        log.debug("Getting User by username: {}", username);

        User user = userRepository.findByUsername(username);

        if (user != null) {
            log.debug("User received!\n");
            return user;
        } else {
            log.warn("User with username: {}, does not exist!\n", username);
            return null;
        }
    }
}
