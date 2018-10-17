package mycontroller;

import java.util.HashMap;

import javax.swing.text.View;

import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{
	private final int CAR_MAX_SPEED = 1;

	
	private int view;
	private Coordinate start;
	private int counter = 0;
	
	
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		view = getViewSquare();
		start = new Coordinate(getPosition());
	}

	@Override
	public void update() {
		
		
		
	}
	
	public void safeExplore() {
		Coordinate currPos = new Coordinate(getPosition()); 
		if (getSpeed() < CAR_MAX_SPEED) {
			
		}
		
		for (Coordinate key: MapManager.getInstance().getSuccessors(currPos).keySet()) {
			
		}
	}
	
	public boolean canAhead(WorldSpatial.Direction orientation, Coordinate currPos) {
		MapManager.getInstance().getSuccessors(currPos);
		HashMap<String, MapTile> dirSuccessors = MapManager.getInstance().getDirSuccessors();
		
		switch(orientation){
		case EAST:
			if(dirSuccessors.get("EAST") == null) {
				return false;
			}
			else {
				
			}
			
		case NORTH:
		case SOUTH:
		case WEST:
		default:
			return false;
	}
	


}
