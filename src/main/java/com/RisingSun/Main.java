package com.RisingSun;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class Main {
//    public static void main(String[] args) {
//        SpringApplication.run(Main.class, args);
//    }
//}


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        try{
            Image icon = new Image(getClass().getResourceAsStream("icons/2025-10-07T08.56.45_1-Photoroom.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e){
            System.err.println("Не удалось загрузить иконку: "+ e.getMessage());
        }

        primaryStage.setTitle("RisingSun Messenger");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(500);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
