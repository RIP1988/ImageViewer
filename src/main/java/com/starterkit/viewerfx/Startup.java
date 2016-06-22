package com.starterkit.viewerfx;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.starterkit.viewerfx.controller.ViewerController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class Startup extends Application {
	private static final Logger LOG = Logger.getLogger(Startup.class);
	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * @see {@link javafx.application.Application#start(Stage)}
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		LOG.debug("Starting application...");
		/*
		 * Set the default locale based on the '--lang' startup argument.
		 */
		String langCode = getParameters().getNamed().get("lang");
		if (langCode != null && !langCode.isEmpty()) {
			Locale.setDefault(Locale.forLanguageTag(langCode));
		}

		primaryStage.setTitle("StarterKit-JavaFX");

		/*
		 * Load screen from FXML file with specific language bundle (derived
		 * from default locale).
		 */
		Parent root = FXMLLoader.load(getClass().getResource("/com/starterkit/viewerfx/view/viewer.fxml"), //
				ResourceBundle.getBundle("com/starterkit/viewerfx/bundle/base"));

		Scene scene = new Scene(root);

		/*
		 * Set the style sheet(s) for application.
		 */
		scene.getStylesheets().add(getClass().getResource("/com/starterkit/viewerfx/css/standard.css").toExternalForm());

		primaryStage.setScene(scene);

		primaryStage.show();
		LOG.debug("Stage shown");
	}
		
}