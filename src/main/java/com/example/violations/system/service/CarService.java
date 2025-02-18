package com.example.violations.system.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CarService {
    private final RestTemplate restTemplate;

    public CarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Object getCarInfoByPlateAsObject(String plateNumber) {
        String url = "http://localhost:8081/api/cars/plate/" + plateNumber;
        return restTemplate.getForObject(url, Object.class);
    }
}

