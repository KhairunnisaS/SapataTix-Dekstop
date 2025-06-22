package com.example.sapatatix;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException; // Import IOException

public class Main extends Application {

    private static Stage primaryStageInstance; // Static reference to the primary stage

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStageInstance = primaryStage; // Store the primary stage instance

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sapatatix/FXML/BerandaFix.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Beranda");
        primaryStage.show();
    }

    // New static method to change the root of the scene
    public static void setRoot(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            primaryStageInstance.setScene(new Scene(root));
            primaryStageInstance.setTitle(title);
            primaryStageInstance.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load FXML: " + fxmlPath);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}