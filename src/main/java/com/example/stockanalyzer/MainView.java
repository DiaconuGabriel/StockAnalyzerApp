package com.example.stockanalyzer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import javax.sound.midi.SysexMessage;

@Route("")
@CssImport("./style.css")

public class MainView extends VerticalLayout {

    public MainView(TickerApi tickerApi, GeminiApi geminiApi) {

        H1 title = new H1("Stock Analyzer");
        title.addClassName("title");
        add(title);
        setHorizontalComponentAlignment(Alignment.CENTER, title);

        TextField textField = new TextField();
        textField.addClassName("text-field-placeholder");
        textField.setPlaceholder("Enter ticker name");
        add(textField);
        setHorizontalComponentAlignment(Alignment.CENTER, textField);

        Paragraph outputParagraph = new Paragraph();
        outputParagraph.addClassName("output-paragraph");
        setHorizontalComponentAlignment(Alignment.CENTER, outputParagraph);

        Button button = new Button("Genereate Report",
            e ->
            {
                String text = textField.getValue();
                outputParagraph.setText(""); // Reset the output paragraph
                if(!text.isEmpty()){
                    String tickerValues = null;
                    String geminiResponse = null;
                    try {
//
                        tickerValues = StockParser.parse(tickerApi.getTickerValues(text));

                        geminiResponse = geminiApi.generateContent(tickerValues);

                        typeText(outputParagraph, GeminiResponseParser.parseResponse(geminiResponse).split(" "));
                    } catch (Exception ex){
                        System.out.println(ex.getMessage());
                        typeText(outputParagraph, ex.getMessage().split(" "));
                    }
                }else{
                    typeText(outputParagraph, "Insert a ticker bro.".split(" "));
                }
            });
        button.addClassName("button");
        setHorizontalComponentAlignment(Alignment.CENTER, button);
        add(button);
        add(outputParagraph);
    }

    private void typeText(Paragraph paragraph, String[] words) {
        int delay = 25; // Fixed delay between each word (in milliseconds)
        for (int i = 0; i < words.length; i++) {
            String word = words[i] + " ";
            String script = String.format(
                    "setTimeout(function() { " +
                            "var para = document.querySelector('.output-paragraph'); " +
                            "para.innerHTML += '%s'; " +
                            "para.scrollTop = para.scrollHeight; }, %d);",
                    word, i * delay
            );
            UI.getCurrent().getPage().executeJs(script);
        }
    }


}