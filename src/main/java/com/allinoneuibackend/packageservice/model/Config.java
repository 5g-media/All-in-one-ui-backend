package com.allinoneuibackend.packageservice.model;

public class Config {
    private String content;
    private Boolean status;

    public Config() {
    }

    public Config(String content, Boolean status) {
        this.content = content;
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
