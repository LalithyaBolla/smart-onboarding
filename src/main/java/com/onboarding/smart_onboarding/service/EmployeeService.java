package com.onboarding.smart_onboarding.service;

import com.onboarding.smart_onboarding.model.Employee;
import com.onboarding.smart_onboarding.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeByUserId(Long userId) {
        return employeeRepository.findByUserId(userId);
    }
}