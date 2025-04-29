package com.caps.eteeapp.controller;

import com.caps.eteeapp.model.Document;
import com.caps.eteeapp.model.FileResponse;
import com.caps.eteeapp.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocuments(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("applicationId") Long applicationId,
            @RequestParam("documentType") String documentType) {


        if (files.length == 0) {
            return ResponseEntity.badRequest().body("No files were uploaded");
        }


        for (MultipartFile file : files) {
            if (file.getSize() > 15 * 1024 * 1024) { // 15MB in bytes
                return ResponseEntity.badRequest().body("One or more files exceed the limit of 15MB");
            }
        }

        List<FileResponse> responses = Arrays.stream(files)
                .map(file -> {
                    Document document = documentService.uploadDocument(applicationId, documentType, file);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/api/documents/")
                            .path(document.getDocumentId().toString())
                            .toUriString();

                    return FileResponse.fromDocument(document, fileDownloadUri);
                })
                .collect(Collectors.toList());

        // Return a single object for single upload, or a list for multiple uploads
        return ResponseEntity.ok(files.length == 1 ? responses.get(0) : responses);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id, HttpServletRequest request) {

        Resource resource = documentService.getDocumentFile(id);


        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
        }


        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<Document>> getDocumentsByApplicationId(@PathVariable Long applicationId) {
        List<Document> documents = documentService.getDocumentsByApplicationId(applicationId);
        return ResponseEntity.ok(documents);
    }
}