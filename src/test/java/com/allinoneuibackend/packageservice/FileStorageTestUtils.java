package com.allinoneuibackend.packageservice;

import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStorageTestUtils {

    public static MockMultipartFile getMockMultipartFile() {
        String workingDir = System.getProperty("user.dir");
        Path path = Paths.get(workingDir + "/uploads/vTranscoder_vnfd.tar.gz");
        String name = "file";
        String originalFileName = "file.tar.gz";
        String contentType = "application/gzip";
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return new MockMultipartFile(name, originalFileName, contentType, content);
    }
}
