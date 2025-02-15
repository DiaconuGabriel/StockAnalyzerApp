
package com.example.stockanalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class TickerApi {
    private final RestTemplate restTemplate;

    @Value("${polygon.api.key}")
    private String apiKey;

    public TickerApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getTickerValues(String ticker, String multi, String range, String start_date, String end_date) throws Exception {
        String apiUrl = "https://api.polygon.io/v2/aggs/ticker/" + ticker
                + "/range/" + multi + "/" + range + "/" + start_date + "/" + end_date +
                "?adjusted=false&sort=asc&limit=50000&apiKey=" + apiKey;

        ObjectMapper objectMapper = new ObjectMapper();
        List<JsonNode> allResults = new ArrayList<>();
        String nextUrl = apiUrl;
        int responseCount = 0;

        try {
            while (nextUrl != null && responseCount<5) {
                // Fetch data from the current URL
                String response = restTemplate.getForObject(nextUrl, String.class);

                // Parse JSON response
                JsonNode rootNode = objectMapper.readTree(response);
                // Add the current results to the list
                JsonNode resultsNode = rootNode.get("results");
                if (resultsNode != null && resultsNode.isArray()) {
                    resultsNode.forEach(allResults::add);
                }

                // Check if "next_url" is present in the response
                if (rootNode.has("next_url")) {
                    nextUrl = rootNode.get("next_url").asText()+"&apiKey=" + apiKey;
                } else {
                    nextUrl = null; // No more pages
                }
                responseCount++;
            }

            // Combine all results into a single JSON (or return as needed)

        } catch (HttpClientErrorException e) {
            int responseCodeticker = e.getStatusCode().value();
            if (responseCodeticker == 429) {
                throw new Exception("Rate limit exceeded. Please try again later.");
            }
            if (responseCodeticker == 403) {
                throw new Exception("You are not entitled to this data.");
            }
            if (responseCodeticker == 400) {
                throw new Exception("Too Many Requests");
            }
        } catch (Exception ex) {
            throw new Exception("Error while fetching ticker values", ex);
        }
        return objectMapper.writeValueAsString(allResults);
    }
}
