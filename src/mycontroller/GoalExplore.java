package mycontroller;

import java.util.ArrayList;

import controller.CarController;
import utilities.Coordinate;
import world.WorldSpatial;

public class GoalExplore {
	
	private static GoalExplore ge;
	private CarController car;
	
	public static GoalExplore getInstance() {
		if(ge == null) {
			ge = new GoalExplore();
		}
		return ge;
	}
	
	public void initialize(CarController car) {
		this.car = car;
	}
	
	
	public String action(Coordinate currentPos, Coordinate nextPos){
        if (nextPos == null){
            return "stop";
        }

        WorldSpatial.Direction orientation = car.getOrientation();
        switch (orientation) {
            case EAST:
                if(nextPos.y - currentPos.y == 1) return "left";
                else if(nextPos.y - currentPos.y == -1) return "right";
                else if(nextPos.x - currentPos.x == 1) return "forward";
                else if(nextPos.x - currentPos.x == -1) return "back";
                else return "stop";
            case NORTH:
                if(nextPos.y - currentPos.y == 1) return "forward";
                else if(nextPos.y - currentPos.y == -1) return "back";
                else if(nextPos.x - currentPos.x == 1) return "right";
                else if(nextPos.x - currentPos.x == -1) return "left";
                else return "stop";
            case SOUTH:
                if(nextPos.y - currentPos.y == 1) return "back";
                else if(nextPos.y - currentPos.y == -1) return "forward";
                else if(nextPos.x - currentPos.x == 1) return "left";
                else if(nextPos.x - currentPos.x == -1) return "right";
                else return "stop";
            case WEST:
                if(nextPos.y - currentPos.y == 1) return "right";
                else if(nextPos.y - currentPos.y == -1) return "left";
                else if(nextPos.x - currentPos.x == 1) return "back";
                else if(nextPos.x - currentPos.x == -1) return "forward";
                else return "stop";
            default:
                return "stop";
        }
    }

    public void moveToPos(Coordinate goal){
        String action;
        Coordinate currentPos = new Coordinate(car.getPosition());
        ArrayList<Coordinate> path = new ArrayList<Coordinate>();

        if(car.getSpeed() == 0){
        	if(SafeExplore.getInstance().canSafeAhead(car.getOrientation(), currentPos)) {
        		action = "forward";
        	}
        	else {
        		action = "back";
        	}
            
        }
        else if(currentPos.equals(goal)){
            action = "stop";
        }
        else{
            path = Search.BFS_findPathToPos(currentPos,goal);
            Coordinate nextPos = path.get(1);
            action = action(currentPos, nextPos);
        }
        System.out.println(path);
        System.out.println(action);
        switch (action) {
            case "forward":
                car.applyForwardAcceleration(); break;
            case "back":
                car.applyReverseAcceleration(); break;
            case "left":
                car.turnLeft(); break;
            case "right":
                car.turnRight(); break;
            default:
                car.applyBrake(); break;
        }
    }
}
