package com.allinoneuibackend.packageservice.service;

import com.allinoneuibackend.packageservice.utils.PackageServiceUtils;
import com.allinoneuibackend.packageservice.exceptions.FileStorageException;
import com.allinoneuibackend.packageservice.exceptions.MyFileNotFoundException;
import com.allinoneuibackend.packageservice.config.FileStorageProperties;
import com.allinoneuibackend.packageservice.model.ExportFileModel;
import com.allinoneuibackend.packageservice.model.ExportFileResponse;
import com.allinoneuibackend.packageservice.model.UploadFileModel;
import com.allinoneuibackend.packageservice.model.UploadFileResponse;

import org.rauschig.jarchivelib.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }
    }

    public void storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    @Override
    public UploadFileResponse fileUploadOperations(UploadFileModel uploadFileModel) throws IOException {
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        String originalFileName = uploadFileModel.getFile().getOriginalFilename();
        long timestamp = new Timestamp(System.currentTimeMillis()).getTime();
        if (uploadFileModel.isArchive()) {
            if (uploadFileModel.getSchemaName().equals("tosca")) {                                   // upload tosca zip
                uploadFileResponse = extractCsar(uploadFileModel.getFile().getOriginalFilename(), this.fileStorageLocation.toString(), timestamp);
                uploadFileResponse.setFolderPath(PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 4) + timestamp);
            } else {                                                                                // upload osm tar.gz
                uploadFileResponse = extractTarGz(originalFileName, this.fileStorageLocation.toString(), timestamp);
                uploadFileResponse.setFolderPath(PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 7) + timestamp);
            }
            PackageServiceUtils.copyResources(this.fileStorageLocation.toString(), uploadFileResponse.getFolderPath());
            uploadFileResponse.setFolderPath("archive/" + uploadFileResponse.getFolderPath());
        } else {
            if (uploadFileModel.getDescriptorType().equals("vnfd")) {
                if (uploadFileModel.getSchemaName().equals("osm")) {                                     // upload osm yaml
                    String folder = PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 5);
                    uploadFileResponse.setFilePath(originalFileName);
                    uploadFileResponse.setError(false);
                    uploadFileResponse.setFolderPath("nonArchive/" + folder + timestamp + "/" + folder);
                } else if (uploadFileModel.getSchemaName().equals("tosca")) {                            // upload tosca yaml
                    String folder = PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 5);
                    uploadFileResponse.setFilePath(originalFileName);
                    uploadFileResponse.setError(false);
                    uploadFileResponse.setFolderPath("nonArchive/" + folder + timestamp + "/Definitions");
                }
            } else {
                if (uploadFileModel.getSchemaName().equals("osm")) {
                    String folder = PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 5);

                    uploadFileResponse.setFilePath(originalFileName);
                    uploadFileResponse.setError(false);
                    uploadFileResponse.setFolderPath("nonArchive/" + folder + timestamp + "/" + folder);
                } else {
                    String folder = PackageServiceUtils.getFileNameWithoutExtension(originalFileName, 5);
                    String changedFileName = PackageServiceUtils.changeNsdYamlName(this.fileStorageLocation, originalFileName, folder, timestamp);
                    uploadFileResponse.setFilePath(changedFileName);
                    uploadFileResponse.setError(false);
                    uploadFileResponse.setFolderPath("nonArchive/" + folder + timestamp + "/" + folder);
                }

            }
            PackageServiceUtils.copyFiles(this.fileStorageLocation.toString(), uploadFileResponse.getFilePath(), uploadFileResponse.getFolderPath());
        }
        return uploadFileResponse;
    }

    @Override
    public ExportFileResponse fileExportOperations(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        if (exportFileModel.getArchive() == false && exportFileModel.getVnfd() == true) {
            if (exportFileModel.getFilePath().equals("")) {
                if (exportFileModel.getSchemaName().equals("osm")) {                                     // osm vnf from scratch
                    exportFileResponse = osmVnfAndNsdFromScratch(exportFileModel);
                } else {                                                            // tosca vnf from scratch
                    exportFileResponse = toscaVnfFromScratch(exportFileModel);
                }
            } else {
                if (exportFileModel.getSchemaName().equals("osm")) {                                     // osm vnf from yaml
                    exportFileResponse = osmVnfAndNsdFromUpload(exportFileModel);
                } else {                                                            // tosca vnf from yaml
                    exportFileResponse = toscaVnfFromYaml(exportFileModel);
                }
            }

        } else if (exportFileModel.getArchive() == false && exportFileModel.getVnfd() == false) {
            if (exportFileModel.getFilePath().equals("")) {
                if (exportFileModel.getSchemaName().equals("osm")) {                                   // osm nsd from scratch
                    exportFileResponse = osmVnfAndNsdFromScratch(exportFileModel);
                } else {                                                          // tosca nsd from scratch
                    exportFileResponse = toscaNsdFromScratch(exportFileModel);
                }
            } else {
                if (exportFileModel.getSchemaName().equals("osm")) {                                   // osm nsd from yaml
                    exportFileResponse = osmVnfAndNsdFromUpload(exportFileModel);
                } else {                                                          // tosca nsd from yaml
                    exportFileResponse = toscaNsdFromYaml(exportFileModel);
                }
            }

        } else if (exportFileModel.getArchive() == true) {
            if (exportFileModel.getSchemaName().equals("osm")) {                                     // osm vnf and nsd from archive
                exportFileResponse = osmVnfAndNsdFromUpload(exportFileModel);
            } else {                                                            // tosca vnf from archive
                exportFileResponse = toscaVnfFromArchive(exportFileModel);
            }
        }

        return exportFileResponse;
    }

    public void saveAndCompress(String content, String filePath, String folderToArchive, String schemaName, Boolean isArchive) throws IOException {
        saveAsYaml(content, filePath);
        compress(folderToArchive, schemaName, isArchive);
    }

    public void initiatePackagesFromScratch(String basePath, String filePath) {
        String[] filePathArr = filePath.split("/");
        String path = "";
        for (int i = 0; i < filePathArr.length - 1; i++) {
            path += "/" + filePathArr[i];
            PackageServiceUtils.createFolder(basePath + path);
        }
        PackageServiceUtils.createFile(basePath + "/" + filePath);
    }

    public String generateFilPath(String schemaName, Boolean isVnfd) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        if (schemaName.equals("osm") && isVnfd == true) {
            return "nonArchive/osmVnfdDescriptor" + timestamp.getTime() + "/osmVnfdDescriptor/osmVnfdDescriptor" + timestamp.getTime() + ".yaml";
        } else if (schemaName.equals("osm") && isVnfd == false) {
            return "nonArchive/osmNsdDescriptor" + timestamp.getTime() + "/osmNsdDescriptor/osmNsdDescriptor" + timestamp.getTime() + ".yaml";
        } else if (schemaName.equals("tosca") && isVnfd == true) {
            return "nonArchive/toscaVnfdDescriptor" + timestamp.getTime() + "/Definitions/toscaVnfdDescriptor" + timestamp.getTime() + ".yaml";
        } else {
            return "nonArchive/toscaNsdDescriptor" + timestamp.getTime() + "/toscaNsdDescriptor" + timestamp.getTime() + ".yaml";
        }
    }

    public ExportFileResponse toscaNsdFromYaml(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String exportFileName = exportFileModel.getFilePath().substring(exportFileModel.getFilePath().lastIndexOf("/") + 1);

        try {
            saveAsYaml(exportFileModel.getContent(), exportFileModel.getFilePath());
            PackageServiceUtils.copyFiles(this.fileStorageLocation.toString(), exportFileModel.getFilePath(), "");
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public ExportFileResponse toscaVnfFromYaml(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String folderToZip = PackageServiceUtils.getSecondDirectory(exportFileModel.getFilePath());
        String exportFileName = PackageServiceUtils.parsePath(exportFileModel.getFilePath(), exportFileModel.getSchemaName());

        try {
            PackageServiceUtils.initPackage(this.fileStorageLocation.toString(), folderToZip);
            saveAndCompress(exportFileModel.getContent(), exportFileModel.getFilePath(), folderToZip, exportFileModel.getSchemaName(), exportFileModel.getArchive());
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public ExportFileResponse osmVnfAndNsdFromScratch(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String filePath = generateFilPath(exportFileModel.getSchemaName(), exportFileModel.getVnfd());
        String folderToArchive = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf("."));
        String exportFileName = PackageServiceUtils.parsePath(filePath, exportFileModel.getSchemaName());

        try {
            initiatePackagesFromScratch(this.fileStorageLocation.toString(), filePath);
            saveAndCompress(exportFileModel.getContent(), filePath, folderToArchive, exportFileModel.getSchemaName(), false);
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

//    public ExportFileResponse osmNsdFromScratch(String basePath, String schemaName, String content) {
//        ExportFileResponse exportFileResponse = new ExportFileResponse();
//        String filePath = generateFilPath(schemaName, false);
//        String folderToArchive = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.indexOf("."));
//        String exportFileName = PackageServiceUtils.parsePath(filePath, schemaName);
//
//        try {
//            initiatePackagesFromScratch(basePath, filePath);
//            saveAndCompress(content, filePath, folderToArchive, schemaName, false);
//        } catch (IOException e) {
//            e.getLocalizedMessage();
//            exportFileResponse.setFileName("");
//            exportFileResponse.setError(true);
//        }
//
//        exportFileResponse.setFileName(exportFileName);
//        exportFileResponse.setError(false);
//
//        return exportFileResponse;
//    }

    public ExportFileResponse toscaVnfFromScratch(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String newFilePath = generateFilPath(exportFileModel.getSchemaName(), true);
        String folderToZip = PackageServiceUtils.getFolderNameFromPath(newFilePath);
        String exportFileName = PackageServiceUtils.parsePath(newFilePath, exportFileModel.getSchemaName());

        try {
            PackageServiceUtils.initPackage(this.fileStorageLocation.toString(), folderToZip);
            saveAndCompress(exportFileModel.getContent(), newFilePath, folderToZip, exportFileModel.getSchemaName(), false);
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }
        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public ExportFileResponse toscaNsdFromScratch(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String newFilePath = generateFilPath(exportFileModel.getSchemaName(), false);
        String exportFileName = newFilePath.substring(newFilePath.lastIndexOf("/") + 1);

        try {
            initiatePackagesFromScratch(this.fileStorageLocation.toString(), newFilePath);
            saveAsYaml(exportFileModel.getContent(), newFilePath);
            PackageServiceUtils.copyFiles(this.fileStorageLocation.toString(), newFilePath, "");
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public ExportFileResponse osmVnfAndNsdFromUpload(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String folderToZip = PackageServiceUtils.getSecondDirectory(exportFileModel.getFilePath());
        String exportFileName = PackageServiceUtils.parsePath(exportFileModel.getFilePath(), exportFileModel.getSchemaName());

        try {
            saveAndCompress(exportFileModel.getContent(), exportFileModel.getFilePath(), folderToZip, exportFileModel.getSchemaName(), exportFileModel.getArchive());
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public ExportFileResponse toscaVnfFromArchive(ExportFileModel exportFileModel) {
        ExportFileResponse exportFileResponse = new ExportFileResponse();
        String folderToZip = PackageServiceUtils.getSecondDirectory(exportFileModel.getFilePath());
        String exportFileName = PackageServiceUtils.parsePath(exportFileModel.getFilePath(), exportFileModel.getSchemaName());

        try {
            saveAndCompress(exportFileModel.getContent(), exportFileModel.getFilePath(), folderToZip, exportFileModel.getSchemaName(), exportFileModel.getArchive());
        } catch (IOException e) {
            e.getLocalizedMessage();
            exportFileResponse.setFileName("");
            exportFileResponse.setError(true);
        }

        exportFileResponse.setFileName(exportFileName);
        exportFileResponse.setError(false);

        return exportFileResponse;
    }

    public UploadFileResponse extractCsar(String fileName, String basePath, long timeStamp) {

        String extractPath = "";

//        PackageServiceUtils.cleanUploads(basePath, "archive");

        File archive = new File(basePath + "/" + fileName);

        Archiver archiver = ArchiverFactory.createArchiver(archive);

        try {
            ArchiveStream stream = archiver.stream(archive);
            ArchiveEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                extractPath = entry.getName().contains(".yaml") && entry.getName().contains("Definitions") ? entry.getName() : extractPath;
                entry.extract(new File(basePath + "/" + fileName.substring(0, fileName.indexOf(".")) + timeStamp));
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new UploadFileResponse(extractPath, true);
        }

        return new UploadFileResponse(extractPath, false);
    }
    
    //Fatih
    public String extractZip(String fileName, String basePath) {

        String extractZipPath = "";

        File archiveZip = new File(basePath + "/" + fileName);

        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.ZIP);

        try {
            archiver.extract(archiveZip, new File(basePath + "/" + fileName.substring(0,fileName.lastIndexOf("."))));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return extractZipPath;
    }

    public UploadFileResponse extractTarGz(String fileName, String basePath, long timestamp) {

        String extractPath = "";

//        PackageServiceUtils.cleanUploads(basePath, "archive");

        File archive = new File(basePath + "/" + fileName);

        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);

        try {
            ArchiveStream stream = archiver.stream(archive);
            ArchiveEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                extractPath = entry.getName().contains(".yaml") ? entry.getName() : extractPath;
                entry.extract(new File(basePath + "/" + fileName.substring(0, fileName.indexOf(".")) + timestamp));
            }
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return new UploadFileResponse(extractPath, true);
        }

        return new UploadFileResponse(extractPath, false);
    }

    public UploadFileResponse getContent(UploadFileResponse uploadFileResponse) {

        File editorFile = new File(this.fileStorageLocation.toString() + "/" + uploadFileResponse.getFolderPath() + "/" + uploadFileResponse.getFilePath());
        String st;
        String content = "";
        BufferedReader br;

        try {
            br = new BufferedReader(new FileReader(editorFile));
            while ((st = br.readLine()) != null) {
                content += st + System.getProperty("line.separator");
            }
        } catch (IOException e) {
            e.printStackTrace();
            uploadFileResponse.setFileContent("");
            uploadFileResponse.setError(true);
            return uploadFileResponse;
        }

        uploadFileResponse.setFileContent(content);
        uploadFileResponse.setError(false);
        return uploadFileResponse;
    }

    public void compress(String fileName, String schemaName, Boolean isArchive) {
        String archivePath = isArchive ? "/archive/" : "/nonArchive/";
        String extension = schemaName.equals("tosca") ? ".zip" : ".tar.gz";
        String destination = this.fileStorageLocation.toString() + "/" + fileName + extension;
        String source = this.fileStorageLocation.toString() + archivePath + fileName;

        if (schemaName.equals("tosca")) {
            PackageServiceUtils.compress(source, destination);
        } else {
            String folderNameWithoutTimestamp = fileName.substring(0, fileName.length() - 13);
            PackageServiceUtils.createTarFile(source + "/" + folderNameWithoutTimestamp, destination);
        }
    }

    public void saveAsYaml(String content, String filePath) throws IOException {
        String basePath = this.fileStorageLocation.toString();
        String yamlPath = basePath + "/" + filePath;

        FileWriter fileWriter = new FileWriter(yamlPath);
        fileWriter.write(content);
        fileWriter.flush();
        fileWriter.close();
    }

    public String getBasePath() {
        return this.fileStorageLocation.toString();
    }
}
