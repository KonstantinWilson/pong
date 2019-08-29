package application;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GameField extends Canvas{
	private Model model = null;
	
	GraphicsContext gc = null;
	
	
	public GameField(){
		super();
	}
	
	public void initialize(Model model){
		this.model = model;
		gc = getGraphicsContext2D();
		
		// Setting background
		gc.setFill(Color.BLACK);
		gc.fillRect(0.0, 0.0, getWidth(), getHeight());
	}
	
	
	public void printFrame(){
		double gfWidth = getWidth();
		double gfHeight = getHeight();
		
		// Setting background
		gc.setFill(Color.BLACK);
		gc.fillRect(0.0, 0.0, getWidth(), getHeight());
		
		gc.setFill(Color.DARKSLATEGRAY);
		gc.fillRect(getWidth()/2-1, 0.0, 2, getHeight());
		
		
		
		/*
		// Help Line
		gc.setStroke(Color.GREEN);
		gc.setLineWidth(1.0);
		
		ArrayList<Line> linePath = model.calculateBallPath(0.0);
		double offsetX = gfWidth/2;
		double offsetY = gfHeight/2;
		for(int i = 0; i < linePath.size(); i++)
			gc.strokeLine(linePath.get(i).getStartX()+offsetX, linePath.get(i).getStartY()+offsetY, linePath.get(i).getEndX()+offsetX, linePath.get(i).getEndY()+offsetY);
		
		gc.setStroke(Color.BLUE);
		linePath = model.calculateBallPath((-Math.PI * (Math.random() * 2.0 - 1.0))/36);
		for(int i = 0; i < linePath.size(); i++)
			gc.strokeLine(linePath.get(i).getStartX()+offsetX, linePath.get(i).getStartY()+offsetY, linePath.get(i).getEndX()+offsetX, linePath.get(i).getEndY()+offsetY);
		*/
		
		
		
		// Painting bats and ball
		gc.setFill(Color.WHITE);
		gc.fillRect(model.getBallPositionX()+gfWidth/2-10.0, model.getBallPositionY()+gfHeight/2-10.0, 20, 20);
		
		if(model.getPlayerSelect() == 'l') gc.setFill(Color.CORNFLOWERBLUE);
		else gc.setFill(Color.WHITE);
		gc.fillRect(5, model.getBatLeftPosition()-50.0+gfHeight/2, 20, 100);
		if(model.getPlayerSelect() == 'r') gc.setFill(Color.CORNFLOWERBLUE);
		else gc.setFill(Color.WHITE);
		gc.fillRect(gfWidth-25.0, model.getBatRightPosition()-50.0+gfHeight/2, 20, 100);
		/*
		gc.fillText("(" + (((double)Math.round(model.getBallDirectionX()*10000))/10000) + " | " + (((double)Math.round(model.getBallDirectionY()*10000))/10000) + ")", model.getBallPositionX()+gfWidth/2+15.0, model.getBallPositionY()+gfHeight/2);
		gc.fillText("(" + (((double)Math.round(model.getBallPositionX()*10000))/10000) + " | " + (((double)Math.round(model.getBallPositionY()*10000))/10000) + ")", model.getBallPositionX()+gfWidth/2+15.0, model.getBallPositionY()+gfHeight/2+10.0);
		*/
	}
}