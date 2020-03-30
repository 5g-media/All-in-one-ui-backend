package com.allinoneuibackend.packageservice.service;

import com.allinoneuibackend.packageservice.exceptions.DockerException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class DockerServiceImpl implements DockerService {

    @Override
    public Collection<String> getCollection(String str) {
        List<String> filter = new ArrayList<>();
        filter.add(str);
        return filter;
    }

    @Override
    public List<String> getDockerLogs(DockerClient dockerClient, String containerId, int lastLogTime) throws DockerException {

        final List<String> logs = new ArrayList<>();

        LogContainerCmd logContainerCmd = dockerClient.logContainerCmd(containerId);
        logContainerCmd.withStdOut(true).withStdErr(true);
        logContainerCmd.withSince(lastLogTime);  // UNIX timestamp (integer) to filter logs. Specifying a timestamp will only output log-entries since that timestamp.

        logContainerCmd.withTimestamps(false);
        logContainerCmd.withFollowStream(true);

        try {
            logContainerCmd.exec(new LogContainerResultCallback() {
                @Override
                public void onNext(Frame item) {
                    logs.add(item.toString().substring(7) + "\n");
                }
            }).awaitCompletion();
        } catch (Exception e) {
            System.out.println("Interrupted Exception!" + e.getLocalizedMessage());
            throw new DockerException(e.getLocalizedMessage());
        }

        lastLogTime = (int) (System.currentTimeMillis() / 1000) + 5;  // assumes at least a 5 second wait between calls to getDockerLogs

        return logs;
    }
}
