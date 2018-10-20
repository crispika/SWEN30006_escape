package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import controller.CarController;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MudTrap;
import tiles.MapTile.Type;
import utilities.Coordinate;


/*
 * This class is a singleton to maintain all information of the map
 * As an information expert
 */

public class MapManager {
	private static MapManager manager;
	
	private CarController car;
	
	private HashMap<Coordinate, Boolean> scanMap = new HashMap<Coordinate,Boolean>();
	private HashMap<Coordinate, MapTile> originMap = new HashMap<Coordinate, MapTile>();
	private HashMap<Coordinate, MapTile> realMap = new HashMap<Coordinate, MapTile>();
	private HashMap<Coordinate, MapTile> tempMap = new HashMap<>(); // get all reachable points of traps scranned by every safeExplore;
	private HashMap<Coordinate, MapTile> goalTempMap = new HashMap<>(); //get all reachable points of traps scranned by every goalExplore;
	
	private String START;
	private Coordinate finish;
	private Coordinate start;
	private int viewSquare;
	
	private ArrayList<Coordinate> keylist = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> safePos = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> reachable = new ArrayList<Coordinate>();
	private ArrayList<Coordinate> fakeReachable = new ArrayList<>();
	private ArrayList<Coordinate> keytype = new ArrayList<>();
	
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
		//System.out.println(reachable);
		//System.out.println("-----------------------------");
		cleanReachable();
		System.out.println(reachable);
		//System.out.println(start);
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
					
					if(currentView.get(furtherPos).isType(Type.TRAP)) {
						
						//System.out.println("-------------addtoSafeTemp-------------");
						tempMap.put(furtherPos, currentView.get(furtherPos));
						//System.err.println("----------------addtoGoalTemp----------");
						//System.out.println(currentView.get(furtherPos));
						goalTempMap.put(furtherPos, currentView.get(furtherPos));
						
						
					}
					setKeyInfo(furtherPos);
					setSafePos(furtherPos);
				}
				if (originMap.containsKey(backPos) && !scanMap.get(backPos)) {
					scanMap.put(backPos, true);
					realMap.put(backPos, currentView.get(backPos));
					
					if(currentView.get(backPos).isType(Type.TRAP) && ! (currentView.get(backPos) instanceof MudTrap)) {
						//System.out.println("-------------addtoSafeTemp-------------");
						tempMap.put(backPos, currentView.get(backPos));
						//System.err.println("----------------addtoGoalTemp----------");
						//System.out.println(currentView.get(backPos));
						goalTempMap.put(backPos, currentView.get(backPos));
						//System.out.println(goalTempMap);
						
					}
					setKeyInfo(backPos);
					setSafePos(backPos);
				}
			}
		}
	}
	public Coordinate getFinish() {
		return finish;
	}


	//find all detected and reachable healthTraps
	public void cleanHealthPos() {
		resetReachable();
		Iterator<Coordinate> iterator = safePos.iterator();
		while(iterator.hasNext()) {
			Coordinate healthPos = iterator.next();
			if(!isReachable(healthPos)) {
				iterator.remove();
			}
		}
	}
	
	public Coordinate findNearestHealth(Coordinate currPos) {
		int Nearest = 9999;
		Coordinate nearPos = new Coordinate(-9999,-9999);
		for (Coordinate health: safePos) {
			int mDistance = Math.abs(health.x - currPos.x) + Math.abs(health.y - currPos.y);
			if(mDistance < Nearest) {
				Nearest = mDistance;
				nearPos = health;
			}
		}
		if(nearPos.equals(new Coordinate(-9999,-9999))) {
			nearPos = null;
		}
		System.err.println("-----nearest health point is: " + nearPos);
		return nearPos;
	}


	//find all reachable but undetected location in the map
	public ArrayList<Coordinate> unScannedPoint(){
		resetReachable();
		ArrayList<Coordinate> unScannedPoint = new ArrayList<>();
		for (Coordinate key: scanMap.keySet()) {
			if(scanMap.get(key) == false && isReachable(key)) {
				unScannedPoint.add(key);
			}
		}
		return unScannedPoint;
	}
	
	public boolean foundAllkey() {
		keytype.clear();;
		resetReachable();
		for (Coordinate keyPos: keylist) {
			if (isReachable(keyPos) && !keytype.contains(((LavaTrap)realMap.get(keyPos)).getKey())) {
				keytype.add(keyPos);
			}
		}
		if(keytype.size() == car.numKeys()) {
			System.err.println("found all keys: " + keytype);
			return true;
		}
		else {
			//System.err.println("only found keys: " + keytype);
			return false;
		}
	}
	
	public ArrayList<Coordinate> unCatchedKey(){
		ArrayList<Coordinate> unCatchedKeys = new ArrayList<>();
		for (Coordinate keyPos: keytype) {
			if (!car.getKeys().contains(((LavaTrap)realMap.get(keyPos)).getKey())){
				unCatchedKeys.add(keyPos);
			}
		}
		//System.err.println("-----still have uncatched keys...." + unCatchedKeys);
		return unCatchedKeys;
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


	//to show four successors of one coordinate
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
	
	public void cleanReachable() {    // remove point with 3 walls around which is "DeadEnd"
		Iterator<Coordinate> iterator = reachable.iterator();
		while (iterator.hasNext()) {
			Coordinate pos = iterator.next();
			HashMap<Coordinate, MapTile> successors = getSuccessors(pos);
			int count = 0;
			for (Coordinate key : successors.keySet()) {
				if (successors.get(key) == null) {
					count += 1;
				}
			}
			if (count >= 3) {
				iterator.remove();
			}
		}
		fakeReachable.clear();
		fakeReachable.addAll(reachable);
		Iterator<Coordinate> fakeIterator = fakeReachable.iterator();
		while (fakeIterator.hasNext()) {
			Coordinate safePos = fakeIterator.next();
			HashMap<Coordinate, MapTile> successors2 = getSuccessors(safePos);
			int count2 = 0;
			for (Coordinate safeKey : successors2.keySet()) {
				if (successors2.get(safeKey) == null || successors2.get(safeKey).isType(Type.TRAP)) {
					count2 += 1;
				}
			}
			if (count2 >= 3) {
				fakeIterator.remove();
			}
		}
	}
	public boolean isFakeReachable(Coordinate pos){
		if (fakeReachable.contains(pos)){
			return true;
		}
		return false;
	}

	public void resetReachable() {
		reachable = Search.DFS(start);
		cleanReachable();
	}
	
	public boolean isReachable(Coordinate pos) {
		if (reachable.contains(pos)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isDeadRoad(Coordinate pos) { // to test if a goal to explore the new area is surrounded by over 2 walls/traps
		int counter = 0;
		HashMap<Coordinate, MapTile> successors = getSuccessors(pos);
		for (Coordinate sucPos: successors.keySet()) {
			if (successors.get(sucPos) == null || 
					(successors.get(sucPos).isType(Type.TRAP) && !(successors.get(sucPos) instanceof HealthTrap))) {
				counter+=1;
			}
		}
		if (counter >= 2) {
			return true;
		}
		return false;
	}
	
	public void clearTempMap() {
		tempMap.clear();
	}
	
	public void clearGoalTempMap() {
		goalTempMap.clear();
	}
	
	public HashMap<Coordinate, MapTile> getTempMap(){
		return tempMap;
	}
	
	public HashMap<Coordinate, MapTile> getGoalTempMap(){
		return goalTempMap;
	}

}
