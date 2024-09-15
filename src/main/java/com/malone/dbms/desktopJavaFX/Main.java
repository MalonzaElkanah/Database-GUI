package com.malone.dbms.desktopJavaFX;

import com.malone.dbms.desktopJavaFX.App.AppView;
import com.malone.dbms.desktopJavaFX.App.AppController;
import com.malone.dbms.utils.DatabaseUtilities;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

// import jfxtras.styles.jmetro.JMetro;
// import jfxtras.styles.jmetro.Style;

public class Main extends Application {

    public void start(Stage stage) {
        DatabaseUtilities dbUtils = new DatabaseUtilities();
        AppView view = new AppView();
        AppController controller = new AppController(view);

        Scene scene = new Scene(view.getRoot());
        // JMetro metro = new JMetro(scene, Style.LIGHT);
        stage.setTitle("Malone DBMS");
        stage.setScene(scene);
        stage.show();
    }

    public static void main( String[] args ) {
        System.out.println("JavaFX Application Initializing...!");
        launch(args);
    }
}
