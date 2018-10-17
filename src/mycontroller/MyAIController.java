package mycontroller;

import controller.CarController;
import utilities.Coordinate;
import world.Car;

public class MyAIController extends CarController{

	private final int CAR_MAX_SPEED = 1;
	private int view;
	private Coordinate start;
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		SafeExploreStrategy.getInstance().initialize(this);
		
		start = new Coordinate(getPosition());
		view = getViewSquare();
	}

	@Override
	public void update() {
		MapManager.getInstance().setScanMap();
		SafeExploreStrategy.getInstance().safeExplore();
		
		
	}
	
	
	
	
	
}
