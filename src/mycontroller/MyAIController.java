package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{

	
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
	}

	@Override
	public void update() {
		
		//System.out.println(MapManager.getInstance().getrealMap());
		
	}
	
	
	//scan the map clockwise
	public void getNextRotatedGoal() {
		
	}
	
	public boolean isReachable(Coordinate pos) {
		
		return false;
	}

}
