package com.urlshortner.springboot.controller;

import com.urlshortner.springboot.models.UrlMapping;
import com.urlshortner.springboot.service.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RedirectController {

    private UrlMappingService urlMappingService;

    @GetMapping({"/{shortUrl}"})
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl) {

        UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl);
        if (urlMapping != null){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", urlMapping.getOriginalUrl());
            return ResponseEntity.status(302).headers(headers).build();


        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
}
