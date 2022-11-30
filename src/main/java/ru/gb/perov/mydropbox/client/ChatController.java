package ru.gb.perov.mydropbox.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.ResourceBundle;


public class ChatController implements Initializable {
    public TextField input;
    public ListView<String> listView;
    public ListView<String> fileListView;
    public TextField path;
    private IoNet net;
    private Window stage;


    public void sendMsg(ActionEvent actionEvent) throws IOException {
        net.sendMsg(input.getText());
        input.clear();
    }

    private void addMessage(String msg) {
        Platform.runLater(() -> listView.getItems().add(msg));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Platform.runLater(input::requestFocus);
            Socket socket = new Socket("localhost", 8189);
            net = new IoNet(this::addMessage, socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendfile(ActionEvent actionEvent) {
        Path fullPath = Paths.get(path.getText(), "/", fileListView.getSelectionModel().getSelectedItem());
        File file = new File(String.valueOf(fullPath));
        net.sendFile(file);
    }

    public void updateDir() {
        if (path.getText().equals("Select directory...") || Objects.equals(path.getText(), "") || path.getText() == null || !Files.exists(Path.of(path.getText()))) {
            changeDir();
        }
        File dir = new File(path.getText());
        fileListView.getItems().clear();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile())
                fileListView.getItems().add(file.getName());
        }
//надо разобраться как это работает вместо for (File file : Objects.requireNonNull(dir.listFiles()))
//        try {
//            Files.walk(Paths.get(String.valueOf(dir)))
//                    .filter(Files::isRegularFile)
//                    .collect(Collectors.toList());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void changeDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory...");

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory != null) {
            path.setText(selectedDirectory.getAbsolutePath());
        } else {
            path.setText("c:/");
        }

        updateDir();
    }
}
