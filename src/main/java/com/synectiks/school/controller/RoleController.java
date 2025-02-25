package com.synectiks.school.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.google.api.services.storage.Storage.BucketAccessControls.List;
import com.synectiks.school.service.RoleService;

@RestController
@RequestMapping("/schools/{schoolId}/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    @GetMapping
    public ResponseEntity<?> getAllRoles(@PathVariable String schoolId) {
        try {
            List<Map<String, Object>> roles = roleService.getAllRoles(schoolId);
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching roles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{roleName}")
    public ResponseEntity<Map<String, Object>> getRole(
            @PathVariable String schoolId, 
            @PathVariable String roleName) {
        return ResponseEntity.ok(roleService.getRole(schoolId, roleName));
    }

    @PostMapping
    public ResponseEntity<String> createRole(@PathVariable String schoolId, @RequestBody Map<String, Object> roleData) {
        String roleName = (String) roleData.get("name");
      List<String> permissions = (List<String>) roleData.get("permissions");
        roleService.createRole(schoolId, roleName, permissions);
        return ResponseEntity.ok("Role created successfully");
    }

    @PutMapping("/{roleName}")
    public ResponseEntity<String> updateRole(@PathVariable String schoolId, @PathVariable String roleName, @RequestBody Map<String, Object> roleData) {
        List<String> permissions = (List<String>) roleData.get("permissions");
        roleService.updateRole(schoolId, roleName, permissions);
        return ResponseEntity.ok("Role updated successfully");
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<String> deleteRole(@PathVariable String schoolId, @PathVariable String roleName) {
        roleService.deleteRole(schoolId, roleName);
        return ResponseEntity.ok("Role deleted successfully");
    }
}