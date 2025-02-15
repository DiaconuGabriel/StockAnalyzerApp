package com.example.stockanalyzer;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiApi {

    @Value("${gemini.api.key}")
    private String API_KEY;

    public String generateContent(String inputText) throws Exception {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-thinking-exp-01-21:generateContent?key=" + API_KEY;
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Sanitize inputText to avoid breaking JSON format
        inputText = inputText.replace("\"", "\\\"");

        // Build JSON payload
        String jsonInputString = String.format(
                """
                        {
                          "contents": [
                            {
                              "role": "user",
                              "parts": [
                                { "text": "%s" }
                              ]
                            }
                          ],
                          "generationConfig": {
                            "temperature": 0.65,
                            "topK": 64,
                            "topP": 0.95,
                            "maxOutputTokens": 100000,
                            "responseMimeType": "text/plain"
                          }
                        }""",
                "You are a friendly bro stock advisor who analyzes stock historic data and predicts the price movement for the stock by saying if it is worth to buy or sell. Your respone is 250 words maximum."+ inputText);

        // Send the request
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        } catch (Exception e) {
            throw new Exception(e);
        }

        // Get the response
        int responseCode = connection.getResponseCode();
        BufferedReader br;
        if (responseCode == 429) {
            throw new Exception("Rate limit exceeded. Please try again later.");
        } else if (responseCode == 503) {
            throw new Exception("Service is unavailable. Please try again later.");
        } else if (responseCode >= 200 && responseCode < 300) {
            br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        // Parse and return the response
        JSONObject jsonResponse = new JSONObject(response.toString());
        return jsonResponse.toString();  // Return the JSON response
    }
}
