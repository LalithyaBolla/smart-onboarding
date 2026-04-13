package com.onboarding.smart_onboarding.controller;

import com.onboarding.smart_onboarding.model.Employee;
import com.onboarding.smart_onboarding.model.OnboardingTask;
import com.onboarding.smart_onboarding.model.User;
import com.onboarding.smart_onboarding.repository.EmployeeRepository;
import com.onboarding.smart_onboarding.repository.UserRepository;
import com.onboarding.smart_onboarding.service.ChecklistService;
import com.onboarding.smart_onboarding.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ChecklistService checklistService;
    @Autowired private DocumentService documentService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName());
        if (user.getRole().equals("ROLE_HR")) {
            return "redirect:/hr/dashboard";
        } else {
            return "redirect:/employee/dashboard";
        }
    }

    @GetMapping("/hr/dashboard")
    public String hrDashboard(Model model) {
        model.addAttribute("employees", employeeRepository.findAll());
        return "hr-dashboard";
    }

    @GetMapping("/employee/dashboard")
    public String employeeDashboard(Authentication auth, Model model) {
        User user = userRepository.findByUsername(auth.getName());
        Employee employee = employeeRepository.findByUserId(user.getId());
        List<OnboardingTask> tasks = checklistService.getTasksForEmployee(employee.getId());
        long completedCount = checklistService.countCompleted(employee.getId());
        int totalCount = tasks.size();
        int progressPercent = totalCount > 0 ? (int)(completedCount * 100 / totalCount) : 0;

        model.addAttribute("employee", employee);
        model.addAttribute("tasks", tasks);
        model.addAttribute("documents", documentService.getDocumentsForEmployee(employee.getId()));
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("progressPercent", progressPercent);
        return "employee-dashboard";
    }

    @PostMapping("/employee/complete-task/{taskId}")
    public String completeTask(@PathVariable Long taskId) {
        checklistService.markTaskComplete(taskId);
        return "redirect:/employee/dashboard";
    }
}