package mycontroller;

import controller.CarController;
import tiles.MapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashMap;

public class MoveTest extends CarController{
    private final int CAR_MAX_SPEED = 1;


    private int view;
    private Coordinate start;
    private int counter = 0;

    public MoveTest(Car car) {
        super(car);
        //MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
        MapManager.getInstance().initialize(this);
        view = getViewSquare();
        start = new Coordinate(getPosition());
        //test
//		ArrayList<Coordinate> path = new ArrayList<Coordinate>();
//		Coordinate goal = new Coordinate(1,10);
//		this.path = findPath(start,goal);

    }

    @Override
    public void update() {

        //test
        Coordinate goal = new Coordinate(23,17);
        moveToPos(goal);

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
        if ( checkNext(orientation, currPos) == null) {
            return false;
        }
        else {
            return true;
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


    public String action(Coordinate currentPos, Coordinate nextPos){
        if (nextPos == null){
            return "stop";
        }

        WorldSpatial.Direction orientation = getOrientation();
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
        Coordinate currentPos = new Coordinate(getPosition());
        ArrayList<Coordinate> path = new ArrayList<Coordinate>();

        if(currentPos.equals(start)){
            action = "forward";
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
                applyForwardAcceleration(); break;
            case "back":
                applyReverseAcceleration(); break;
            case "left":
                turnLeft(); break;
            case "right":
                turnRight(); break;
            default:
                applyBrake(); break;
        }
    }
}
