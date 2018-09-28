package com.jorgeortizesc.iaplayer;


import java.io.IOException;

import com.jorgeortizesc.iaplayer.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Launcher extends Application {


	public static Image ICO;
	@Override
	public void start(Stage stage) {
		stage.setTitle("IAPlayer");

		ICO  = new Image("file:res/icon.png");
		stage.getIcons().add(ICO);
		stage.setMinHeight(800);
		stage.setMinWidth(1200);

		MainController mc = new MainController(stage);

		FXMLLoader loader = new FXMLLoader();
		loader.setController(mc);

		loader.setLocation(Launcher.class.getResource("view/View.fxml"));
		BorderPane pane = null;
		try {
			pane = loader.load();
		} catch (IOException e) {
			System.exit(0);
		}

		pane.getStylesheets().add(Launcher.class.getResource("view/style.css").toExternalForm());

		mc.init();

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
