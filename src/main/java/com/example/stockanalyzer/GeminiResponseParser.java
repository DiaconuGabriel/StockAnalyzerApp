package com.example.stockanalyzer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class GeminiResponseParser {

    public static String parseResponse(String jsonResponse) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Check if the response contains an error
        if (jsonObject.has("error")) {
            JSONObject errorObject = jsonObject.getJSONObject("error");
            String errorMessage = errorObject.getString("message");
            throw new Exception("No Response bro. To hard to analyze all of that data!");
        }

        // Check if the response contains "candidates"
        if (jsonObject.has("candidates") && jsonObject.getJSONArray("candidates").length() > 0) {
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);
            if (!firstCandidate.has("content")) {
                throw new Exception("Missing 'content' in the first candidate.");
            }

            JSONObject content = firstCandidate.getJSONObject("content");
            if (!content.has("parts")) {
                throw new Exception("Missing 'parts' array in 'content'.");
            }

            JSONArray parts = content.getJSONArray("parts");
            StringBuilder parsedText = new StringBuilder();

            for (int i = 0; i < parts.length(); i++) {
                parsedText.append(parts.getJSONObject(i).getString("text"));
            }

            return parsedText.toString();
        }

        // If no candidates were found
        throw new Exception("No Response bro. To hard to analyze all of that data!");
    }
}
