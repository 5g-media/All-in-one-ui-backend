package com.allinoneuibackend.packageservice;

import com.allinoneuibackend.packageservice.service.FileStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    private MockMultipartFile mockFile;
    private String basePath;

    @Before
    public void init() {
        this.mockFile = FileStorageTestUtils.getMockMultipartFile();
        this.basePath = fileStorageService.getBasePath();
    }

//    @Test
//    public void getContentTest() throws IOException {
//        String content = fileStorageService.getContent("vTranscoder_vnfd/vTranscoder_vnfd.yaml", basePath);
//        assertNotNull(content);
//    }

    @Test
    public void storeFileTest() {
        fileStorageService.storeFile(mockFile);
        String fileName = StringUtils.cleanPath(mockFile.getOriginalFilename());
        File file = new File(fileStorageService.getBasePath() + "/" + fileName);
        assertTrue(mockFile.getSize() == file.length());
    }

    @Test
    public void compressFileTest() throws IOException {
        String fileName = "yaml/unnamed/unnamed.yaml";
        fileStorageService.compress(fileName,"osm", true);
        File file = new File(
                fileStorageService.getBasePath() + "/"
                        + fileName.substring(fileName.indexOf("/") + 1, fileName.lastIndexOf("/")) + ".tar.gz");
        assertTrue(file.length() > 0 && file.getName().contains("tar.gz"));
    }

    @Test
    public void SaveAsYamlTest() throws IOException {
        String fileName = "yaml/unnamed/unnamed.yaml";
        String content = "hellooooo";

        fileStorageService.saveAsYaml(content, fileName);
        File file = new File(fileStorageService.getBasePath() + "/" + fileName);
        assertTrue(file.length() > 0 && file.getName().contains("yaml"));
    }
}
