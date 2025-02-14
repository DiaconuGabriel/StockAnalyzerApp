package com.example.stockanalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class TickerApi {
    private final RestTemplate restTemplate;

    @Value("${polygon.api.key}")
    private String apiKey;

    public TickerApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTickerValues(String ticker, String multi, String range, String start_date, String end_date) {
        String apiUrl = "https://api.polygon.io/v2/aggs/ticker/" + ticker
                + "/range/"+ multi + "/" + range + "/" + start_date + "/" + end_date + "?adjusted=true&sort=asc&apiKey=" + apiKey;
        try {
            return restTemplate.getForObject(apiUrl, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403) {
                return "You are not entitled to this data. Please upgrade your plan at https://polygon.io/pricing";
            }
            System.out.println(e.getStatusCode().value());
            throw e;
        }
    }
}
