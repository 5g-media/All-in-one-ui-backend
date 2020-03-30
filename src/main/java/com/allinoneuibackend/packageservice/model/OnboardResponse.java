package com.allinoneuibackend.packageservice.model;

public class OnboardResponse {
    private String fileName;
    private String message;
    private Boolean isSuccess;

    public OnboardResponse() {
    }

    public OnboardResponse(String fileName, String message, Boolean isSuccess) {
        this.fileName = fileName;
        this.message = message;
        this.isSuccess = isSuccess;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }
}
