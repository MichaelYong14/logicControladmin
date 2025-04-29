package com.caps.eteeapp.service;

import com.caps.eteeapp.model.ApplicantApplication;
import com.caps.eteeapp.model.Document;
import com.caps.eteeapp.repository.ApplicantApplicationRepository;
import com.caps.eteeapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ApplicantApplicationRepository applicationRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Document> getDocumentsByApplicationId(Long applicationId) {
        return documentRepository.findByApplication_ApplicationId(applicationId);
    }

    public Document uploadDocument(Long applicationId, String documentType, MultipartFile file) {
        // Validate application exists
        Optional<ApplicantApplication> applicationOpt = applicationRepository.findById(applicationId);
        if (!applicationOpt.isPresent()) {
            throw new RuntimeException("Application not found with id " + applicationId);
        }


        String fileName = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setApplication(applicationOpt.get());
        document.setDocumentType(documentType);
        document.setFilePath(fileName);
        document.setFileName(file.getOriginalFilename());
        document.setUploadDate(new Date());
        document.setFileType(file.getContentType());
        document.setFileSize(file.getSize());

        return documentRepository.save(document);
    }

    public Resource getDocumentFile(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id " + documentId));

        return fileStorageService.loadFileAsResource(document.getFilePath());
    }

    public void deleteDocument(Long documentId) {
        // Only delete metadata from database, keep file on disk
        documentRepository.deleteById(documentId);
    }
}