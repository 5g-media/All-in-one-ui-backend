package com.allinoneuibackend.packageservice;

import com.allinoneuibackend.packageservice.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({
        FileStorageProperties.class
})
@SpringBootApplication
public class PackageService {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PackageService.class);
        app.addListeners(new ShutdownListener());
        app.run(args);
    }
}
