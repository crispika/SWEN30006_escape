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
		MapManager.getInstance().getSuccessors(currPos);
		HashMap<String, MapTile> dirSuccessors = MapManager.getInstance().getDirSuccessors();
		
		if (getSpeed() == 0) {
			if(canAhead(getOrientation(), currPos)) {
				applyForwardAcceleration();
			}
			else {
				applyReverseAcceleration();
			}
		}
		else {
			if(canAhead(getOrientation(), currPos)) {
				
				applyForwardAcceleration();
			}
		}
		
		
	}
	
	public boolean canAhead(WorldSpatial.Direction orientation, Coordinate currPos) {
		if ( checkNext(orientation, currPos)) {
			
		}
		
	}
		
	public MapTile checkNext(WorldSpatial.Direction orientation, Coordinate currPos) {
		MapManager.getInstance().getSuccessors(currPos);
		HashMap<String, MapTile> dirSuccessors = MapManager.getInstance().getDirSuccessors();
		switch (orientation) {
		case EAST:
			if (dirSuccessors.get("EAST") == null) {
				return null;
			} else {
				return dirSuccessors.get("EAST");
			}
		case NORTH:
			if (dirSuccessors.get("NORTH") == null) {
				return null;
			} else {
				return dirSuccessors.get("NORTH");
			}
		case SOUTH:
			if (dirSuccessors.get("SOUTH") == null) {
				return null;
			} else {
				return dirSuccessors.get("SOUTH");
			}
		case WEST:
			if (dirSuccessors.get("WEST") == null) {
				return null;
			} else {
				return dirSuccessors.get("WEST");
			}
		default:
			System.err.println("I am into Default Case");
			return null;
		}
	}


}
