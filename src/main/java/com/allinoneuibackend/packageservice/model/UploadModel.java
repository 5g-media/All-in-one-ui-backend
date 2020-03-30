package com.allinoneuibackend.packageservice.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadModel {
    private MultipartFile file;
    private String uploadFileName;
    private String schemaName;
    private Boolean isArchive;

    public UploadModel(MultipartFile file, String uploadFileName, String schemaName, Boolean isArchive) {
        this.file = file;
        this.uploadFileName = uploadFileName;
        this.schemaName = schemaName;
        this.isArchive = isArchive;
    }

    public UploadModel() {
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
    }
}
