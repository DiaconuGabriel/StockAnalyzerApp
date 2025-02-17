StockAnalyzerApp 📈

StockAnalyzerApp is a web application for stock market analysis and predictions, built with Java, Spring Boot, and Vaadin. Google Gemini AI acts as a friendly stock advisor, analyzing data to identify trends and generate insights. Historical stock data is retrieved using Polygon API.

![Screenshot 2025-02-16 155138](https://github.com/user-attachments/assets/7aaa6570-0270-4a58-adc5-400ef8c6d189)

Features:

- AI-powered stock analysis with Gemini AI.
- Historical stock data from Polygon API (last 2 years).
- Select stocks, time ranges, and generate reports.
- Predict trends based on historical data.

Tech Stack:

- Java 17+.
- Spring Boot.
- Vaadin.
- Google Gemini AI.
- Polygon API (for stock data).
- REST API calls + JSON processing.

How it works?

You need to write your ticker and select the dates you want to get your analysis. After you select multiplier and range. That means how much data you want to analyze. Shorter ranges like minutes are best for smaller timeframes (e.g., a week). You can select 1 minute since that is not that much data for the AI to analyze. If you want to analyze longer timeframes (e.g., 1-2 years) you should use ranges like hours, days, or weeks due to the large data volume. Once the settings are selected, clicking "Generate Report" will fetch the data and provide an analysis. Errors may occur during API calls, but the system will notify you if there’s an issue.
