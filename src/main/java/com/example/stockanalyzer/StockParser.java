package com.example.stockanalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
public class StockParser {
    public static String parse(String jsonString) throws Exception {
        // Creează un ObjectMapper pentru a parsa JSON-ul
        ObjectMapper objectMapper = new ObjectMapper();

        // Parsează JSON-ul ca un obiect JsonNode
        JsonNode rootNode = objectMapper.readTree(jsonString);
        JsonNode resultsNode = rootNode.path("results");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Setează timezone-ul la UTC

        StringBuilder resultString = new StringBuilder();

        // Iterează prin fiecare element din "results"
        for (JsonNode result : resultsNode) {
            long timestamp = result.path("t").asLong();
            double v = result.path("v").asLong();
            double vw = result.path("vw").asDouble();
            double o = result.path("o").asDouble();
            double c = result.path("c").asDouble();
            double h = result.path("h").asDouble();
            double l = result.path("l").asDouble();

            // Formatează data și creează stringul
            String formattedDate = dateFormat.format(new Date(timestamp));
            resultString.append(String.format(
                    "%s : v: %.2f, vw: %.2f, o: %.2f, c: %.2f, h: %.2f, l: %.2f; ",
                    formattedDate, v, vw, o, c, h, l
            ));
        }

        return resultString.toString().trim();
    }
}