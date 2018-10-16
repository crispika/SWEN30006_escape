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
	private Coordinate start;
	private enum GoalDirection {North,South,West,East};
	private GoalDirection currGoal;
	
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		view = getViewSquare();
		start = new Coordinate(getPosition());
		currGoal = GoalDirection.North;
	}

	@Override
	public void update() {
		
		
		
	}
	
	
	public void ClockWiseSearch() {
		int x = start.x;
		int y = start.y;
		Coordinate currPos = new Coordinate(getPosition());
		switch(currGoal) {
		case North:
			
//			if (currPos.y < y) {
//				if (MapManager.getInstance().toNorth().isType(Type.ROAD)) {
//
//				}
//			}
//			else {
//				currGoal = GoalDirection.East;
//			}
			
			
			
			
			break;
		case East:
			break;
		case South:
			break;
		case West:
			break;
		default:
			System.err.println("I am into default case");
			break;
		}
		
	}

}
