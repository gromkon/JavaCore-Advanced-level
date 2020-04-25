package Lesson_8.Homework.Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream("sample.fxml"));
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        //завершение на крестик
        controller = loader.getController();

        primaryStage.setOnCloseRequest(event -> {
            controller.dispose();
            Platform.exit();
            System.exit(0);
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}
