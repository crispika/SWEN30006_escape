package mycontroller;

import java.util.HashMap;

import javax.swing.text.View;

import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{

	private int view;
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		view = getViewSquare();
	}

	@Override
	public void update() {
		

		
	}
	
	
	public void ClockWiseSearch() {
		for (int i=0;i<view;i++) {
			
		}
	}

}
