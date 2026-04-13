package com.onboarding.smart_onboarding.controller;

import com.onboarding.smart_onboarding.model.Employee;
import com.onboarding.smart_onboarding.model.User;
import com.onboarding.smart_onboarding.repository.EmployeeRepository;
import com.onboarding.smart_onboarding.repository.UserRepository;
import com.onboarding.smart_onboarding.service.ChecklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ChecklistService checklistService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/signup")
    public String signupPage() { return "signup"; }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println("Signup error: " + e.getMessage());
        }
        return "redirect:/login";
    }

    @PostMapping("/hr/add-employee")
    public String addEmployee(@RequestParam String fullName,
                              @RequestParam String username,
                              @RequestParam String password,
                              @RequestParam String jobRole,
                              @RequestParam String department) {
        try {
            // Step 1: Save user
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ROLE_EMPLOYEE");
            userRepository.save(user);
            System.out.println("User saved: " + username);

            // Step 2: Save employee
            Employee employee = new Employee();
            employee.setFullName(fullName);
            employee.setJobRole(jobRole);
            employee.setDepartment(department);
            employee.setJoiningDate(LocalDate.now());
            employee.setUser(user);
            employeeRepository.save(employee);
            System.out.println("Employee saved: " + fullName);

            // Step 3: Generate checklist (won't crash even if AI fails)
            checklistService.generateAndSaveChecklist(employee);
            System.out.println("Checklist generated for: " + fullName);

        } catch (Exception e) {
            System.out.println("Add employee error: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/hr/dashboard";
    }
}