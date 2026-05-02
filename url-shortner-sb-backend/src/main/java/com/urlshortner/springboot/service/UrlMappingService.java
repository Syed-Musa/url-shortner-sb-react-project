package com.urlshortner.springboot.service;

import com.urlshortner.springboot.dtos.ClickEventDTO;
import com.urlshortner.springboot.dtos.UrlMappingDTO;
import com.urlshortner.springboot.models.ClickEvent;
import com.urlshortner.springboot.models.UrlMapping;
import com.urlshortner.springboot.models.User;
import com.urlshortner.springboot.repository.ClickEventRepository;
import com.urlshortner.springboot.repository.UrlMappingRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UrlMappingService {

    private final ClickEventRepository clickEventRepository;
    private UrlMappingRepository urlMappingRepository;

    public UrlMappingDTO createShortUrl(String originalUrl, User user) {

        String shortUrl = generateShorturl();
        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setOriginalUrl(originalUrl);
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setUser(user);
        urlMapping.setCreatedDate(LocalDateTime.now());
        UrlMapping savedUrlMapping = urlMappingRepository.save(urlMapping);
        return convertToDto(savedUrlMapping);
    }


    private  UrlMappingDTO convertToDto(UrlMapping urlMapping){

        UrlMappingDTO urlMappingDTO = new UrlMappingDTO();

        urlMappingDTO.setId(urlMapping.getId());
        urlMappingDTO.setOriginalUrl(urlMapping.getOriginalUrl());
        urlMappingDTO.setShortUrl(urlMapping.getShortUrl());
        urlMappingDTO.setClickCount(urlMapping.getClickCount());
        urlMappingDTO.setCreatedDate(urlMapping.getCreatedDate());
        urlMappingDTO.setUsername(urlMapping.getUser().getUsername());
        return urlMappingDTO;

    }


    private String generateShorturl() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();
        StringBuilder shorturl = new StringBuilder(8);

        for (int i = 0; i < 8 ; i++){
            shorturl.append(characters.charAt(random.nextInt(characters.length())));
        }
return shorturl.toString();

    }


    public List<UrlMappingDTO> getUrlsByUser(User user) {

        return urlMappingRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<ClickEventDTO> getClickEventsByDate(String shortUrl, LocalDateTime start, LocalDateTime end) {

        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);

        if (urlMapping != null) {
            return clickEventRepository
                    .findByUrlMappingAndClickDateBetween(urlMapping, start, end)
                    .stream()
                    .collect(Collectors.groupingBy(
                            click -> click.getClickDate().toLocalDate(),
                            Collectors.counting()
                    ))
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        ClickEventDTO dto = new ClickEventDTO();
                        dto.setClickDate(entry.getKey());
                        dto.setCount(entry.getValue());
                        return dto;
                    })
                    .toList();
        }

        return Collections.emptyList(); // better than returning null
    }

    public Map<LocalDate, Long> getTotalClicksByUserAndDate(User user, LocalDate start , LocalDate end) {
        List<UrlMapping> urlMappings = urlMappingRepository.findByUser(user);
        List<ClickEvent> clickEvents =  clickEventRepository.findByUrlMappingInAndClickDateBetween(urlMappings, start.atStartOfDay() , end.plusDays(1).atStartOfDay());
        return clickEvents.stream()
                .collect(Collectors.groupingBy(
                        click -> click.getClickDate().toLocalDate(),
                        Collectors.counting()
                ));


    }

    public UrlMapping getOriginalUrl(String shortUrl) {

        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if (urlMapping != null){
            urlMapping.setClickCount(urlMapping.getClickCount() + 1);
            urlMappingRepository.save(urlMapping);
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setClickDate(LocalDateTime.now());
            clickEvent.setUrlMapping(urlMapping);

            clickEventRepository.save(clickEvent);
        }
        return urlMapping;
    }
}
