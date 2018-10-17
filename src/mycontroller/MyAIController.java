package mycontroller;

import java.util.HashMap;
import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class MyAIController extends CarController{
	private final int CAR_MAX_SPEED = 1;

	
	private int view;
	private Coordinate start;
	private boolean findwall = false;
	private boolean clockwise;
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		
		start = new Coordinate(getPosition());
		view = getViewSquare();
	}

	@Override
	public void update() {
		MapManager.getInstance().setScanMap();
		safeExplore();
		
		
	}
	
	public void safeExplore() {
		Coordinate currPos = new Coordinate(getPosition());
		// HashMap<String, MapTile> dirSuccessors =
		// MapManager.getInstance().getDirSuccessors();

		if (getSpeed() == 0) {
			applyForwardAcceleration();
		}
		if (!findwall) {
			if (getSpeed() > 0) {
				if (SafeExploreManager.getInstance().canSafeAhead(getOrientation(), currPos)) {
					applyForwardAcceleration();
				} else {
					boolean turnRight = false;
					if (SafeExploreManager.getInstance().canSafeAhead(
							SafeExploreManager.getInstance().nextDirection(getOrientation(), turnRight), currPos)) {
						turnLeft();
						clockwise = false;
					} else {
						turnRight();
						clockwise = true;
					}
					findwall = true;
				}
			}
		}
		else {
			if (!clockwise) {
				if(SafeExploreManager.getInstance().canSafeAhead(getOrientation(), currPos)) {
					if( !SafeExploreManager.getInstance().succHasWall(currPos)) {
						turnRight();
					}
					else {
						applyForwardAcceleration();
					}
				}
				else {
					turnLeft();
				}
			}
			else {
				if(SafeExploreManager.getInstance().canSafeAhead(getOrientation(), currPos)) {
					if( !SafeExploreManager.getInstance().succHasWall(currPos)) {
						turnLeft();
					}
					else {
						applyForwardAcceleration();
					}
				}
				else {
					turnRight();
				}
			}
		}
	}
	
	
	
}
