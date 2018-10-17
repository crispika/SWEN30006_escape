package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PrimitiveIterator.OfDouble;


import java.util.Stack;
import java.util.Queue;
import controller.CarController;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.MapTile.Type;
import utilities.Coordinate;

public class MapManager {
	private static MapManager manager;
	
	private CarController car;
	
	private HashMap<Coordinate, Boolean> scanMap = new HashMap<Coordinate,Boolean>();
	private HashMap<Coordinate, MapTile> originMap = new HashMap<Coordinate, MapTile>();
	private HashMap<Coordinate, MapTile> realMap = new HashMap<Coordinate, MapTile>();
	//private HashMap<Coordinate, MapTile>  currentView;
	
	private String START;
	private Coordinate finish;
	private Coordinate start;
	private int viewSquare;
	
	private ArrayList<Coordinate> keylist = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> safePos = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> reachable = new ArrayList<Coordinate>();
	
	private HashMap<String, MapTile> dirSuccessors ;
	
	public void initialize(CarController car) {
		
		this.car = car;
		originMap.putAll(car.getMap());
		realMap.putAll(car.getMap());
		START = car.getPosition();
		start = new Coordinate(START);
		for (Coordinate key : originMap.keySet()) {
			scanMap.put(key, false);
			if (originMap.get(key).isType(Type.FINISH)) {
				finish = key;
			}
		}
		
		viewSquare = car.getViewSquare();
		setScanMap();
		reachable = Search.DFS(start);
		System.out.println(reachable);
		System.out.println(start);
	}
	
	
	
	public static MapManager getInstance() {
		if(manager == null) {
			manager = new MapManager();
		}
		return manager;
	}
//	
//	public void initialize(HashMap<Coordinate, MapTile> originMap, String START,int viewSquare, HashMap<Coordinate, MapTile> currentView) {
//		
//		this.originMap.putAll(originMap);
//		for (Coordinate key: originMap.keySet()) {
//			scanMap.put(key, false);
//			if (originMap.get(key).isType(Type.FINISH)){
//				finish = key;
//			}
//		}
//		realMap.putAll(originMap);
//		this.START = START;
//		this.viewSquare =viewSquare;
//		start = new Coordinate(START);
//		setCurrentView(currentView);
//		setScanMap(start);
//	}
//	
//	public void setCurrentView(HashMap<Coordinate, MapTile> currentView){
//		this.currentView = new HashMap<Coordinate, MapTile>();
//		this.currentView.putAll(currentView);
//	}
	
	//record the map viewed by the car
	public void setScanMap() {
		Coordinate pos = new Coordinate(car.getPosition());
		HashMap<Coordinate, MapTile> currentView = car.getView();
		for (int i = 0; i < viewSquare; i++) {
			for (int j = 0; j < viewSquare; j++) {
				System.out.println(j);
				Coordinate furtherPos = new Coordinate(Integer.toString(pos.x + i) + "," + Integer.toString(pos.y + j));
				Coordinate backPos = new Coordinate(Integer.toString(pos.x - i) + "," + Integer.toString(pos.y - j));
				if (originMap.containsKey(furtherPos) && !scanMap.get(furtherPos)) {
					scanMap.put(furtherPos, true);
					realMap.put(furtherPos, currentView.get(furtherPos));
					setKeyInfo(furtherPos);
					setSafePos(furtherPos);
				}
				if (originMap.containsKey(backPos) && !scanMap.get(backPos)) {
					scanMap.put(backPos, true);
					realMap.put(backPos, currentView.get(backPos));
					setKeyInfo(backPos);
					setSafePos(backPos);
				}
			}
		}
	}
	
	private void setKeyInfo(Coordinate pos) {
		if (realMap.get(pos) instanceof LavaTrap) {
			if (((LavaTrap) realMap.get(pos) ).getKey() > 0) {
				keylist.add(pos);
			}
		}
	}
	
	private void setSafePos(Coordinate pos) {
		if(realMap.get(pos) instanceof HealthTrap) {
			safePos.add(pos);
		}
	}
	
	public HashMap<Coordinate, MapTile> getrealMap(){
		return realMap;
	}
	
	
	//find what in the next direction
	public MapTile toNorth(Coordinate pos) {
		if(pos.y == car.mapHeight() -1) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(pos.x) + "," + Integer.toString(pos.y+1)));
	}
	
	public MapTile toSouth(Coordinate pos) {
		if(pos.y == 0) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(pos.x) + "," + Integer.toString(pos.y-1)));
	}
	
	public MapTile toEast(Coordinate pos) {
		if(pos.x == car.mapWidth() -1) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(pos.x+1) + "," + Integer.toString(pos.y)));
	}
	
	public MapTile toWest(Coordinate pos) {
		if(pos.x == 0) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(pos.x-1) + "," + Integer.toString(pos.y)));
	}
	
	public HashMap<Coordinate, MapTile> getSuccessors(Coordinate currPos){
		HashMap<Coordinate, MapTile> successors = new HashMap<Coordinate, MapTile>();
		dirSuccessors = new HashMap<String, MapTile>();
		
		if (toNorth(currPos) == null || toNorth(currPos).isType(Type.WALL) || toNorth(currPos) instanceof MudTrap) {
			
			successors.put(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y+1)), null);
			dirSuccessors.put("NORTH", null);
		}
		else {
			successors.put(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y+1)),toNorth(currPos));
			dirSuccessors.put("NORTH", toNorth(currPos));
		}
		
		if (toSouth(currPos) == null || toSouth(currPos).isType(Type.WALL) || toSouth(currPos) instanceof MudTrap) {
			successors.put(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y-1)), null);
			dirSuccessors.put("SOUTH", null);
		}
		else {
			successors.put(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y-1)),toSouth(currPos));
			dirSuccessors.put("SOUTH", toSouth(currPos));
		}
		
		if (toEast(currPos) == null || toEast(currPos).isType(Type.WALL) || toEast(currPos) instanceof MudTrap) {
			successors.put(new Coordinate(Integer.toString(currPos.x+1) + "," + Integer.toString(currPos.y)), null);
			dirSuccessors.put("EAST", null);
		}
		else {
			successors.put(new Coordinate(Integer.toString(currPos.x+1) + "," + Integer.toString(currPos.y)),toEast(currPos));
			dirSuccessors.put("EAST", toEast(currPos));
		}
		
		if (toWest(currPos) == null || toWest(currPos).isType(Type.WALL) || toWest(currPos) instanceof MudTrap) {
			successors.put(new Coordinate(Integer.toString(currPos.x-1) + "," + Integer.toString(currPos.y)), null);
			dirSuccessors.put("WEST", null);
		}
		else {
			successors.put(new Coordinate(Integer.toString(currPos.x-1) + "," + Integer.toString(currPos.y)),toWest(currPos));
			dirSuccessors.put("WEST", toWest(currPos));
		}
		
		return successors;
	}

	public HashMap<String, MapTile> getDirSuccessors(){
		return dirSuccessors;
	}


	public boolean isReachable(Coordinate pos) {
		if (reachable.contains(pos)) {
			return true;
		}
		else {
			return false;
		}
	}

	public void bfs(Coordinate startpos, Coordinate goalpos) {
		Queue<Coordinate> bfsQueue = new LinkedList<Coordinate>();

		bfsQueue.offer(startpos);

		while (!bfsQueue.isEmpty()) {
			Coordinate pos = bfsQueue.poll();
			for (Coordinate key: getSuccessors(pos).keySet()) {
				if (getSuccessors(pos).get(key) != null && !reachable.contains(key)) {
					bfsQueue.offer(key);
				}
			}
		}

	}
}
