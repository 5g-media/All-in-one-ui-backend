package com.allinoneuibackend.packageservice.service;

import com.allinoneuibackend.packageservice.model.ExportFileModel;
import com.allinoneuibackend.packageservice.model.ExportFileResponse;
import com.allinoneuibackend.packageservice.model.UploadFileModel;
import com.allinoneuibackend.packageservice.model.UploadFileResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    void storeFile(MultipartFile file);

    Resource loadFileAsResource(String fileName);

    UploadFileResponse extractCsar(String storageLocation, String basePath, long timeStamp) throws IOException;

    UploadFileResponse extractTarGz(String storageLocation, String basePath, long timestamp) throws IOException;

    UploadFileResponse getContent(UploadFileResponse uploadFileResponse);
    
    String extractZip(String storageLocation, String basePath);

    void compress(String fileName, String schemaName, Boolean isArchive) throws IOException;

    void saveAsYaml(String content, String filePath) throws IOException;

    String getBasePath();

    UploadFileResponse fileUploadOperations(UploadFileModel uploadFileModel) throws IOException;

    ExportFileResponse fileExportOperations(ExportFileModel exportFileModel);
}
