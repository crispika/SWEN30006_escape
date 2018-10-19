package mycontroller;

import java.util.HashMap;

import controller.CarController;
import tiles.HealthTrap;
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
			//car.applyReverseAcceleration();
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
						//clockwise = false;
					} else {
						car.turnRight();
						//clockwise = true;
					}
					findwall = true;
				}
			}
		}
		else {
			if(ifSafe(car.getOrientation(),currPos,"right") && !ifSafe(car.getOrientation(),currPos,"left")){
				clockwise = true;
			}
			if(!ifSafe(car.getOrientation(),currPos,"right") && ifSafe(car.getOrientation(),currPos,"left")){
				clockwise = false;
			}



			System.out.println("---------SafeExplore--------");
			System.out.println("CurrentPos(SafeExplore): "+currPos);
			if (!clockwise) {

				if(ifSafe(car.getOrientation(),currPos,"right")) {
					car.turnRight();
				}
				else if (MyAIController.getCarForward()){
					System.out.println("forward");
					if(ifSafe(car.getOrientation(),currPos,"ahead")){
						car.applyForwardAcceleration();
					}
					else{
						car.turnLeft();
					}
				}
				else{
					System.out.println("back");
					if(ifSafe(car.getOrientation(),currPos,"back")){
						car.applyReverseAcceleration();
					}
					else{
						car.turnLeft();
					}

				}


//				if(SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currPos)) {
//					if( !SafeExplore.getInstance().succHasWall(currPos,car.getOrientation())) {
//						car.turnRight();
//					}
//					else {
//						car.applyForwardAcceleration();
//					}
//				}
//				else {
//					car.turnLeft();
//				}
			}
			else {

				if(ifSafe(car.getOrientation(),currPos,"left")) {
					System.out.println(MyAIController.getCarForward());
					car.turnLeft();

				}
				else if (MyAIController.getCarForward()){
					System.out.println("forward-");
					if(ifSafe(car.getOrientation(),currPos,"ahead")){
						car.applyForwardAcceleration();
					}
					else{
						car.turnRight();
					}
				}
				else{
					System.out.println("back-");
					if(ifSafe(car.getOrientation(),currPos,"back")){
						car.applyReverseAcceleration();
					}
					else{
						car.turnRight();
					}

				}





//				if(SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currPos)) {
//					if( !SafeExplore.getInstance().succHasWall(currPos,car.getOrientation())) {
//						car.turnLeft();
//					}
//					else {
//						car.applyForwardAcceleration();
//					}
//				}
//				else {
//					car.turnRight();
//				}
			}
		}

//		else {
//			if(!clockwise) {
//				if(canSafeAhead(car.getOrientation(), currPos)) {
//					if(!succHasWall(currPos, car.getOrientation(), !clockwise)) {
//						car.turnRight();
//					}
//					else {
//						car.applyForwardAcceleration();
//					}
//				}
//				else {
//					car.turnLeft();
//				}
//			}
//			else {
//				if(canSafeAhead(car.getOrientation(), currPos)) {
//					if(!succHasWall(currPos, car.getOrientation(), clockwise)) {
//						car.turnLeft();
//					}
//					else {
//						car.applyForwardAcceleration();
//					}
//				}
//				else {
//					car.turnRight();
//				}
//			}
//		}
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

	public Coordinate findBehindCoordinate(WorldSpatial.Direction orientation,Coordinate currPos) {
		switch (orientation){
			case EAST:
				return new Coordinate(Integer.toString(currPos.x-1)+","+ Integer.toString(currPos.y));
			case NORTH:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y-1));
			case SOUTH:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y+1));
			case WEST:
				return new Coordinate(Integer.toString(currPos.x+1)+","+ Integer.toString(currPos.y));
			default:
				return null;
		}
	}


	public Coordinate findLeftCoordinate(WorldSpatial.Direction orientation,Coordinate currPos) {
		switch (orientation){
			case EAST:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y+1));
			case NORTH:
				return new Coordinate(Integer.toString(currPos.x-1)+","+ Integer.toString(currPos.y));
			case SOUTH:
				return new Coordinate(Integer.toString(currPos.x+1)+","+ Integer.toString(currPos.y));
			case WEST:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y-1));
			default:
				return null;
		}
	}

	public Coordinate findRightCoordinate(WorldSpatial.Direction orientation,Coordinate currPos) {
		switch (orientation){
			case EAST:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y-1));
			case NORTH:
				return new Coordinate(Integer.toString(currPos.x+1)+","+ Integer.toString(currPos.y));
			case SOUTH:
				return new Coordinate(Integer.toString(currPos.x-1)+","+ Integer.toString(currPos.y));
			case WEST:
				return new Coordinate(Integer.toString(currPos.x)+","+ Integer.toString(currPos.y+1));
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

	public boolean succHasWall(Coordinate nextpos, WorldSpatial.Direction orientation) {
		Coordinate nextNextPos = findNextCoordinate(orientation, nextpos);
		HashMap<Coordinate,MapTile> successors = MapManager.getInstance().getSuccessors(nextpos);
		successors.remove(nextNextPos);
		for (Coordinate key: successors.keySet()) {
			if (nextpos.equals(new Coordinate(3,14))) {
				System.err.println("nextPos: " + nextpos);
				System.err.println("nextNextPos: " + nextNextPos);
				System.err.println(key);
				System.err.println(orientation);
			}


			if(successors.get(key) == null) {
				if (nextpos.equals(new Coordinate(3,14))) {
					System.err.println("------------1---------------");
				}
				return true;
			}
			// in safeExplore, we assume all trap as wall
			if(successors.get(key).isType(Type.TRAP)) {
				if (nextpos.equals(new Coordinate(3,14))) {
					System.err.println("------------2---------------");
				}
				return true;
			}
		}
		return false;
	}

