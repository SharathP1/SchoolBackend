package com.synectiks.school.controller;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
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
 
import com.synectiks.school.service.GroupService;
 
@RestController
@RequestMapping("/schools/{schoolId}/groups")
public class GroupController {
 
    @Autowired
    private GroupService groupService;
 
    @GetMapping
    public ResponseEntity<?> getAllGroups(@PathVariable String schoolId) {
        try {
            List<Map<String, Object>> groups = groupService.getAllGroups(schoolId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error fetching groups: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
 
    @GetMapping("/{groupName}")
    public ResponseEntity<Map<String, Object>> getGroup(
            @PathVariable String schoolId,
            @PathVariable String groupName) {
        return ResponseEntity.ok(groupService.getGroup(schoolId, groupName));
    }
 
    @PostMapping
    public ResponseEntity<String> createGroup(@PathVariable String schoolId, @RequestBody Map<String, Object> groupData) {
        String groupName = (String) groupData.get("name");
        List<String> permissions = (List<String>) groupData.get("permissions");
        groupService.createGroup(schoolId, groupName, permissions);
        return ResponseEntity.ok("Group created successfully");
    }
 
    @PutMapping("/{groupName}")
    public ResponseEntity<String> updateGroup(@PathVariable String schoolId, @PathVariable String groupName, @RequestBody Map<String, Object> groupData) {
        List<String> permissions = (List<String>) groupData.get("permissions");
        groupService.updateGroup(schoolId, groupName, permissions);
        return ResponseEntity.ok("Group updated successfully");
    }
 
    @DeleteMapping("/{groupName}")
    public ResponseEntity<String> deleteGroup(@PathVariable String schoolId, @PathVariable String groupName) {
        groupService.deleteGroup(schoolId, groupName);
        return ResponseEntity.ok("Group deleted successfully");
    }
 
    @PostMapping("/{groupName}/users")
    public ResponseEntity<String> addUserToGroup(@PathVariable String schoolId, @PathVariable String groupName, @RequestBody Map<String, Object> userData) {
        String userId = (String) userData.get("userId");
        groupService.addUserToGroup(schoolId, groupName, userId);
        return ResponseEntity.ok("User added to group successfully");
    }
 
    @PostMapping("/{groupName}/users/batch")
    public ResponseEntity<String> addUsersToGroup(@PathVariable String schoolId, @PathVariable String groupName, @RequestBody Map<String, Object> usersData) {
        List<String> userIds = (List<String>) usersData.get("userIds");
        groupService.addUsersToGroup(schoolId, groupName, userIds);
        return ResponseEntity.ok("Users added to group successfully");
    }
 
    @DeleteMapping("/{groupName}/users/{userId}")
    public ResponseEntity<String> removeUserFromGroup(@PathVariable String schoolId, @PathVariable String groupName, @PathVariable String userId) {
        groupService.removeUserFromGroup(schoolId, groupName, userId);
        return ResponseEntity.ok("User removed from group successfully");
    }
}