package com.caps.eteeapp.service;

import com.caps.eteeapp.model.Document;
import com.caps.eteeapp.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public List<Document> getDocumentsByApplicationId(Long applicationId) {
        return documentRepository.findByApplication_ApplicationId(applicationId);
    }
}
