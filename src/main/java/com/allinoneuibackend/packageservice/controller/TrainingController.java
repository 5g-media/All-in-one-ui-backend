package com.allinoneuibackend.packageservice.controller;

import com.allinoneuibackend.packageservice.service.FileStorageService;
import com.allinoneuibackend.packageservice.model.TrainModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.env.Environment;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.util.Arrays;

import java.io.*;

@RestController
public class TrainingController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private Environment env;

    @PostMapping("/trainData")
    public ResponseEntity trainData(
        @RequestBody TrainModel trainModel
        ) {

        String alpha = trainModel.getAlpha();
        String trafficPattern = trainModel.getBgTrafficPattern();
        String actorLearningRate = trainModel.getActorLearningRate();
        String criticLEarningRate = trainModel.getCriticLearningRate();
        String link_capacity = trainModel.getLinkCapacity();
        String uploadFileName = trainModel.getUploadFileName();
        String modelType = trainModel.getModelType();
        String rewardFunction = trainModel.getRewardFunction();
        String parallelAgent = trainModel.getParallelAgent();
        String seedNumber = trainModel.getSeedNumber();
        String nameNnModel = trainModel.getNameNnModel();

        try{
             String trimmedFile =  "./uploads/" + uploadFileName.substring(0, uploadFileName.lastIndexOf('.'))+ "/";
             Runtime rtPython1 = Runtime.getRuntime();
             Process procStartPython = rtPython1.exec("./5gmedia-cno/cno/script2.sh" + " " + alpha + " " +trafficPattern + " " +actorLearningRate + " " + criticLEarningRate + " " + link_capacity + " " + nameNnModel + " " + rewardFunction + " " + parallelAgent + " " + "./cooked_traces/" + " " + seedNumber);
             BufferedReader r = new BufferedReader(new InputStreamReader(procStartPython.getInputStream()));
             String line2 = "";
              while (true) {
                try {
                    line2 = r.readLine();
                  } catch (IOException e) {
                   e.printStackTrace();
                   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
                  }
                  if (line2 == null) {
                    break;
                  }
                  System.out.println(line2);
               }
             
             
             return ResponseEntity.status(HttpStatus.OK).body("Training Started");
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
        
    }

    @GetMapping("/shutdown")
    public ResponseEntity shutdown() {
        try {
            Runtime rt = Runtime.getRuntime();
          
            Process procTenserboard = rt.exec("./5gmedia-cno/cno/script4.sh");
            BufferedReader stdInput1 = new BufferedReader(new
            InputStreamReader(procTenserboard.getInputStream()));
            String s1 = null;
            String sc1 = "";
            String numb1 = "";
            if ((s1 = stdInput1.readLine()) != null) {
                 Pattern p1 = Pattern.compile("\\d+");
                 Matcher m1 = p1.matcher(s1);
                 while(m1.find()) {
                      numb1 = numb1 + " " +  m1.group();
                  }
            }
        
            int index1 = numb1.lastIndexOf(" 1 ");
            sc1 = numb1.substring(index1+2,numb1.length());
            Runtime rt4 = Runtime.getRuntime();
            Process procStopTensorboard = rt4.exec("/bin/kill -15 " + sc1);
            
            
            Runtime rt2 = Runtime.getRuntime();
            Process procPython = rt2.exec("./5gmedia-cno/cno/script5.sh");
            BufferedReader stdInput2 = new BufferedReader(new
            InputStreamReader(procPython.getInputStream()));
            String s2 = null;
            String sc2 = "";
            String numb2 = "";
            if ((s2 = stdInput2.readLine()) != null) {
                 Pattern p2 = Pattern.compile("\\d+");
                 Matcher m2 = p2.matcher(s2);
                 while(m2.find()) {
                      numb2 = numb2 + " " +  m2.group();
                  }
            }

            int index2 = numb2.lastIndexOf(" 2 ");
            sc2 = numb2.substring(index2+2,numb2.length());
            System.out.println(sc2);
            Runtime rt5 = Runtime.getRuntime();
            Process procStopPython = rt5.exec("/bin/kill -15 " + sc2);
            
            
            Runtime rt3 = Runtime.getRuntime();
            Process procTrain = rt3.exec("./5gmedia-cno/cno/script6.sh");
            BufferedReader stdInput3 = new BufferedReader(new
            InputStreamReader(procTrain.getInputStream()));
            String s3 = null;
            String sc3 = "";
            String numb3 = "";
            for (String line; (line = stdInput3.readLine()) != null; numb3 = numb3 + " " + line );

            System.out.println("nwe" + numb3 );
        
            Runtime rt6 = Runtime.getRuntime();
            Process procStopTraining = rt6.exec("/bin/kill -15 " + numb3); 
            
            return ResponseEntity.status(HttpStatus.OK).body("Training Stopped with following processes" + numb1 + numb2 + numb3 );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }
    
    @GetMapping("/tensorboard")
    public ResponseEntity tensorboard() {
        try {
            Runtime rt = Runtime.getRuntime();
          
            Process procTenserboard = rt.exec("./5gmedia-cno/cno/script1.sh");
            BufferedReader stdInput1 = new BufferedReader(new
            InputStreamReader(procTenserboard.getInputStream()));
            
            return ResponseEntity.status(HttpStatus.OK).body("Training Started");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }
    
    

    @PostMapping("/uploadZipFile")
    public String uploadZipFile(
            @RequestParam("file") MultipartFile file) {

        fileStorageService.storeFile(file);
        String basePath = fileStorageService.getBasePath();

        fileStorageService.extractZip(file.getOriginalFilename(), basePath);
        return basePath + "/" + file.getOriginalFilename();
    }


    @PostMapping("/deployMape")
    public ResponseEntity deployMape(
        @RequestBody String ProductionIP){
        String latestFile = "";
        String pemFile = env.getProperty("pemFile");
        String deployURL = env.getProperty("deployURL");

        try {
            Runtime rtDeploy = Runtime.getRuntime();
            String homedir = System.getProperty("user.dir");    
            File modelFile = getTheNewestFile( homedir + "/5gmedia-cno/cno/results/", "ckpt.meta");
            latestFile = modelFile.getPath();
            Process procDeploy = rtDeploy.exec(homedir +"/5gmedia-cno/cno/deploy.sh" +  " " + homedir + pemFile + " " + latestFile + " " + deployURL);
            BufferedReader stdInputDeploy = new BufferedReader(new
            InputStreamReader(procDeploy.getInputStream()));
            
            return ResponseEntity.status(HttpStatus.OK).body(latestFile + " deployed to MAPE");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getLocalizedMessage());
        }
    }

    public File getTheNewestFile(String filePath, String ext) {
        System.out.println(filePath);
        File theNewestFile = null;
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*." + ext);
        File[] files = dir.listFiles(fileFilter);

        if (files.length > 0) {
            /** The newest file comes first **/
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            theNewestFile = files[0];
        }

  return theNewestFile;
}
}
