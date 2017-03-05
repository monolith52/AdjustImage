package monolith52.adjustimage;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Application.fxml"));
		BorderPane root = (BorderPane)loader.load();
		root.getStylesheets().add(getClass().getResource("Application.css").toExternalForm());
		Scene scene = new Scene(root);
		((ApplicationController)loader.getController()).setScene(scene);
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((event) -> {
			Platform.exit();
			System.exit(0);
		});
		primaryStage.show();
		
//		ScenicView.show(scene);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

