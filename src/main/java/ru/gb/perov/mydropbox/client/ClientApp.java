package ru.gb.perov.mydropbox.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;


public class ClientApp extends Application {



    @Override
    public void start(Stage stage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("chat.fxml")));
        stage.setTitle("MyDropBox Project");
        stage.setScene(new Scene(parent));
        stage.show();
    }
}
