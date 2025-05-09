package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Applicant;
import com.caps.eteeapp.model.Document;
import com.caps.eteeapp.repository.ApplicantRepository;
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
    private ApplicantRepository applicantRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public List<Document> getDocumentsByApplicantId(Long applicantId) {
        return documentRepository.findByApplicant_ApplicantId(applicantId);
    }

    public Document uploadDocument(Long applicantId, String documentType, MultipartFile file) {
        Optional<Applicant> applicantOpt = applicantRepository.findById(applicantId);
        if (!applicantOpt.isPresent()) {
            throw new RuntimeException("Applicant not found with id " + applicantId);
        }

        String fileName = fileStorageService.storeFile(file);

        Document document = new Document();
        document.setApplicant(applicantOpt.get());
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
        documentRepository.deleteById(documentId);
    }
}