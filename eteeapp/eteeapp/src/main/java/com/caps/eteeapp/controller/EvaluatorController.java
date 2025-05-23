package com.caps.eteeapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caps.eteeapp.model.Department;
import com.caps.eteeapp.model.Evaluator;
import com.caps.eteeapp.service.DepartmentService;
import com.caps.eteeapp.service.EvaluatorService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/evaluators")
public class EvaluatorController {

    @Autowired
    private EvaluatorService evaluatorService;
    
    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Evaluator>> getAllEvaluators() {
        List<Evaluator> evaluators = evaluatorService.getAllEvaluators();
        return ResponseEntity.ok(evaluators);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Evaluator> getEvaluatorById(@PathVariable Long id) {
        Optional<Evaluator> evaluatorOpt = evaluatorService.findEvaluatorById(id);
        if (evaluatorOpt.isPresent()) {
            return ResponseEntity.ok(evaluatorOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<?> getEvaluatorStatus(@PathVariable Long id) {
        Optional<Evaluator> evaluatorOpt = evaluatorService.findEvaluatorById(id);
        if (evaluatorOpt.isPresent()) {
            Evaluator evaluator = evaluatorOpt.get();
            // Update the logic to automatically consider admins as APPROVED
            // This ensures consistency with the frontend changes
            String status = evaluator.isAdmin() ? "APPROVED" : "PENDING";
            return ResponseEntity.ok(
                new EvaluatorStatusResponse(status, evaluator.isAdmin())
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}/admin-status")
    public ResponseEntity<Evaluator> updateAdminStatus(
            @PathVariable Long id,
            @RequestParam boolean isAdmin) {
        
        Optional<Evaluator> evaluatorOpt = evaluatorService.findEvaluatorById(id);
        
        if (evaluatorOpt.isPresent()) {
            Evaluator evaluator = evaluatorOpt.get();
            Evaluator updatedEvaluator = evaluatorService.updateAdminStatus(evaluator, isAdmin);
            return ResponseEntity.ok(updatedEvaluator);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }
    
    // Add POST endpoint for registration
    @PostMapping("/register")
    public ResponseEntity<?> registerEvaluator(@RequestBody RegistrationRequest registrationRequest) {
        try {
            System.out.println("Registration request received: " + registrationRequest.toString()); // Debug log
            
            // Validate required fields
            if (registrationRequest.getName() == null || registrationRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (registrationRequest.getEmail() == null || registrationRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (registrationRequest.getPassword() == null || registrationRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required");
            }
            if (registrationRequest.getDepartment() == null || registrationRequest.getDepartment().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Department is required");
            }
            
            // Check if email already exists
            Optional<Evaluator> existingEvaluator = evaluatorService.findByEmail(registrationRequest.getEmail());
            if (existingEvaluator.isPresent()) {
                return ResponseEntity.badRequest().body("Email already registered");
            }
            
            // Find department by name
            Optional<Department> department = departmentService.findByDepartmentName(registrationRequest.getDepartment());
            if (!department.isPresent()) {
                System.out.println("Department not found: " + registrationRequest.getDepartment()); // Debug log
                return ResponseEntity.badRequest().body("Invalid department selected: " + registrationRequest.getDepartment());
            }
            
            // Create new evaluator from registration request
            Evaluator evaluator = new Evaluator();
            evaluator.setName(registrationRequest.getName());
            evaluator.setEmail(registrationRequest.getEmail());
            evaluator.setPassword(registrationRequest.getPassword());
            evaluator.setContactNumber(registrationRequest.getContactNumber());
            evaluator.setRole(registrationRequest.getRole());
            evaluator.setDepartment(department.get());
            
            System.out.println("Saving evaluator: " + evaluator.toString()); // Debug log
            
            // Register the new evaluator
            Evaluator registeredEvaluator = evaluatorService.registerEvaluator(evaluator);
            return ResponseEntity.ok(registeredEvaluator);
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage()); // Debug log
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
    
    // Add POST endpoint for login
    @PostMapping("/login")
    public ResponseEntity<?> loginEvaluator(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<Evaluator> evaluatorOpt = evaluatorService.loginEvaluator(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            if (evaluatorOpt.isPresent()) {
                Evaluator evaluator = evaluatorOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("evaluatorId", evaluator.getEvaluatorId());
                response.put("name", evaluator.getName());
                response.put("email", evaluator.getEmail());
                response.put("isAdmin", evaluator.isAdmin());
                response.put("role", evaluator.getRole());
                response.put("department", evaluator.getDepartment());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Invalid email or password");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
    
    // Nested class for login request
    private static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    // Nested class for status response
    private static class EvaluatorStatusResponse {
        private String status;
        private boolean isAdmin;
        
        public EvaluatorStatusResponse(String status, boolean isAdmin) {
            this.status = status;
            this.isAdmin = isAdmin;
        }
        
        public String getStatus() {
            return status;
        }
        
        public boolean isAdmin() {
            return isAdmin;
        }
    }
    
    // Nested class for registration request
    private static class RegistrationRequest {
        private String name;
        private String email;
        private String password;
        private String contactNumber;
        private String role;
        private String department;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        @Override
        public String toString() {
            return "RegistrationRequest{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", contactNumber='" + contactNumber + '\'' +
                    ", role='" + role + '\'' +
                    ", department='" + department + '\'' +
                    '}';
        }
    }
}
