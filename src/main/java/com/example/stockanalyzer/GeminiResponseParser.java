package com.example.stockanalyzer;

import org.json.JSONArray;
import org.json.JSONObject;

public class GeminiResponseParser {

    public static String parseResponse(String jsonResponse) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray candidates = jsonObject.getJSONArray("candidates");
        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
        JSONArray parts = content.getJSONArray("parts");
        StringBuilder parsedText = new StringBuilder();

        for (int i = 0; i <= parts.length(); i++) {
            parsedText.append(parts.getJSONObject(i).getString("text"));
        }

        return parsedText.toString();
    }
}
