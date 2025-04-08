package com.synectiks.school.controller;
 
import com.synectiks.school.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    @Autowired
    private FirebaseAuthService firebaseAuthService;
 
    @PostMapping("/signup")
    public Map<String, String> signUp(@RequestBody Map<String, String> user) {
        try {
            System.out.println(user);
            String schoolName = user.get("schoolName");
            String email = user.get("email");
            String location = user.get("location");
            String password = user.get("password");
 
            return firebaseAuthService.signUp(schoolName, email, location, password);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to register school: " + e.getMessage());
            return errorResponse;
        }
    }
 
    @PostMapping("/signin")
    public Map<String, String> signIn(@RequestBody Map<String, String> user) {
        try {
            String email = user.get("email");
            String password = user.get("password");
            return firebaseAuthService.signIn(email, password);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to sign in: " + e.getMessage());
            return errorResponse;
        }
    }
 
 
    @PostMapping("/schools/{schoolId}/create-parents")
    public Map<String, Map<String, String>> createParents(@PathVariable String schoolId, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("##############");

            // Validate path variable schoolId
            if (schoolId == null || schoolId.trim().isEmpty()) {
                return Map.of("error", Map.of("message", "School ID in path is required"));
            }

            // Get parents list from request body
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, String>> parents = (java.util.List<Map<String, String>>) request.get("parents");

            if (parents == null || parents.isEmpty()) {
                return Map.of("error", Map.of("message", "Parent data is required in request body"));
            }

            // Validate each parent object has required fields
            for (Map<String, String> parent : parents) {
                if (!parent.containsKey("studentName") || parent.get("studentName") == null || parent.get("studentName").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Student name is required for all parents"));
                }
                if (!parent.containsKey("email") || parent.get("email") == null || parent.get("email").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Email is required for all parents"));
                }
                if (!parent.containsKey("class") || parent.get("class") == null || parent.get("class").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Class is required for all parents"));
                }
                if (!parent.containsKey("parentName") || parent.get("parentName") == null || parent.get("parentName").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Parent name is required for all parents"));
                }
                if (!parent.containsKey("classteacher") || parent.get("classteacher") == null || parent.get("classteacher").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Class teacher is required for all parents"));
                }
            }

            return firebaseAuthService.createParentAccounts(schoolId, parents);

        } catch (ClassCastException e) {
            return Map.of("error", Map.of("message", "Invalid request body format: " + e.getMessage()));
        } catch (Exception e) {
            return Map.of("error", Map.of("message", "Failed to process request: " + e.getMessage()));
        }
    }

    
    @PostMapping("/schools/{schoolId}/create-teachers")
    public Map<String, Map<String, String>> createTeachers(
            @PathVariable String schoolId,
            @RequestBody Map<String, Object> request) {
        try {
            System.out.println("##############");
            if (schoolId == null || schoolId.isEmpty()) {
                return Map.of("error", Map.of("message", "School ID is required"));
            }

            java.util.List<Map<String, String>> teachers = (java.util.List<Map<String, String>>) request.get("teachers");
            if (teachers == null || teachers.isEmpty()) {
                return Map.of("error", Map.of("message", "Teacher data is required"));
            }

            // Add randomly generated TID to each teacher map
            for (Map<String, String> teacher : teachers) {
                String teacherId = UUID.randomUUID().toString(); // Generates format like 7eb84698-2ce6-418a-b588-63e40b555c0c
                teacher.put("teacherId", teacherId);
            }

            return firebaseAuthService.createTeacherAccounts(schoolId, teachers);
        } catch (Exception e) {
            return Map.of("error", Map.of("message", "Failed to process request: " + e.getMessage()));
        }
    }

    
    @PostMapping("/schools/{schoolId}/create-hods")
    public Map<String, Map<String, String>> createHods(@PathVariable String schoolId, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("##############");

            // Validate path variable schoolId
            if (schoolId == null || schoolId.trim().isEmpty()) {
                return Map.of("error", Map.of("message", "School ID in path is required"));
            }

            // Get hods list from request body
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, String>> hods = (java.util.List<Map<String, String>>) request.get("hods");

            if (hods == null || hods.isEmpty()) {
                return Map.of("error", Map.of("message", "HOD data is required in request body"));
            }

            // Validate each HOD object has required fields
            for (Map<String, String> hod : hods) {
                if (!hod.containsKey("HODName") || hod.get("HODName") == null || hod.get("HODName").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "HODName is required for all HODs"));
                }
                if (!hod.containsKey("Department") || hod.get("Department") == null || hod.get("Department").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Department is required for all HODs"));
                }
                if (!hod.containsKey("email") || hod.get("email") == null || hod.get("email").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Email is required for all HODs"));
                }
            }

            return firebaseAuthService.createHodAccounts(schoolId, hods);

        } catch (ClassCastException e) {
            return Map.of("error", Map.of("message", "Invalid request body format: " + e.getMessage()));
        } catch (Exception e) {
            return Map.of("error", Map.of("message", "Failed to process request: " + e.getMessage()));
        }
    }

    
    @PostMapping("/schools/{schoolId}/create-hr")
    public Map<String, Map<String, String>> createHR(@PathVariable String schoolId, @RequestBody Map<String, Object> request) {
        try {
            // Validate path variable schoolId
            if (schoolId == null || schoolId.trim().isEmpty()) {
                return Map.of("error", Map.of("message", "School ID in path is required"));
            }

            // Get hrs list from request body
            @SuppressWarnings("unchecked")
            List<Map<String, String>> hrs = (List<Map<String, String>>) request.get("hrs");

            if (hrs == null || hrs.isEmpty()) {
                return Map.of("error", Map.of("message", "HR data is required in request body"));
            }

            // Validate each HR object has required fields
            for (Map<String, String> hr : hrs) {
                if (!hr.containsKey("HRName") || hr.get("HRName") == null || hr.get("HRName").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "HRName is required for all HRs"));
                }
                if (!hr.containsKey("email") || hr.get("email") == null || hr.get("email").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Email is required for all HRs"));
                }
            }

            return firebaseAuthService.createHRAccounts(schoolId, hrs);

        } catch (ClassCastException e) {
            return Map.of("error", Map.of("message", "Invalid request body format: " + e.getMessage()));
        } catch (Exception e) {
            return Map.of("error", Map.of("message", "Failed to process request: " + e.getMessage()));
        }
    }

    
    @PostMapping("/schools/{schoolId}/create-coordinators")
    public Map<String, Map<String, String>> createCoordinators(@PathVariable String schoolId, @RequestBody Map<String, Object> request) {
        try {
            System.out.println("##############");

            // Validate path variable schoolId
            if (schoolId == null || schoolId.trim().isEmpty()) {
                return Map.of("error", Map.of("message", "School ID in path is required"));
            }

            // Get coordinators list from request body
            @SuppressWarnings("unchecked")
            java.util.List<Map<String, String>> coordinators = (java.util.List<Map<String, String>>) request.get("coordinators");

            if (coordinators == null || coordinators.isEmpty()) {
                return Map.of("error", Map.of("message", "Coordinator data is required in request body"));
            }

            // Validate each Coordinator object has required fields
            for (Map<String, String> coordinator : coordinators) {
                if (!coordinator.containsKey("coordinatorName") || coordinator.get("coordinatorName") == null || coordinator.get("coordinatorName").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Coordinator name is required for all coordinators"));
                }
                if (!coordinator.containsKey("email") || coordinator.get("email") == null || coordinator.get("email").trim().isEmpty()) {
                    return Map.of("error", Map.of("message", "Email is required for all coordinators"));
                }
            }

            return firebaseAuthService.createCoordinatorAccounts(schoolId, coordinators);

        } catch (ClassCastException e) {
            return Map.of("error", Map.of("message", "Invalid request body format: " + e.getMessage()));
        } catch (Exception e) {
            return Map.of("error", Map.of("message", "Failed to process request: " + e.getMessage()));
        }
    }

 
}