package com.example.multiplayergame4;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launch extends Application {

    private Stage stage;
    private Scene scene;
    private Parent root;


    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Launch.class.getResource("launchScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("BLABLA");
        stage.setScene(scene);
        scene.getRoot().requestFocus();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void swtichToGameScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle("GAME");
        stage.setScene(scene);
        scene.getRoot().requestFocus();
        stage.show();
    }
}
