package com.onboarding.smart_onboarding.service;

import com.onboarding.smart_onboarding.model.*;
import com.onboarding.smart_onboarding.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChecklistService {

    @Autowired private TaskRepository taskRepository;
    @Autowired private AIService aiService;

    public void generateAndSaveChecklist(Employee employee) {
        try {
            String aiResponse = aiService.generateChecklist(
                    employee.getJobRole(), employee.getDepartment()
            );
            String[] lines = aiResponse.split("\n");
            int saved = 0;
            for (String line : lines) {
                line = line.trim();
                if (line.contains("|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3) {
                        OnboardingTask task = new OnboardingTask();
                        task.setCategory(parts[0].trim());
                        task.setTaskName(parts[1].trim());
                        task.setDescription(parts[2].trim());
                        task.setCompleted(false);
                        task.setEmployee(employee);
                        taskRepository.save(task);
                        saved++;
                    }
                }
            }
            // If AI returned nothing useful, add default tasks
            if (saved == 0) {
                saveDefaultTasks(employee);
            }
        } catch (Exception e) {
            System.out.println("AI failed, using default tasks: " + e.getMessage());
            saveDefaultTasks(employee);
        }
    }

    private void saveDefaultTasks(Employee employee) {
        String[][] defaultTasks = {
                {"IT Setup", "Setup Laptop", "Configure laptop and install required software"},
                {"IT Setup", "Get System Access", "Obtain login credentials for all systems"},
                {"IT Setup", "Setup Email", "Configure company email account"},
                {"HR Documents", "Sign Offer Letter", "Review and sign the offer letter"},
                {"HR Documents", "Submit ID Proof", "Submit Aadhaar, PAN and other ID documents"},
                {"HR Documents", "Complete Form 16", "Fill all HR joining forms"},
                {"Training", "Company Orientation", "Attend company orientation session"},
                {"Training", "Team Training", "Complete role-specific training modules"},
                {"Team Introduction", "Meet Your Team", "Get introduced to your team members"},
                {"Compliance", "Complete Security Training", "Finish mandatory security awareness training"}
        };

        for (String[] t : defaultTasks) {
            OnboardingTask task = new OnboardingTask();
            task.setCategory(t[0]);
            task.setTaskName(t[1]);
            task.setDescription(t[2]);
            task.setCompleted(false);
            task.setEmployee(employee);
            taskRepository.save(task);
        }
    }

    public List<OnboardingTask> getTasksForEmployee(Long employeeId) {
        return taskRepository.findByEmployeeId(employeeId);
    }

    public void markTaskComplete(Long taskId) {
        OnboardingTask task = taskRepository.findById(taskId).orElseThrow();
        task.setCompleted(true);
        taskRepository.save(task);
    }

    public long countCompleted(Long employeeId) {
        return getTasksForEmployee(employeeId).stream()
                .filter(OnboardingTask::isCompleted).count();
    }
}