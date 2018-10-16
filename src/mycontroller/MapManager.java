package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PrimitiveIterator.OfDouble;

import controller.CarController;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
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
	public MapTile toNorth() {
		Coordinate currPos = new Coordinate(car.getPosition());
		if(currPos.y == car.mapHeight()) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y+1)));
	}
	
	public MapTile toSouth() {
		Coordinate currPos = new Coordinate(car.getPosition());
		if(currPos.y == 0) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(currPos.x) + "," + Integer.toString(currPos.y-1)));
	}
	
	public MapTile toEast() {
		Coordinate currPos = new Coordinate(car.getPosition());
		if(currPos.x == 0) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(currPos.x-1) + "," + Integer.toString(currPos.y)));
	}
	
	public MapTile toWest() {
		Coordinate currPos = new Coordinate(car.getPosition());
		if(currPos.x == car.mapWidth()) {
			return null;
		}
		return realMap.get(new Coordinate(Integer.toString(currPos.x+1) + "," + Integer.toString(currPos.y)));
	}
	
	
}
