package ru.gb.perov.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ChatController {
    private NettyNet net;
    @FXML
    private HBox authBox;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField password;
    @FXML
    private volatile ProgressBar progressBar;

    @FXML
    private HBox fileBox;

    @FXML
    private Label serverCurrentPath;
    @FXML
    private Button changeServerPath;
    @FXML
    private ListView<String> serverFileList;

    @FXML
    private Button copyToServer;
    @FXML
    private Button moveToServer;
    @FXML
    private Button copyToClient;
    @FXML
    private Button moveToClient;

    @FXML
    private Label clientCurrentPath;
    @FXML
    private Button changeClientPath;
    @FXML
    private ListView<String> clientFileList;
    @FXML
    private TextField newNick1;
    @FXML
    private TextField newNick;

    @FXML
    private Window stage;


    public ChatController() {
        net = new NettyNet(System.out::println);
//        Scanner in = new Scanner(System.in);
//        while (in.hasNextLine()) {
//            String msg = in.nextLine();
//            net.sendMesage(new StringMessage(msg, LocalDateTime.now()));
//        }
    }

    public void updateClientDir() {
        if (clientCurrentPath.getText().equals("Change DIR...") ||
                Objects.equals(clientCurrentPath.getText(), "") ||
                clientCurrentPath.getText() == null ||
                !Files.exists(Path.of(clientCurrentPath.getText()))) {
            changeDir();
        }
        File dir = new File(clientCurrentPath.getText());
        clientFileList.getItems().clear();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isFile()) {
                clientFileList.getItems().add(file.getName());
            } else {
                clientFileList.getItems().add("[" + file.getName() + "]");
            }

        }
    }

    private void changeDir() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Select Directory...");

        File selectedDirectory = chooser.showDialog(stage);

        if (selectedDirectory != null) {
            clientCurrentPath.setText(selectedDirectory.getAbsolutePath());
        } else {
            clientCurrentPath.setText("c:/");
        }
        updateClientDir();
    }

    public void copyToServer() {
//        net.sendMsg(input.getText());
//        input.clear();
    }

    public void updateServerDir() {

    }

//    private final ChatClient client;
//    private String selectedFileOnServer;
//    private String selectedFileOnClient;

//    public void setProgress(double persentage) {
//        progressBar.setProgress(persentage);
//    }
}