package com.allinoneuibackend.packageservice.service;

import com.allinoneuibackend.packageservice.exceptions.DockerException;
import com.github.dockerjava.api.DockerClient;

import java.util.Collection;
import java.util.List;

public interface DockerService {
    Collection<String> getCollection(String str);
    List<String> getDockerLogs(DockerClient dockerClient, String containerId, int lastLogTime) throws DockerException;
}
