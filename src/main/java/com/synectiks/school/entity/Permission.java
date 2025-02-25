package com.synectiks.school.entity;

public class Permission {
    private String category;
    private String permission;
    private String description;

    // Default constructor
    public Permission() {
    }

    // Parameterized constructor
    public Permission(String category, String permission, String description) {
        this.category = category;
        this.permission = permission;
        this.description = description;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
