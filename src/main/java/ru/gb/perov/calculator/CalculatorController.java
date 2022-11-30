package ru.gb.perov.calculator;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class CalculatorController {
    public TextField output;

    public void inputDigit(ActionEvent actionEvent) {
        Button source = (Button) actionEvent.getSource();
        String digit = source.getText();
        output.appendText(digit);
    }
}
