package ru.ssau.todo.dto;

import ru.ssau.todo.entity.Role;

import java.util.List;

public class UserDto {
    private Long id;
    private String username;
    private String password;
    private List<Role> roles;

    public UserDto() {
    }

    public UserDto(Long id, String username, String password, List<Role> roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public UserDto(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public UserDto(String username, List<Role> roles) {
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
