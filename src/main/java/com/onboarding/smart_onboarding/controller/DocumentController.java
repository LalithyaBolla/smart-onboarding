package com.onboarding.smart_onboarding.controller;

import com.onboarding.smart_onboarding.model.*;
import com.onboarding.smart_onboarding.repository.*;
import com.onboarding.smart_onboarding.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class DocumentController {

    @Autowired private DocumentService documentService;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;

    @PostMapping("/employee/upload-document")
    public String uploadDocument(Authentication auth,
                                 @RequestParam String documentName,
                                 @RequestParam MultipartFile file) throws Exception {
        User user = userRepository.findByUsername(auth.getName());
        Employee employee = employeeRepository.findByUserId(user.getId());
        documentService.uploadDocument(employee, documentName, file);
        return "redirect:/employee/dashboard";
    }

    @PostMapping("/hr/verify-document/{docId}")
    public String verifyDocument(@PathVariable Long docId) {
        documentService.verifyDocument(docId);
        return "redirect:/hr/dashboard";
    }
}