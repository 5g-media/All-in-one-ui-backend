package com.allinoneuibackend.packageservice.model;

public class UploadFileResponse {
    private String filePath;
    private String folderPath;
    private String fileContent;
    private boolean isError;

    public UploadFileResponse(String filePath, boolean isError) {
        this.filePath = filePath;
        this.isError = isError;
    }

    public UploadFileResponse() {
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
