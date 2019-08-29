package application;

import java.util.ArrayList;
import java.util.Calendar;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Line;

public class Model {
	private Controller controller = null;
	private Network network = null;
	private AnimationTimer timer = null;
	private boolean timerRunning = false;
	
	private int gameMode = GAME_MODE_NULL;
	
	private Point2D ballPosition;
	private double ballVelocity;
	private Point2D ballDirection;
	
	private double batLeftPosition;
	private double batRightPosition;
	private char playerSelect = 'n';
	private boolean pauseAI = false;
	private double ballCalculatedHit;
	private byte tickCounter;
	private double AI_calculationAngle = (-Math.PI * (Math.random() * 2.0 - 1.0))/36;
	private boolean mp_my_turn = false;

	private int scoreLeftPlayer;
	private int scoreRightPlayer;

	private double BALL_speed_base = 5.0;
	private double BALL_speed_maximum = 8.0;
	private double BALL_speed_increment = .2;
	
	private double fieldWidth	= 1024.0;
	private double fieldHeight	= 576.0;
	private double batWidth		= 100.0;
	private double batThickness	= 20.0;
	private double batGap		= 5.0;
	private double ballSize		= 20.0;
	private double effectiveFieldWidth;
	private double effectiveFieldHeight;
	private double batLimit;

