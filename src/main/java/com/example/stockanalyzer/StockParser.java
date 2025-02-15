package com.example.stockanalyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.invoke.SerializedLambda;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
public class StockParser {
    public static String[] parse(String jsonString) throws Exception {
        // Creează un ObjectMapper pentru a parsa JSON-ul
         String firstDate = "";
         String lastDate = "";
         StringBuilder resultString = new StringBuilder();
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Parsează JSON-ul ca un array (direct din rădăcină)
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Verifică dacă rootNode este cu adevărat un array
            if (!rootNode.isArray() || rootNode.isEmpty()) {
                return new String[]{"", "", ""};
            }

            // Formatarea datei și inițializarea variabilelor
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Setează timezone-ul la UTC

            long timestamp = 0;
            int first = 0;

            // Iterează prin fiecare element din array
            for (JsonNode result : rootNode) {

                // Extrage datele (sau folosește valori default dacă lipsesc)
                timestamp = result.has("t") ? result.path("t").asLong() : 0;
                if (first == 0) {
                    firstDate = timestamp != 0 ? dateFormat.format(new Date(timestamp)) : "Unknown Date";
                    first = 1;
                }
                double v = result.has("v") ? result.path("v").asDouble() : 0.0;
                double vw = result.has("vw") ? result.path("vw").asDouble() : 0.0;
                double o = result.has("o") ? result.path("o").asDouble() : 0.0;
                double c = result.has("c") ? result.path("c").asDouble() : 0.0;
                double h = result.has("h") ? result.path("h").asDouble() : 0.0;
                double l = result.has("l") ? result.path("l").asDouble() : 0.0;

                // Formatează data și creează rezultatul
                String formattedDate = timestamp != 0 ? dateFormat.format(new Date(timestamp)) : "Unknown Date";
                resultString.append(String.format(
                        "%s : v: %.2f, vw: %.2f, o: %.2f, c: %.2f, h: %.2f, l: %.2f; ",
                        formattedDate, v, vw, o, c, h, l
                ));
            }
            lastDate = timestamp != 0 ? dateFormat.format(new Date(timestamp)) : "Unknown Date";
        } catch (Exception e) {
            throw new Exception("Error parsing JSON: " + e.getMessage());
        }
        // Returnează rezultatele concatenate și ultimul timestamp
        return new String[]{
                resultString.toString().trim(), // Toate rezultatele concatenate
                firstDate, // Prima dată procesată
                lastDate // Ultima dată procesată
        };
    }
}