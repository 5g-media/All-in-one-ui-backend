package com.allinoneuibackend.packageservice.controller;

import com.allinoneuibackend.packageservice.service.FileStorageService;
import com.allinoneuibackend.packageservice.model.OnboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Collections;

@RestController
public class OnboardController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/onboardToPrivateCatalogue")
    public OnboardResponse onboardToPrivateCatalogue(@RequestParam("url") String url,
                                                     @RequestParam("exportFileName") String exportFileName) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", getUserFileResource(exportFileName));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        OnboardResponse onboardResponse = new OnboardResponse();
        onboardResponse.setFileName(exportFileName);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            onboardResponse.setMessage("Onboard Succesfull");
            onboardResponse.setSuccess(true);
            return onboardResponse;
        } catch (Exception e) {
            e.printStackTrace();
            onboardResponse.setMessage(e.getLocalizedMessage());
            onboardResponse.setSuccess(false);
            return onboardResponse;
        }
    }

    public Resource getUserFileResource(String exportFileName) {
        String filePath = fileStorageService.getBasePath() + "/" + exportFileName;
        File file = new File(filePath);

        return new FileSystemResource(file);
    }
}
