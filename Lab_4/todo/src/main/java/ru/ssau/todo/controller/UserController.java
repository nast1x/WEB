package ru.ssau.todo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ssau.todo.dto.UserDto;
import ru.ssau.todo.service.CustomUserDetailsService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final CustomUserDetailsService userService;

    public UserController(CustomUserDetailsService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        var user = userService.registerUser(userDto);
        var dto = new UserDto(user.getUsername(), user.getRoles());
        return ResponseEntity.ok(dto);
    }
}
