package com.allinoneuibackend.packageservice.model;

public class ExportFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private boolean isError;

    public ExportFileResponse(String fileName, String fileDownloadUri, boolean isError) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.isError = isError;
    }

    public ExportFileResponse() {
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileDownloadUri() {
        return fileDownloadUri;
    }

    public void setFileDownloadUri(String fileDownloadUri) {
        this.fileDownloadUri = fileDownloadUri;
    }
}
