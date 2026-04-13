package com.onboarding.smart_onboarding.service;

import com.onboarding.smart_onboarding.model.*;
import com.onboarding.smart_onboarding.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DocumentService {

    @Autowired private DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void uploadDocument(Employee employee, String docName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Document doc = new Document();
        doc.setDocumentName(docName);
        doc.setFileName(fileName);
        doc.setFilePath(filePath.toString());
        doc.setUploadedAt(LocalDateTime.now());
        doc.setStatus("UPLOADED");
        doc.setEmployee(employee);
        documentRepository.save(doc);
    }

    public List<Document> getDocumentsForEmployee(Long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }

    public void verifyDocument(Long docId) {
        Document doc = documentRepository.findById(docId).orElseThrow();
        doc.setStatus("VERIFIED");
        documentRepository.save(doc);
    }
}