package com.example.uidesignlab;

public class User {

    private String name;
    private String role;

    // Empty constructor required by Firebase
    public User() {
    }

    // Constructor with parameters
    public User(String name, String role) {
        this.name = name;
        this.role = role;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for role
    public String getRole() {
        return role;
    }

    // Setter for role
    public void setRole(String role) {
        this.role = role;
    }
}