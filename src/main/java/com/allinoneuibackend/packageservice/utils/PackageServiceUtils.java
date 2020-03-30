package com.allinoneuibackend.packageservice.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackageServiceUtils {

    public static boolean createFolder(String path) {
        File newFolder = new File(path);
        boolean result = false;

        if (!newFolder.exists()) {
            System.out.println("creating directory: " + newFolder.getName());

            try {
                newFolder.mkdir();
                result = true;
            } catch (SecurityException se) {
                result = false;
            }
            if (result) {
                System.out.println("DIR created");
            }
        }

        return result;
    }

    public static boolean createFile(String path) {
        File newFile = new File(path);
        boolean result = false;

        if (!newFile.exists()) {
            System.out.println("creating file: " + newFile.getName());

            try {
                newFile.createNewFile();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
            }
            if (result) {
                System.out.println("file created");
            }
        }

        return result;
    }

    public static void copyResources(String basePath, String sourcePath) throws IOException {
        File source = new File(basePath + "/" + sourcePath);
        File dest = new File(basePath + "/archive");

        FileUtils.copyDirectoryToDirectory(source, dest);
    }

    public static void copyFiles(String basePath, String sourcePath, String destPath) throws IOException {
        File source = new File(basePath + "/" + sourcePath);
        File dest = new File(basePath + "/" + destPath);

        FileUtils.copyFileToDirectory(source, dest);
    }

    public static void cleanUploads(String basePath, String folderPath) {
        try {
            FileUtils.deleteDirectory(new File(basePath + "/" + folderPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToFile(String path, String content) {
        try {
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compress(String sourcePath, String destionation) {
        try {
            String sourceDir = sourcePath;
            String zipFile = destionation;

            try {
                FileOutputStream fout = new FileOutputStream(zipFile);
                ZipOutputStream zout = new ZipOutputStream(fout);

                File fileSource = new File(sourceDir);

                addDirectory(zout, sourceDir, fileSource);

                zout.close();

                System.out.println("Zip file has been created!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getRelativePath(String sourceDir, File file) {
        String path = file.getPath().substring(sourceDir.length());
        if (path.startsWith(File.pathSeparator)) {
            path = path.substring(1);
        }
        path = path.length() > 0 ? path.substring(1) : path;
        return path;
    }

    private static void addDirectory(ZipOutputStream zout, String sourceDir, File fileSource) throws IOException {
        if (fileSource.isDirectory()) {
            String path = getRelativePath(sourceDir, fileSource);
            if (path.trim().length() > 0) {
                ZipEntry ze = new ZipEntry(getRelativePath(sourceDir, fileSource) + "/");
                zout.putNextEntry(ze);
                zout.closeEntry();
            }

            File[] files = fileSource.listFiles();
            System.out.println("Adding directory " + fileSource.getName());
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    addDirectory(zout, sourceDir, files[i]);
                } else {

                    System.out.println("Adding file " + files[i].getName());

                    byte[] buffer = new byte[1024];

                    FileInputStream fin = new FileInputStream(files[i]);
                    zout.putNextEntry(new ZipEntry(getRelativePath(sourceDir, files[i])));

                    int length;

                    while ((length = fin.read(buffer)) > 0) {
                        zout.write(buffer, 0, length);
                    }
                    zout.closeEntry();
                    fin.close();
                }
            }
        }
    }

    public static String parsePath(String filePath, String schemaName) {
        String formattedFilePath = filePath.replaceAll("\\\\", "/");
        int secondIndex = filePath.indexOf("/", filePath.indexOf("/") + 1);
        String descriptorFileName = formattedFilePath.substring(filePath.indexOf("/") + 1, secondIndex);
        return schemaName.equals("tosca") ? descriptorFileName + ".zip" : descriptorFileName + ".tar.gz";
    }

    public static String getFolderNameFromPath(String filePath) {
        return filePath.replaceAll("\\\\", "/")
                .substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
    }

    public static void createTarFile(String sourceDir, String destination) {
        TarArchiveOutputStream tarOs = null;
        try {
            File destinationDir = new File(destination);
            // Using input name to create output name
            FileOutputStream fos = new FileOutputStream(destinationDir);
            GZIPOutputStream gos = new GZIPOutputStream(new BufferedOutputStream(fos));
            tarOs = new TarArchiveOutputStream(gos);
            addFilesToTarGZ(sourceDir, "", tarOs);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                tarOs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addFilesToTarGZ(String filePath, String parent, TarArchiveOutputStream tarArchive) throws IOException {
        File file = new File(filePath);
        // Create entry name relative to parent file path
        String entryName = parent + file.getName();
        // add tar ArchiveEntry
        tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Write file content to archive
            IOUtils.copy(bis, tarArchive);
            tarArchive.closeArchiveEntry();
            bis.close();
        } else if (file.isDirectory()) {
            // no need to copy any content since it is
            // a directory, just close the outputstream
            tarArchive.closeArchiveEntry();
            // for files in the directories
            for (File f : file.listFiles()) {
                // recursively call the method for all the subdirectories
                addFilesToTarGZ(f.getAbsolutePath(), entryName + File.separator, tarArchive);
            }
        }
    }

    public static void initPackage(String basePath, String descriptorFileName) throws IOException {
        String exportPackagePath = basePath + "/nonArchive";
        String currentPackagePath = exportPackagePath + "/" + descriptorFileName;

        PackageServiceUtils.createFolder(exportPackagePath);
        PackageServiceUtils.createFolder(currentPackagePath);

        String manifest = currentPackagePath + "/" + descriptorFileName + ".mf";
        String definitions = currentPackagePath + "/Definitions";
        String files = currentPackagePath + "/Files";
        String filesLicences = files + "/Licenses";
        String filesMonitoring = files + "/Monitoring";
        String filesScripts = files + "/Scripts";
        String filesTests = files + "/Tests";
        String toscaMetadata = currentPackagePath + "/TOSCA-Metadata";
        String toscaMeta = toscaMetadata + "/TOSCA.meta";

        String certFile = files + "/vcache.cert";
        String licence = filesLicences + "/LICENCE";
        String cloudInit = filesScripts + "/cloud-init.txt";
        String testInformation = filesTests + "/tests_information.txt";
        String monitoring = filesMonitoring + "/" + descriptorFileName + "_monitoring.yaml";

        PackageServiceUtils.createFolder(definitions);
        PackageServiceUtils.createFolder(files);
        PackageServiceUtils.createFolder(filesLicences);
        PackageServiceUtils.createFolder(filesMonitoring);
        PackageServiceUtils.createFolder(filesScripts);
        PackageServiceUtils.createFolder(filesTests);
        PackageServiceUtils.createFolder(toscaMetadata);

        PackageServiceUtils.createFile(manifest);
        PackageServiceUtils.createFile(certFile);
        PackageServiceUtils.createFile(licence);
        PackageServiceUtils.createFile(cloudInit);
        PackageServiceUtils.createFile(monitoring);
        FileUtils.copyFile(new File(System.getProperty("user.dir") + "/cloud-init.txt"), new File(cloudInit));
        PackageServiceUtils.createFile(testInformation);
        PackageServiceUtils.createFile(toscaMeta);

        createTOSCAMetaFile(descriptorFileName, toscaMeta);
        createManifestFile(descriptorFileName, manifest);
    }

    public static void createTOSCAMetaFile(String fileName, String filePath) {
        String fileNameWithoutTimestamp = fileName.substring(0, fileName.length() - 13);
        String toscaMeta = "TOSCA-Meta-File-Version: 1.0\n";
        toscaMeta += "CSAR-Version: 1.1\n";
        toscaMeta += "CreatedBy: SDK AIO Validator\n";
        toscaMeta += "Entry-Definitions: Definitions/" + fileName + ".yaml";

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(toscaMeta);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createManifestFile(String fileName, String filePath) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        String manifestContent = "metadata:\n";
        manifestContent += "   vnf_product_name: " + fileName + "\n";
        manifestContent += "   vnf_provider_id: NETAS\n";
        manifestContent += "   vnf_package_version: 1.1\n";
        manifestContent += "   vnf_release_data_time: " + dateFormat.format(date) + "\n\n";

        manifestContent += "monitoring:\n";
        manifestContent += "   main_monitoring_descriptor:\n";
        manifestContent += "      Source: Files/Monitoring/" + fileName + "_monitoring.yaml\n\n";

        manifestContent += "configuration:\n";
        manifestContent += "   cloud_init:\n";
        manifestContent += "      Source: Files/Scripts/cloud-init.txt";

        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(manifestContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileNameWithoutExtension(String originalFilename, int i) {
        return originalFilename.substring(0, originalFilename.length() - i);
    }

    public static String getSecondDirectory(String filePath) {
        String formattedFilePath = filePath.replaceAll("\\\\", "/");
        int secondIndex = filePath.indexOf("/", filePath.indexOf("/") + 1);
        return formattedFilePath.substring(filePath.indexOf("/") + 1, secondIndex);
    }

    public static String changeNsdYamlName(Path fileStorageLocation, String originalFileName, String fileNameWithoutExtension, long timestamp) {
        File file = new File(fileStorageLocation + "/" + originalFileName);

        File file2 = new File(fileStorageLocation + "/" + fileNameWithoutExtension + timestamp + ".yaml");

        if (file2.exists())
            return "";

        boolean success = file.renameTo(file2);

        if (!success) {
            // File was not successfully renamed
        }
        return file2.getName();
    }
}
