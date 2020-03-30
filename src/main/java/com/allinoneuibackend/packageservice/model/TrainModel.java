package com.allinoneuibackend.packageservice.model;

public class TrainModel {
    private String alpha;
    private String bgTrafficPattern;
    private String actorLearningRate;
    private String criticLearningRate;
    private String linkCapacity;
    private String nameNnModel;
    private String uploadFileName;
    private String modelType;
    private String rewardFunction;
    private String parallelAgent;
    private String seedNumber;



    public TrainModel(String alpha, String bgTrafficPattern, String actorLearningRate, String criticLearningRate, String linkCapacity, String nameNnModel, String modelType, String rewardFunction, String parallelAgent, String seedNumber) {
        this.alpha = alpha;
        this.bgTrafficPattern = bgTrafficPattern;
        this.actorLearningRate = actorLearningRate;
        this.criticLearningRate = criticLearningRate;
        this.linkCapacity = linkCapacity;
        this.nameNnModel = nameNnModel;
        this.uploadFileName = uploadFileName;
        this.modelType = modelType;
        this.rewardFunction = rewardFunction;
        this.parallelAgent = parallelAgent;
        this.seedNumber = seedNumber;
    }

    public TrainModel() {
    }

    public String getCriticLearningRate() {
        return criticLearningRate;
    }

    public void setCriticLearningRate(String criticLearningRate) {
        this.criticLearningRate = criticLearningRate;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }
    
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }
    
    public String getRewardFunction() {
        return rewardFunction;
    }

    public void setRewardFunction(String rewardFunction) {
        this.rewardFunction = rewardFunction;
    }
    
    public String getParallelAgent() {
        return parallelAgent;
    }

    public void setParallelAgent(String parallelAgent) {
        this.parallelAgent = parallelAgent;
    }


    public String getBgTrafficPattern() {
        return bgTrafficPattern;
    }

    public void setBgTrafficPattern(String bgTrafficPattern) {
        this.bgTrafficPattern = bgTrafficPattern;
    }

    public String getActorLearningRate() {
        return actorLearningRate;
    }

    public void setActorLearningRate(String actorLearningRate) {
        this.actorLearningRate = actorLearningRate;
    }

    public String getLinkCapacity() {
        return linkCapacity;
    }

    public void setLinkCapacity(String linkCapacity) {
        this.linkCapacity = linkCapacity;
    }

    public String getSeedNumber() {
        return seedNumber;
    }

    public void setSeedNumber(String seedNumber) {
        this.seedNumber = seedNumber;
    }


    public String getNameNnModel() {
        return nameNnModel;
    }

    public void setNameNnModel(String nameNnModel) {
        this.nameNnModel = nameNnModel;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }
}
