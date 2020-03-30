package com.allinoneuibackend.packageservice.model;

public class ExportFileModel {
    private String content;
    private String filePath;
    private Boolean isArchive;
    private Boolean isVnfd;
    private String schemaName;

    public ExportFileModel(String content, String filePath, Boolean isArchive, Boolean isVnfd, String schemaName) {
        this.content = content;
        this.filePath = filePath;
        this.isArchive = isArchive;
        this.isVnfd = isVnfd;
        this.schemaName = schemaName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Boolean getArchive() {
        return isArchive;
    }

    public void setArchive(Boolean archive) {
        isArchive = archive;
    }

    public Boolean getVnfd() {
        return isVnfd;
    }

    public void setVnfd(Boolean vnfd) {
        isVnfd = vnfd;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
