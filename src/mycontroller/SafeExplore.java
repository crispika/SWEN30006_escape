package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;
import world.WorldSpatial.Direction;

public class SafeExplore {
	private static SafeExplore sm;
	private CarController car;
	private boolean findwall;
	private boolean clockwise;
	private Coordinate hitWall = new Coordinate(-1,-1);
	
	public static SafeExplore getInstance() {
		if(sm == null) { 
			sm = new SafeExplore();
		}
		return sm;
	}
	
	public void initialize(CarController car) {
		this.car = car;
	}
	
	public void initSafeExplore() {
		findwall = false;
		MapManager.getInstance().clearTempMap();
		hitWall = new Coordinate(-1,-1);
	}
	
	public Coordinate getHitWallPoint() {
		return hitWall;
	}
	
	public void safeExplore() {
		Coordinate currPos = new Coordinate(car.getPosition());
		// HashMap<String, MapTile> dirSuccessors =
		// MapManager.getInstance().getDirSuccessors();

		if (car.getSpeed() == 0 && canSafeAhead(car.getOrientation(), currPos)) {
			car.applyForwardAcceleration();
		}
		else {
			car.applyReverseAcceleration();
		}
		if(car.getSpeed()< 0) {
			car.applyBrake();
		}
		if (!findwall) {
			if (car.getSpeed() > 0) {
				if (SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currPos)) {
					car.applyForwardAcceleration();
				} else {
					boolean turnRight = false;
					
					hitWall = currPos;
					if (SafeExplore.getInstance().canSafeAhead(
							SafeExplore.getInstance().nextDirection(car.getOrientation(), turnRight), currPos)) {
						car.turnLeft();
						clockwise = false;
					} else {
						car.turnRight();
						clockwise = true;
					}
					findwall = true;
				}
			}
		}
		else {
			if (!clockwise) {
				if(SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currPos)) {
					if( !SafeExplore.getInstance().succHasWall(currPos)) {
						car.turnRight();
					}
					else {
						car.applyForwardAcceleration();
					}
				}
				else {
					car.turnLeft();
				}
			}
			else {
				if(SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currPos)) {
					if( !SafeExplore.getInstance().succHasWall(currPos)) {
						car.turnLeft();
					}
					else {
						car.applyForwardAcceleration();
					}
				}
				else {
					car.turnRight();
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
