package com.example.stockanalyzer;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import javax.sound.midi.SysexMessage;
import java.time.LocalDate;

@Route("")
@CssImport("./style.css")

public class MainView extends VerticalLayout {

    public MainView(TickerApi tickerApi, GeminiApi geminiApi) {

        //title
        H1 title = new H1("Stock Analyzer");
        title.addClassName("title");
        add(title);
        setHorizontalComponentAlignment(Alignment.CENTER, title);

        //stock picker
        TextField textField = new TextField();
        textField.addClassName("text-field-placeholder");
        textField.setPlaceholder("Enter ticker name");
        add(textField);
        setHorizontalComponentAlignment(Alignment.CENTER, textField);

        //date picker
        DatePicker datePicker_start = new DatePicker("Start date");
        DatePicker datePicker_end = new DatePicker("End date");

        datePicker_start.setMax(LocalDate.now().minusDays(1));
        datePicker_end.setMax(LocalDate.now().minusDays(1));

        datePicker_start.addValueChangeListener(e -> datePicker_end.setMin(e.getValue()));
        datePicker_end.addValueChangeListener(
                e -> datePicker_start.setMax(e.getValue()));

        datePicker_start.addClassName("custom-date-picker");
        datePicker_end.addClassName("custom-date-picker");
        HorizontalLayout datePickersLayout = new HorizontalLayout(datePicker_start, datePicker_end);
        datePickersLayout.addClassName("date-pickers-layout");
        add(datePickersLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, datePickersLayout);

        //period picker
        Select<String> select_multiplier = new Select<>();
        select_multiplier.setLabel("Multiplier");
        select_multiplier.setItems("1", "5", "10", "15", "30", "60");
        select_multiplier.setValue("1");

        Select<String> select_period = new Select<>();
        select_period.setLabel("Range");
        select_period.setItems("day", "week", "month");
        select_period.setValue("day");

        select_multiplier.addClassName("custom-period-picker");
        select_period.addClassName("custom-period-picker");

        HorizontalLayout periodPickerLayout = new HorizontalLayout(select_multiplier, select_period);
        add(periodPickerLayout);
        setHorizontalComponentAlignment(Alignment.CENTER, periodPickerLayout);

        //respone of ai
        Paragraph outputParagraph = new Paragraph();
        outputParagraph.addClassName("output-paragraph");
        setHorizontalComponentAlignment(Alignment.CENTER, outputParagraph);

        //button
        Button button = new Button("Genereate Report",
            e ->
            {
                String text = textField.getValue().toUpperCase();
                String start_date = (datePicker_start.getValue() != null) ? datePicker_start.getValue().toString() : "";
                String end_date = (datePicker_end.getValue() != null) ? datePicker_end.getValue().toString() : "";
                String multiplier = select_multiplier.getValue();
                String period = select_period.getValue();
                outputParagraph.setText(""); // Reset the output paragraph
                System.out.println(datePicker_start.getValue());
                if(!text.isEmpty() && !start_date.isEmpty() && !end_date.isEmpty()){
                    String tickerValues = null;
                    String geminiResponse = null;
                    try {
                        tickerValues = StockParser.parse(tickerApi.getTickerValues(text, multiplier, period, start_date, end_date));
                        if(tickerValues.isEmpty()){
                            typeText(outputParagraph, "Bro put a valid ticker.".split(" "));
                        }else {
                            geminiResponse = geminiApi.generateContent(tickerValues);
                            typeText(outputParagraph, GeminiResponseParser.parseResponse(geminiResponse).split(" "));
                        }
                    } catch (Exception ex){
                        System.out.println(ex.getMessage());
                        typeText(outputParagraph, ex.getMessage().split(" "));
                    }
                }else if(text.isEmpty()){
                    typeText(outputParagraph, "Insert a ticker bro.".split(" "));
                }else{
                    typeText(outputParagraph, "Insert a date bro.".split(" "));
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