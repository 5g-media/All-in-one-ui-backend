package com.allinoneuibackend.packageservice.controller;

import com.allinoneuibackend.packageservice.model.UploadFileModel;
import com.allinoneuibackend.packageservice.model.UploadFileResponse;
import com.allinoneuibackend.packageservice.service.FileStorageService;
import com.allinoneuibackend.packageservice.model.ExportFileModel;
import com.allinoneuibackend.packageservice.model.ExportFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public ResponseEntity uploadFile(@ModelAttribute UploadFileModel uploadFileModel) {

        UploadFileResponse uploadFileResponse = new UploadFileResponse();

        try {
            fileStorageService.storeFile(uploadFileModel.getFile());
            uploadFileResponse = fileStorageService.fileUploadOperations(uploadFileModel);
            uploadFileResponse = fileStorageService.getContent(uploadFileResponse);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(uploadFileResponse);
        }

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(uploadFileResponse);
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }


    @PostMapping("/exportFile")
    public ResponseEntity exportFile(@ModelAttribute ExportFileModel exportFileModel) {

        ExportFileResponse exportFileResponse = new ExportFileResponse();

        try {
            exportFileResponse = fileStorageService.fileExportOperations(exportFileModel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(exportFileResponse);
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(exportFileResponse.getFileName()).toUriString();

        exportFileResponse.setFileDownloadUri(fileDownloadUri);

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body(exportFileResponse);
    }
}
