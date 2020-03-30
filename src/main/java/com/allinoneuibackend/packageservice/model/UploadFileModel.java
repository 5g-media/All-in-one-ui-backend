package com.allinoneuibackend.packageservice.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadFileModel {
    private MultipartFile file;
    private boolean isArchive;
    private String schemaName;
    private String descriptorType;

    public UploadFileModel(MultipartFile file, boolean isArchive, String schemaName, String descriptorType) {
        this.file = file;
        this.isArchive = isArchive;
        this.schemaName = schemaName;
        this.descriptorType = descriptorType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public boolean isArchive() {
        return isArchive;
    }

    public void setArchive(boolean archive) {
        isArchive = archive;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDescriptorType() {
        return descriptorType;
    }

    public void setDescriptorType(String descriptorType) {
        this.descriptorType = descriptorType;
    }
}
