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
		MapManager.getInstance().getSuccessors(currPos);
		//HashMap<String, MapTile> dirSuccessors = MapManager.getInstance().getDirSuccessors();

		if (getSpeed() == 0) {
			applyForwardAcceleration();
		}
		if(!findwall) {
			if (getSpeed() > 0) {
				if (canSafeAhead(getOrientation(), currPos)) {
					applyForwardAcceleration();
				} else {
					boolean turnRight = false;
					if (canSafeAhead(nextDirection(getOrientation(), turnRight), currPos)) {
						turnLeft();
						clockwise = false;
					} else {
						turnRight();
						clockwise = true;
					}
					findwall =true;
				}
			}
		}
		else {
			if (!clockwise) {
				if(canSafeAhead(getOrientation(), currPos)) {
					if( !succHasWall(currPos)) {
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
				if(canSafeAhead(getOrientation(), currPos)) {
					if( !succHasWall(currPos)) {
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
	
	
	public WorldSpatial.Direction nextDirection(WorldSpatial.Direction orientation, boolean turnRight){
		switch (orientation) {
		case EAST:
			if(turnRight) {
				return Direction.SOUTH;
			}
			else {
				return Direction.NORTH;
			}
		case NORTH:
			if(turnRight) {
				return Direction.EAST;
			}
			else {
				return Direction.WEST;
			}
		case SOUTH:
			if(turnRight) {
				return Direction.WEST;
			}
			else {
				return Direction.EAST;
			}
		case WEST:
			if(turnRight) {
				return Direction.NORTH;
			}
			else {
				return Direction.SOUTH;
			}
		default:
			return null;
		}
	}
	
	public boolean canSafeAhead(WorldSpatial.Direction orientation,Coordinate currPos) {
		Coordinate nextPos = findNextCoordinate(orientation, currPos);
		if ( checkNext(orientation, currPos) == null) {
			return false;
		}
		else if(!MapManager.getInstance().isReachable(nextPos)) {
			return false;
		}
		else if(checkNext(orientation, currPos).isType(Type.TRAP)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public Coordinate findNextCoordinate(WorldSpatial.Direction orientation,Coordinate currPos) {
		switch (orientation){
		case EAST:
			return new Coordinate(Integer.toString(currPos.x+1)+","+ Integer.toString(currPos.y));
		case NORTH:
			return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y+1));
		case SOUTH:
			return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y-1));
		case WEST:
			return new Coordinate(Integer.toString(currPos.x-1)+","+ Integer.toString(currPos.y));
		default:
			return null;
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
	
	public boolean succHasWall(Coordinate nextpos) {
		HashMap<Coordinate,MapTile> successors = MapManager.getInstance().getSuccessors(nextpos);
		for (Coordinate key: successors.keySet()) {
			if(successors.get(key) == null) {
				return true;
			}
			// in safeExplore, we assume all trap as wall
			if(successors.get(key).isType(Type.TRAP)) {
				return true;
			}
		}
		return false;
	}

}
