package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;


public class Main extends Application {
	public static Controller controller = null;
	public static Model model = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Pong.fxml"));
			Parent root = loader.load();
			controller = (Controller)loader.getController();
			if(controller != null){
				Scene scene = new Scene(root, 1100, 800);
				scene.getStylesheets().add(getClass().getResource("Pong.css").toExternalForm());
				
				primaryStage.setTitle("Pong");
				primaryStage.setScene(scene);
				primaryStage.setOnCloseRequest(e -> {
					Platform.exit();
					System.exit(0);
				});
				primaryStage.show();

				scene.setOnKeyPressed(new EventHandler<KeyEvent>(){
					@Override
					public void handle(KeyEvent event) {
						controller.keyPressed(event);
					}
				});
				scene.setOnKeyReleased(new EventHandler<KeyEvent>(){
					@Override
					public void handle(KeyEvent event) {
						controller.keyReleased(event);
					}
				});
				
				model = new Model();
				controller.initialize(model);
				controller.setPrimaryStage(primaryStage);
				model.initialize(controller);
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
