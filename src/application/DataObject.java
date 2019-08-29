package application;

import java.nio.ByteBuffer;

public class DataObject {
	private int id;
	private double leftPlayer;
	private double rightPlayer;
	private double ballX;
	private double ballY;
	private double velocity;
	private double dirX;
	private double dirY;
	private int leftScore;
	private int rightScore;
	
	public DataObject(byte[] arr){
		if(arr.length >= 68){
			id = ByteBuffer.wrap(arr, 0, 8).getInt();
			leftPlayer = ByteBuffer.wrap(arr, 4, 8).getDouble();
			rightPlayer = ByteBuffer.wrap(arr, 12, 8).getDouble();
			ballX = ByteBuffer.wrap(arr, 20, 8).getDouble();
			ballY = ByteBuffer.wrap(arr, 28, 8).getDouble();
			velocity = ByteBuffer.wrap(arr, 36, 8).getDouble();
			dirX = ByteBuffer.wrap(arr, 44, 8).getDouble();
			dirY = ByteBuffer.wrap(arr, 52, 8).getDouble();
			leftScore = ByteBuffer.wrap(arr, 60, 4).getInt();
			rightScore = ByteBuffer.wrap(arr, 64, 4).getInt();
		}
	}
	
	public DataObject(int id, double leftPlayer, double rightPlayer, double ballX, double ballY, double velocity, double dirX, double dirY, int leftScore, int rightScore){
		this.id = id;
		this.leftPlayer = leftPlayer;
		this.rightPlayer = rightPlayer;
		this.ballX = ballX;
		this.ballY = ballY;
		this.velocity = velocity;
		this.dirX = dirX;
		this.dirY = dirY;
		this.leftScore = leftScore;
		this.rightScore = rightScore;
	}
	
	public byte[] toByteArray(){
		byte[] buffer = new byte[68];

		ByteBuffer.wrap(buffer, 0, 4).putInt(id);
		ByteBuffer.wrap(buffer, 4, 8).putDouble(leftPlayer);
		ByteBuffer.wrap(buffer, 12, 8).putDouble(rightPlayer);
		ByteBuffer.wrap(buffer, 20, 8).putDouble(ballX);
		ByteBuffer.wrap(buffer, 28, 8).putDouble(ballY);
		ByteBuffer.wrap(buffer, 36, 8).putDouble(velocity);
		ByteBuffer.wrap(buffer, 44, 8).putDouble(dirX);
		ByteBuffer.wrap(buffer, 52, 8).putDouble(dirY);
		ByteBuffer.wrap(buffer, 60, 4).putInt(leftScore);
		ByteBuffer.wrap(buffer, 64, 4).putInt(rightScore);
		/*
		byte[] tmp = ByteBuffer.allocate(8).putDouble(leftPlayer).array();
		for(int i = 0; i < 8; i++) buffer[i] = tmp[i];
		
		tmp = ByteBuffer.allocate(8).putDouble(rightPlayer).array();
		for(int i = 8; i < 16; i++) buffer[i] = tmp[i-8];
		
		tmp = ByteBuffer.allocate(8).putDouble(ballX).array();
		for(int i = 16; i < 24; i++) buffer[i] = tmp[i-16];
		
		tmp = ByteBuffer.allocate(8).putDouble(ballY).array();
		for(int i = 24; i < 32; i++) buffer[i] = tmp[i-24];
		
		tmp = ByteBuffer.allocate(8).putDouble(velocity).array();
		for(int i = 32; i < 40; i++) buffer[i] = tmp[i-32];
		
		tmp = ByteBuffer.allocate(8).putDouble(dirX).array();
		for(int i = 40; i < 48; i++) buffer[i] = tmp[i-40];
		
		tmp = ByteBuffer.allocate(8).putDouble(dirY).array();
		for(int i = 48; i < 56; i++) buffer[i] = tmp[i-48];
		
		tmp = ByteBuffer.allocate(4).putInt(leftScore).array();
		for(int i = 56; i < 60; i++) buffer[i] = tmp[i-56];
		
		tmp = ByteBuffer.allocate(4).putInt(rightScore).array();
		for(int i = 60; i < 64; i++) buffer[i] = tmp[i-60];
		*/
		
		return buffer;
	}

	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}

	public double getLeftPlayer(){
		return leftPlayer;
	}
	
	public double getRightPlayer(){
		return rightPlayer;
	}
	
	public double getBallX(){
		return ballX;
	}
	
	public double getBallY(){
		return ballY;
	}
	
	public double getVelocity(){
		return velocity;
	}
	
	public double getDirX(){
		return dirX;
	}
	
	public double getDirY(){
		return dirY;
	}
	
	public int getLeftScore(){
		return leftScore;
	}
	
	public int getRightScore(){
		return rightScore;
	}
	
}
