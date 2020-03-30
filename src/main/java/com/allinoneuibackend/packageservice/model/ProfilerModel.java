package com.allinoneuibackend.packageservice.model;

import java.util.List;

public class ProfilerModel {

    private List<String> envList;
    private String imageName;
    private String stepCount;

    public ProfilerModel(List<String> envList, String imageName, String stepCount) {
        this.envList = envList;
        this.imageName = imageName;
        this.stepCount = stepCount;
    }

    public List<String> getEnvList() {
        return envList;
    }

    public void setEnvList(List<String> envList) {
        this.envList = envList;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }
}
