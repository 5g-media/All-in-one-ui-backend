package com.allinoneuibackend.packageservice;

import com.allinoneuibackend.packageservice.controller.FileController;
import com.allinoneuibackend.packageservice.service.FileStorageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileControllerTest {

    @Autowired
    private FileController fileController = new FileController();

    @MockBean
    private FileStorageService fileStorageService;

    private MockMultipartFile mockFile;
    private MockMvc mockMvc;

    @Before
    public void init() {
        this.mockMvc = getMockMVC(fileController);
        this.mockFile = FileStorageTestUtils.getMockMultipartFile();
    }

//    @Test
//    public void uploadFileTest() throws Exception {
//        String workingDir = System.getProperty("user.dir");
//        Path path = Paths.get(workingDir + "/uploads");
//        Mockito.when(fileStorageService.getBasePath()).thenReturn(path.toString());
//        Mockito.when(fileStorageService.extractTarGz(ArgumentMatchers.any(String.class),
//                ArgumentMatchers.any(String.class))).thenReturn("vTranscoder_vnfd/vTranscoder_vnfd.yaml");
//        Mockito.when(
//                fileStorageService.getContent(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class)))
//                .thenReturn("this is the content");
//        mockMvc.perform(fileUpload("/uploadFile").file(mockFile)).andDo(print()).andExpect(status().isOk());
//    }

    @Test
    public void exportFileTest() throws Exception {
        mockMvc.perform(
                post("/exportFile").param("file", "helloooowoasoda").param("fileName", "yaml/unnamed/unnamed.yaml")
                        .param("isArchive", "false").contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print()).andExpect(status().isOk());
    }

    public MockMvc getMockMVC(FileController fileController) {
        return MockMvcBuilders.standaloneSetup(fileController).build();
    }

}