	public static final int GAME_MODE_NULL = -1;
	public static final int GAME_MODE_SINGLEPLAYER = 0;
	public static final int GAME_MODE_MULTIPLAYER_LOCAL = 1;
	public static final int GAME_MODE_MULTIPLAYER_NETWORK = 2;
	public static final int GAME_MODE_AI = 3;
	
	
	public Model(){
		effectiveFieldWidth = fieldWidth - ballSize - 2 * (batThickness + batGap);
		effectiveFieldHeight = fieldHeight - ballSize;
		batLimit = (this.fieldHeight-this.batWidth)/2-this.batGap;

		
		timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                tick();
            }
        };
	}

	public ArrayList<Line> calculateBallPath(double angle){
		double dirX = ballDirection.getX();
		double dirY = ballDirection.getY();
		if(angle != 0.0){
			dirX = Math.cos(angle) * dirX + -Math.sin(angle) * dirY;
			dirY = Math.sin(angle) * dirX + Math.cos(angle) * dirY;
		}
		
		double startX = ballPosition.getX();
		double startY = ballPosition.getY();
		double endX = startX;
		double endY = startY;
		
		ArrayList<Line> ballPath = new ArrayList<Line>();
		do{
			if(dirY > 0.0){
				endX = (dirX/dirY) * ((effectiveFieldHeight/2.0) - startY - (startX/-dirX) * dirY);
				endY = effectiveFieldHeight/2.0;
			}
			else if(dirY < 0.0){
				endX = (dirX/dirY) * (-(effectiveFieldHeight/2.0) - startY - (startX/-dirX) * dirY);
				endY = -effectiveFieldHeight/2.0;
			}
			if(Math.abs(endX) >= effectiveFieldWidth/2.0){
				double lrFactor = Math.abs(endX)/endX;
				double tEndX = lrFactor * effectiveFieldWidth/2.0;
				double tEndY = startY + (endY - startY) * ((tEndX - startX)/(endX - startX));
				endX = tEndX;
				endY = tEndY;
			}
			ballPath.add(new Line(startX, startY, endX, endY));
			startX = endX;
			startY = endY;
			dirY *= -1;
			
		}while(Math.abs(endX) < effectiveFieldWidth/2.0);
		
		return ballPath;
	}

	public double calculateBallTarget(double angle){
		double dirX = ballDirection.getX();
		double dirY = ballDirection.getY();
		if(angle != 0.0){
			dirX = Math.cos(angle) * dirX + -Math.sin(angle) * dirY;
			dirY = Math.sin(angle) * dirX + Math.cos(angle) * dirY;
		}
		
		double startX = ballPosition.getX();
		double startY = ballPosition.getY();
		double endX = startX;
		double endY = startY;
		
		do{
			if(dirY > 0.0){
				endX = (dirX/dirY) * ((effectiveFieldHeight/2.0) - startY - (startX/-dirX) * dirY);
				endY = effectiveFieldHeight/2.0;
			}
			else if(dirY < 0.0){
				endX = (dirX/dirY) * (-(effectiveFieldHeight/2.0) - startY - (startX/-dirX) * dirY);
				endY = -effectiveFieldHeight/2.0;
			}
			if(Math.abs(endX) >= effectiveFieldWidth/2.0){
				double lrFactor = Math.abs(endX)/endX;
				double tEndX = lrFactor * effectiveFieldWidth/2.0;
				double tEndY = startY + (endY - startY) * ((tEndX - startX)/(endX - startX));
				endX = tEndX;
				endY = tEndY;
			}
			startX = endX;
			startY = endY;
			dirY *= -1;
			
		}while(Math.abs(endX) < effectiveFieldWidth/2.0);
		
		return endY;
	}
	
	public void initialize(Controller controller){
		if(this.controller == null){
			this.controller = controller;
			resetGame();
		}
	}
	
	public void resetField(){
		ballPosition = new Point2D(0.0, 0.0);
		batLeftPosition = 0.0;
		batRightPosition = 0.0;
		ballVelocity = BALL_speed_base;
		tickCounter = 0;
		ballDirection = new Point2D(1.0-((int)(2.0*Math.random())*2.0), (2.0*Math.random()-1.0)/2.0).normalize();

		if(ballDirection.getX() < 0.0 && playerSelect == 'l') 
			this.mp_my_turn = true;
		else if(ballDirection.getX() > 0.0 && playerSelect == 'r') 
			this.mp_my_turn = true;
		
		ballCalculatedHit = this.calculateBallTarget(AI_calculationAngle);
		pauseAI = false;
		
		controller.update();
	}
	
	public void resetGame(){
		resetField();
		scoreLeftPlayer = 0;
		scoreRightPlayer = 0;
	}
	
	public void pauseAI(){
		pauseAI = !pauseAI;
	}

	public void playerUp(char player){
		if(player == 'l' && batLeftPosition > -batLimit) batLeftPosition -= 5;
		if(player == 'r' && batRightPosition > -batLimit) batRightPosition -= 5;
		controller.update();
	}

	public void playerUp(){
		playerUp(this.playerSelect);
	}

	public void playerDown(char player){
		if(player == 'l' && batLeftPosition < batLimit) batLeftPosition += 5;
		if(player == 'r' && batRightPosition < batLimit) batRightPosition += 5;
		controller.update();
	}

	public void playerDown(){
		playerDown(this.playerSelect);
	}
	
	public void playPause(){
		if(timerRunning){
			timer.stop();
		}
		else{
			timer.start();
		}
		timerRunning = !timerRunning;
	}
	
	public void play(){
		if(!timerRunning){
			timer.start();
			timerRunning = true;
		}
	}
	
	public void pause(){
		if(timerRunning){
			timer.stop();
			timerRunning = false;
		}
	}
	
	public void tick(){
		if(gameMode == Model.GAME_MODE_SINGLEPLAYER){
			if(controller.keyActive(KeyCode.UP)) playerUp('l');
			if(controller.keyActive(KeyCode.DOWN)) playerDown('l');
			runAI('r');
			moveBall();
		}
		else if(gameMode == Model.GAME_MODE_MULTIPLAYER_LOCAL){
			if(controller.keyActive(KeyCode.UP)) playerUp('r');
			if(controller.keyActive(KeyCode.DOWN)) playerDown('r');
			if(controller.keyActive(KeyCode.W)) playerUp('l');
			if(controller.keyActive(KeyCode.S)) playerDown('l');
			moveBall();
		}
		else if(gameMode == Model.GAME_MODE_MULTIPLAYER_NETWORK){
			if(controller.keyActive(KeyCode.UP)) playerUp();
			if(controller.keyActive(KeyCode.DOWN)) playerDown();
			
			
			network.sendData(new DataObject(0, this.batLeftPosition, this.batRightPosition, this.getBallPositionX(), this.getBallPositionY(), this.ballVelocity, this.getBallDirectionX(), this.getBallDirectionY(), this.getScoreLeftPlayer(), this.getScoreRightPlayer()));
			//if((this.getBallDirectionX() < 0.0 && this.playerSelect == 'l') || (this.getBallDirectionX() >= 0.0 && this.playerSelect == 'r')){
			if(/*this.playerSelect == 'l'*/ this.mp_my_turn)	moveBall();
			//}
			
			
			//if(this.playerSelect == 'l') moveBall();
		}
		else if(gameMode == Model.GAME_MODE_AI){
			runAI('r');
			runAI('l');
			moveBall();
		}
		
		tickCounter = (byte)((tickCounter+1) % 60);
		controller.update();
	}
	
	private void processNetworkInput(DataObject data){
		/*if(this.playerSelect == 'l'){
			if(data.getDirX() < 0.0){
				this.batRightPosition = data.getRightPlayer();
			}
			else{
				this.ballPosition = new Point2D(data.getBallX(), data.getBallY());
				this.ballDirection = new Point2D(data.getDirX(), data.getDirY());
				this.ballVelocity = data.getVelocity();
				this.batRightPosition = data.getRightPlayer();
				this.scoreLeftPlayer = data.getLeftScore();
				this.scoreRightPlayer = data.getRightScore();
			}
		}
		else if(this.playerSelect == 'r'){
			if(data.getDirX() >= 0.0){
				this.batLeftPosition = data.getLeftPlayer();
			}
			else{
				this.ballPosition = new Point2D(data.getBallX(), data.getBallY());
				this.ballDirection = new Point2D(data.getDirX(), data.getDirY());
				this.ballVelocity = data.getVelocity();
				this.batLeftPosition = data.getLeftPlayer();
				this.scoreLeftPlayer = data.getLeftScore();
				this.scoreRightPlayer = data.getRightScore();
			}
		}*/
		
		
		if(this.playerSelect == 'l'){
			this.batRightPosition = data.getRightPlayer();
		}
		else if(this.playerSelect == 'r'){
			this.ballPosition = new Point2D(data.getBallX(), data.getBallY());
			this.ballDirection = new Point2D(data.getDirX(), data.getDirY());
			this.ballVelocity = data.getVelocity();
			this.batLeftPosition = data.getLeftPlayer();
			this.scoreLeftPlayer = data.getLeftScore();
			this.scoreRightPlayer = data.getRightScore();
		}
		
	}
	
	private void moveBall(){
		if(Math.abs(ballPosition.getY() + ballDirection.getY() * this.ballVelocity) > this.effectiveFieldHeight/2.0){
			double f1 = ((this.effectiveFieldHeight/2) - Math.abs(this.ballPosition.getY()))/Math.abs(this.ballDirection.getY() * this.ballVelocity);
			double f2 = 1.0 - f1;
			double cX = this.ballPosition.getX() + f1 * this.ballVelocity * this.ballDirection.getX();
			double cY = this.ballPosition.getY() + f1 * this.ballVelocity * this.ballDirection.getY();

			this.ballDirection = new Point2D(ballDirection.getX(), -ballDirection.getY()).normalize();

			double eX = cX + f2 * this.ballVelocity * this.ballDirection.getX();
			double eY = cY + f2 * this.ballVelocity * this.ballDirection.getY();
			this.ballPosition = new Point2D(eX, eY);
			
			
			if(ballVelocity < BALL_speed_maximum) ballVelocity += BALL_speed_increment;
			ballCalculatedHit = this.calculateBallTarget(AI_calculationAngle);
		}
		else if(Math.abs(ballPosition.getX() + ballDirection.getX() * this.ballVelocity) > this.effectiveFieldWidth/2.0){
			double f1 = ((this.effectiveFieldWidth/2) - Math.abs(this.ballPosition.getX()))/Math.abs(this.ballDirection.getX() * this.ballVelocity);
			double f2 = 1.0 - f1;
			double cX = this.ballPosition.getX() + f1 * this.ballVelocity * this.ballDirection.getX();
			double cY = this.ballPosition.getY() + f1 * this.ballVelocity * this.ballDirection.getY();

			if(this.ballPosition.getX() < 0.0 && cY > batLeftPosition - ((batWidth+ballSize)/2.0) && cY < batLeftPosition + ((batWidth+ballSize)/2.0)){
				this.ballDirection = new Point2D(-ballDirection.getX(), ballDirection.getY() + (cY - batLeftPosition)/((batWidth+ballSize)/2.0)).normalize();
				if(this.gameMode == Model.GAME_MODE_MULTIPLAYER_NETWORK && this.playerSelect == 'l') network.sendMessage("[cmd]yt");
				this.mp_my_turn = false;
			}
			
			else if(this.ballPosition.getX() > 0.0 && cY > batRightPosition - ((batWidth+ballSize)/2.0) && cY < batRightPosition + ((batWidth+ballSize)/2.0)){
				this.ballDirection = new Point2D(-ballDirection.getX(), ballDirection.getY() + (cY - batRightPosition)/((batWidth+ballSize)/2.0)).normalize();
				if(this.gameMode == Model.GAME_MODE_MULTIPLAYER_NETWORK && this.playerSelect == 'r') network.sendMessage("[cmd]yt");
				this.mp_my_turn = false;
			}
			
			else{
				if(ballPosition.getX() < 0.0) scoreRightPlayer++;
				else scoreLeftPlayer++;
				resetField();
				return;
			}
			
			double eX = cX + f2 * this.ballVelocity * this.ballDirection.getX();
			double eY = cY + f2 * this.ballVelocity * this.ballDirection.getY();
			this.ballPosition = new Point2D(eX, eY);
			
			ballCalculatedHit = this.calculateBallTarget(AI_calculationAngle);
		}
		else ballPosition = ballPosition.add(ballDirection.multiply(ballVelocity));
	}
	
	private void runAI(char side){
		if((ballDirection.getX() > 0.0 && side == 'r') || (ballDirection.getX() < -0.0 && side == 'l')){
			double batPos = batRightPosition;
			if(side == 'l') batPos = batLeftPosition;
			
			if(effectiveFieldWidth/2.0 - Math.abs(getBallPositionX()) < 100.0){
				if(batPos-20 > ballPosition.getY()){
					playerUp(side);
				}
				else if(batPos+20 < ballPosition.getY()){
					playerDown(side);
				}
			}
			else{
				if(batPos-2.5 > ballCalculatedHit){
					playerUp(side);
				}
				else if(batPos+2.5 < ballCalculatedHit){
					playerDown(side);
				}
			}
		}
		/*
		else if((ballDirection.getX() < 0.0 && side == 'r') || (ballDirection.getX() > 0.0 && side == 'l')){
			double batPos = batRightPosition;
			if(side == 'l') batPos = batLeftPosition;
			if(batPos-2.5 > 0.0){
				playerUp(side);
			}
			else if(batPos+2.5 < 0.0){
				playerDown(side);
			}
		}
		*/
	}
	
	
	public void host(int port){
		if(network == null) network = new Network(this);
		network.host(port);
	}
	
	public void connect(String address, int port){
		if(network == null) network = new Network(this);
		System.out.println("Model: Connecting to " + address + ":" + port + ".");
		network.connect(address, port);
	}
	
	public void networkUpdate(Object o){
		if(o.getClass().getSimpleName().equals("String")){
			
			String s = (String)o;
			
			System.out.println("[" + s.length() + "] " + s);
			if(s.equals("[cmd]ConnectedHost")){
				controller.networkUpdate(o);
				this.mp_my_turn = true;
				playerSelect = 'l';
			}
			else if(s.equals("[cmd]ConnectedClient")){
				controller.networkUpdate(o);
				playerSelect = 'r';
			}
			else if(s.equals("[cmd]ConnectionReset")){
				network.destroy();
				network = null;
				pause();
				resetGame();
			}
			else if(s.equals("[cmd]yt")){
				this.mp_my_turn = true;
				System.out.println("Switch calculation.");
			}
		}
		else if(o.getClass().getSimpleName().equals("Message")){
			System.out.println((Message)o);
		}
		else if(o.getClass().getSimpleName().equals("DataObject")){
			processNetworkInput((DataObject)o);
		}
	}
	
	public void disconnect(){
		if(network != null){
			network.disconnect();
			network.destroy();
		}
		network = null;
	}
	
	
	public void setGameMode(int gm){
		gameMode = gm;
	}
	

	public double getBallPositionX(){
		return ballPosition.getX();
	}

	public double getBallPositionY(){
		return ballPosition.getY();
	}

	public double getBatLeftPosition(){
		return batLeftPosition;
	}

	public double getBatRightPosition(){
		return batRightPosition;
	}
	
	public double getBallVelocity(){
		return ballVelocity;
	}
	
	public double getBallDirectionX(){
		return ballDirection.getX();
	}
	
	public double getBallDirectionY(){
		return ballDirection.getY();
	}
	
	public int getScoreLeftPlayer(){
		return scoreLeftPlayer;
	}
	
	public int getScoreRightPlayer(){
		return scoreRightPlayer;
	}
	
	public char getPlayerSelect(){
		return this.playerSelect;
	}
}