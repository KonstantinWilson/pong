package application;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Controller{
	private Model model = null;
	private Stage primaryStage = null;
	private Stage networkStage = null;
	
	@FXML private GameField gameField;
	@FXML private Text tScoreLeft;
	@FXML private Text tScoreRight;
	@FXML private MenuBar menuBar;
	GraphicsContext gc = null;

	@FXML private TextField hostPort;
	@FXML private Button hostButton;
	@FXML private TextField connectIPAddress;
	@FXML private TextField connectPort;
	@FXML private Button connectButton;

	private boolean key_UP = false;
	private boolean key_DOWN = false;
	private boolean key_W = false;
	private boolean key_S = false;
	private boolean key_ENTER = false;
	private boolean key_R = false;
	private boolean key_P = false;
	private boolean key_T = false;
	
	public Controller(){
		
	}
	
	public void initialize(Model model){
		if(this.model == null){
			this.model = model;
			gameField.initialize(model);
		}
	}
	
	public void setPrimaryStage(Stage primaryStage){
		this.primaryStage = primaryStage;
	}

	public void keyPressed(final KeyEvent event){
		switch(event.getCode()){
			case UP:	key_UP = true;
						break;
			case DOWN:	key_DOWN = true;
						break;
			case W:		key_W = true;
						break;
			case S:		key_S = true;
						break;
			case ENTER:	if(!key_ENTER) model.playPause();
						key_ENTER = true;
						break;
			case R:		if(!key_R) model.resetGame();
						key_R = true;
						break;
			case P:		if(!key_P) model.pauseAI();
						key_P = true;
						break;
			case T:		if(!key_T) model.tick();
						key_T = true;
						break;
			default:
		}
	}

	public void keyReleased(final KeyEvent event){
		switch(event.getCode()){
			case UP:	key_UP = false;
						break;
			case DOWN:	key_DOWN = false;
						break;
			case W:		key_W = false;
						break;
			case S:		key_S = false;
						break;
			case ENTER:	key_ENTER = false;
						break;
			case R:		key_R = false;
						break;
			case P:		key_P = false;
						break;
			case T:		key_T = false;
						break;
			default:
		}
	}

	@FXML
	private void menuSingleplayer(final ActionEvent event)
	{
		model.pause();
		Alert alert = new Alert(AlertType.INFORMATION, "You are the left player.\nArrow Key Up - Move paddle up\nArrow Key Down - Move paddle down", ButtonType.OK);
		alert.showAndWait();
		model.setGameMode(Model.GAME_MODE_SINGLEPLAYER);
		model.resetGame();
		model.play();
	}

	@FXML
	private void menuAIMatch(final ActionEvent event)
	{
		model.pause();
		Alert alert = new Alert(AlertType.INFORMATION, "AIs will play one match against each other.", ButtonType.OK);
		alert.showAndWait();
		model.setGameMode(Model.GAME_MODE_AI);
		model.resetGame();
		model.play();
	}

	@FXML
	private void menuMultiplayerLocal(final ActionEvent event)
	{
		model.pause();
		Alert alert = new Alert(AlertType.INFORMATION, "Left Player:\nW Key - Move paddle up\nS Key - Move paddle down\n\nRight Player:\nArrow Key Up - Move paddle up\nArrow Key Down - Move paddle down", ButtonType.OK);
		alert.showAndWait();
		model.setGameMode(Model.GAME_MODE_MULTIPLAYER_LOCAL);
		model.resetGame();
		model.play();
	}

	@FXML
	private void menuMultiplayerNetwork(final ActionEvent event)
	{
		model.pause();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Network.fxml"));
			loader.setController(this);
			Parent root = loader.load();
			networkStage = new Stage();
			networkStage.setTitle("Pong - Multiplayer");
			networkStage.setScene(new Scene(root, 300, 350));
			networkStage.initModality(Modality.APPLICATION_MODAL);
			networkStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
				@Override
				public void handle(WindowEvent event) {
					model.disconnect();
					networkStage.close();
					networkStage = null;
				}
			});
			networkStage.show();
			if(this.primaryStage != null){
				networkStage.setX(primaryStage.getX() + (primaryStage.getWidth() - networkStage.getWidth())/2);
				networkStage.setY(primaryStage.getY() + (primaryStage.getHeight() - networkStage.getHeight())/2);
			}
			// Hide this current window (if this is what you want)
			//((Node)(event.getSource())).getScene().getWindow().hide();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		        
	}
	
	public void update(){
		gameField.printFrame();
		tScoreLeft.setText(new StringBuilder().append(model.getScoreLeftPlayer()).toString());
		tScoreRight.setText(new StringBuilder().append(model.getScoreRightPlayer()).toString());
	}
	
	
	public boolean keyActive(KeyCode kc){
		switch(kc){
			case UP:	return key_UP;
			case DOWN:	return key_DOWN;
			case W:		return key_W;
			case S:		return key_S;
			case ENTER:	return key_ENTER;
			case R:		return key_R;
			case P:		return key_P;
			case T:		return key_T;
			default:
		}
		return false;
	}
	
	// ------------------------------------- Network Stage
	
	public void networkCancel(final ActionEvent event){
		model.disconnect();
		networkStage.close();
		networkStage = null;
	}

	public void networkHost(final ActionEvent event){
		if(hostPort != null){
			if(hostPort.isDisabled()){
				model.disconnect();
				hostPort.setDisable(false);
				connectIPAddress.setDisable(false);
				connectPort.setDisable(false);
				connectButton.setDisable(false);
				hostButton.setText("Host");
			}
			else{
				System.out.println("Hosting on port " + hostPort.getText() + ".");
				model.host(Integer.parseInt(hostPort.getText()));
				hostPort.setDisable(true);
				connectIPAddress.setDisable(true);
				connectPort.setDisable(true);
				connectButton.setDisable(true);
				hostButton.setText("Cancel ...");
			}
		}
	}

	public void networkConnect(final ActionEvent event){
		if(connectIPAddress != null && connectPort != null){
			if(connectPort.isDisabled()){
				model.disconnect();
				hostPort.setDisable(false);
				hostButton.setDisable(false);
				connectIPAddress.setDisable(false);
				connectPort.setDisable(false);
				connectButton.setText("Connect");
			}
			else{
				System.out.println("Controller: Connecting to " + connectIPAddress.getText() + ":" + connectPort.getText() + ".");
				model.connect(connectIPAddress.getText(), Integer.parseInt(connectPort.getText()));
				hostPort.setDisable(true);
				hostButton.setDisable(true);
				connectIPAddress.setDisable(true);
				connectPort.setDisable(true);
				connectButton.setText("Cancel ...");
			}
		}
	}
	
	public void networkUpdate(Object o){
		if(o != null){
			if(networkStage != null){
				Platform.runLater(new Runnable(){
					@Override
					public void run() {
						networkStage.close();
						networkStage = null;
						model.resetGame();
						model.play();
					}
				});
			}
			
			model.setGameMode(Model.GAME_MODE_MULTIPLAYER_NETWORK);
		}
	}
}
