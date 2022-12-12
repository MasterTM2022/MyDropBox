package ru.gb.perov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("client.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        stage.setTitle("MyDropBox");
        stage.setScene(scene);
        stage.show();
//        ChatController controller = fxmlLoader.getController();
//        stage.setOnCloseRequest(event -> {
////            controller.sendHistory();
////            controller.getClient().sendMessage(END);
//        });
    }

    public static void main(String[] args) {
        launch();
    }
}