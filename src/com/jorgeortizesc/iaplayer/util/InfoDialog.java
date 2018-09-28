package com.jorgeortizesc.iaplayer.util;

import com.jorgeortizesc.iaplayer.Launcher;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class InfoDialog {
	public static void show(String title, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(Launcher.ICO);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}
}
