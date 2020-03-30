package com.allinoneuibackend.packageservice.controller;

import com.allinoneuibackend.packageservice.exceptions.DockerException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.allinoneuibackend.packageservice.service.DockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DockerController {

    @Autowired
    private DockerService dockerService;

    @PostMapping("/getContainersByName")
    public List<Container> getDockersByName(@RequestParam("containerName") String containerName) {
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();

        List<Container> containers = dockerClient.listContainersCmd()
                .withShowSize(true)
                .withShowAll(true)
                .withNameFilter(dockerService.getCollection(containerName))
                .exec();

        return containers;
    }

    @PostMapping("/startProfiler")
    public ResponseEntity startProfiler(
            @RequestParam("envList") List<String> envList,
            @RequestParam("imageName") String imageName,
            @RequestParam("stepCount") int stepCount) {

        CreateContainerResponse container = null;

        try {
            for (int i = 0; i < stepCount; i++) {
                DockerClient dockerClient = DockerClientBuilder.getInstance().build();
                container = dockerClient.createContainerCmd(imageName)
                        .withEnv(envList)
                        .exec();

                dockerClient.startContainerCmd(container.getId()).exec();
            }
            return ResponseEntity.status(HttpStatus.OK).body(container.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }

    @PostMapping("/getLogs")
    public ResponseEntity getLogs(
            @RequestParam("containerId") String containerId) {

        List<String> logs = null;
        try {
            DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
            logs = dockerService.getDockerLogs(dockerClient, containerId, (int) (System.currentTimeMillis() / 1000));
        } catch (DockerException e) {
            e.getLocalizedMessage();
            return ResponseEntity.status(500).body(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().body(logs);
    }
}