//	public boolean succHasWall(Coordinate currPos, WorldSpatial.Direction orientation, boolean clockwise) {
//		Coordinate nextpos = findNextCoordinate(orientation, currPos);
//		HashMap<Coordinate,MapTile> successors = MapManager.getInstance().getSuccessors(nextpos);
//		if(clockwise) {
//			Coordinate left = findLeftCoordinate(orientation, nextpos);
//			if(successors.get(left) == null) {
//				return true;
//			}
//			if(successors.get(left).isType(Type.TRAP)) {
//				return true;
//			}
//			return false;
//		}
//		else {
//			Coordinate right = findRightCoordinate(orientation, nextpos);
//			if(successors.get(right) == null) {
//				return true;
//			}
//			if(successors.get(right).isType(Type.TRAP)) {
//				return true;
//			}
//			return false;
//
//		}
//	}

	public boolean ifLeftSafe(WorldSpatial.Direction orientation,Coordinate currPos) {
		Coordinate nextPos = findLeftCoordinate(orientation, currPos);

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

	public boolean ifRightSafe(WorldSpatial.Direction orientation,Coordinate currPos) {
		Coordinate nextPos = findRightCoordinate(orientation, currPos);

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

	public boolean ifSafe(WorldSpatial.Direction orientation,Coordinate currPos,String dir){
		HashMap<Coordinate,MapTile> successors = MapManager.getInstance().getSuccessors(currPos);

		switch(dir){
			case "left":
				Coordinate left = findLeftCoordinate(orientation,currPos);
				MapTile mapTile_l = successors.get(left);
				if(mapTile_l == null || !MapManager.getInstance().isReachable(left)){
					return false;
				}
				if(mapTile_l instanceof HealthTrap ||(!mapTile_l.isType(Type.WALL) && !mapTile_l.isType(Type.TRAP) && !mapTile_l.isType(Type.EMPTY))){
					return true;
				}
				break;
			case "right":
				Coordinate right = findRightCoordinate(orientation,currPos);
				MapTile mapTile_r = successors.get(right);
				if(mapTile_r == null || !MapManager.getInstance().isReachable(right)){
					return false;
				}
				if(mapTile_r instanceof HealthTrap ||(!mapTile_r.isType(Type.WALL) && !mapTile_r.isType(Type.TRAP) && !mapTile_r.isType(Type.EMPTY))){
					return true;
				}
				break;
			case "ahead":
				Coordinate ahead = findNextCoordinate(orientation,currPos);
				MapTile mapTile_a = successors.get(ahead);
				if(mapTile_a == null || !MapManager.getInstance().isReachable(ahead)){
					return false;
				}
				if(mapTile_a instanceof HealthTrap ||(!mapTile_a.isType(Type.WALL) && !mapTile_a.isType(Type.TRAP) && !mapTile_a.isType(Type.EMPTY))){
					return true;
				}
				break;
			case "back":
				Coordinate back = findBehindCoordinate(orientation,currPos);
				MapTile mapTile_b = successors.get(back);
				if(mapTile_b == null || !MapManager.getInstance().isReachable(back)){
					return false;
				}
				if(mapTile_b instanceof HealthTrap ||(!mapTile_b.isType(Type.WALL) && !mapTile_b.isType(Type.TRAP) && !mapTile_b.isType(Type.EMPTY))){
					return true;
				}
				break;
			default:
				System.err.println("Should not get here");
				return false;

		}
		return false;

	}

}
