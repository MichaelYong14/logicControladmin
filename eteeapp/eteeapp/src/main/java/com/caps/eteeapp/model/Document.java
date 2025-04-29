package com.caps.eteeapp.model;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;

@Data
@Entity
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private ApplicantApplication application;

    private String documentType;

    private String filePath;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;

    private String fileName;

    private String fileType;

    private Long fileSize;

}