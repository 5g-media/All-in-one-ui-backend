package com.allinoneuibackend.packageservice.controller;

import com.allinoneuibackend.packageservice.model.Config;
import com.allinoneuibackend.packageservice.utils.PackageServiceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
public class ConfigController {

    @PostMapping("/setConfig")
    public Config setConfiguration(@RequestBody String conf) {

        String cwd = System.getProperty("user.dir");
        String filePath = cwd + "/config.json";
        try {
            PackageServiceUtils.createFile(filePath);
            PackageServiceUtils.writeToFile(filePath, conf);
            return new Config("Saved", true);
        } catch (Exception e) {
            return new Config(e.getLocalizedMessage(), false);
        }

    }

    @GetMapping("/getConfig")
    public Config getConfiguration() {
        String cwd = System.getProperty("user.dir");
        String filePath = cwd + "/config.json";
        File file = new File(filePath);

        String st;
        String content = "";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            while ((st = br.readLine()) != null) {
                content += st + System.getProperty("line.separator");
            }
        } catch (IOException e) {
            return new Config(e.getLocalizedMessage(), false);
        }

        return new Config(content, true);
    }
}
