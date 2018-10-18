package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import controller.CarController;
import tiles.GrassTrap;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

public class MyAIController extends CarController{

	private final int CAR_MAX_SPEED = 1;
	private int view;
	private Coordinate start;
	private ArrayList<Coordinate> visted = new ArrayList<>(); 
	private boolean inSafeExplore = true;
	private Coordinate currGoal;
	private int safeCounter = 0;
	private boolean inFire = false;
	
	public MyAIController(Car car) {
		super(car);
		//MapManager.getInstance().initialize(getMap(),getPosition(),getViewSquare(),getView());
		MapManager.getInstance().initialize(this);
		SafeExplore.getInstance().initialize(this);
		GoalExplore.getInstance().initialize(this);
		
		start = new Coordinate(getPosition());
		view = getViewSquare();
		SafeExplore.getInstance().initSafeExplore();
	}

	@Override
	public void update() {
		//save all viewed map to the real map
		MapManager.getInstance().setScanMap();
		//save the visted position;
		Coordinate currPos = new Coordinate(getPosition());
		addVisted(currPos);
		
		
	
		if(currPos.equals(SafeExplore.getInstance().getHitWallPoint()) && safeCounter > 1){
			System.out.println("-------------1--------------");
			MapManager.getInstance().resetReachable();
			inSafeExplore = false;
			safeCounter = 0;
			HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getTempMap();
			
			Iterator<Coordinate> iterator = temp.keySet().iterator();
			while(iterator.hasNext()) {
				Coordinate pos = iterator.next();
				if(!MapManager.getInstance().isReachable(pos)) {
					iterator.remove();
				}
			}
			
			Boolean onlyHealth = true;
			for (Coordinate pos: temp.keySet()) {
				if (!(temp.get(pos) instanceof HealthTrap)) {
					onlyHealth = false;
				}
			}
			if (temp.size() == 0 || onlyHealth) {
				//TODO randomly choose a direction to safeExplore
			}
			else {
				ArrayList<String> trapCount = new ArrayList<>();
				for (Coordinate pos: temp.keySet()) {
//					if (temp.get(pos) instanceof HealthTrap && !trapCount.contains("Health")) {
//						trapCount.add("Health");
//					}
					if(temp.get(pos) instanceof LavaTrap && !trapCount.contains("Lava")) {
						trapCount.add("Lava");
					}
					if(temp.get(pos) instanceof GrassTrap && !trapCount.contains("Grass")) {
						trapCount.add("Grass");
					}
				}
				
				if (trapCount.size() == 1 && trapCount.contains("Lava")) {
					ArrayList<Coordinate> canExplore = canExplore(temp);
					
					if(canExplore.size()>0) {
						currGoal = randomPick(canExplore);
						GoalExplore.getInstance().moveToPos(currGoal);
						System.err.println(currGoal);
						System.out.println("canExplore: "+canExplore);
					}
					else{
						ArrayList<Coordinate> keyPos = new ArrayList<>();
						for (Coordinate pos: temp.keySet()) {
							int keyNo = ((LavaTrap)temp.get(pos)).getKey();
							if( keyNo > 0) {
								keyPos.add(pos);
							}
						}
						currGoal = randomPick(keyPos);
						
						inFire = true;
						System.err.println("!!! infire has been setted");
						GoalExplore.getInstance().moveToPos(currGoal);
					}
					
				}
				else if(trapCount.size() == 1 && trapCount.contains("Grass")) {
					ArrayList<Coordinate> canExplore = canExplore(temp);
					
					if(canExplore.size()>0) {
						currGoal = randomPick(canExplore);
						GoalExplore.getInstance().moveToPos(currGoal);
					}
				}
				//Strategy combine Lava and Grass
				else {
					
				}
			}
		}
		
		
		if(inSafeExplore) {
			System.out.println("--------------------2--------------------");
			SafeExplore.getInstance().safeExplore();
			if (currPos.equals(SafeExplore.getInstance().getHitWallPoint())) {
				safeCounter+=1;
				System.out.println("safecounter: " +  safeCounter);
			}
		}
		else {
			if(currPos.equals(currGoal)) {
				System.err.println(currGoal);
				if(inFire) {
					System.out.println("---------------infire--------------");
					currGoal = escapePoint(getOrientation(), currPos);
					System.err.println("escapePoint"+currGoal);
					inFire =false;
				}
				else {
					System.out.println("---------------safe--------------");
					inSafeExplore = true;
					SafeExplore.getInstance().initSafeExplore();
					SafeExplore.getInstance().safeExplore();
				}
			}
			else {
				System.err.println(currGoal);
				System.out.println("---------------toGoal--------------");
				GoalExplore.getInstance().moveToPos(currGoal);
			}
		}
	}
	
	public void addVisted(Coordinate pos) {
		if(!visted.contains(pos)) {
			visted.add(pos);
		}
	}

	
	//For test
	//@Override
	public void update2() {
		// TODO Auto-generated method stub
		// save all viewed map to the real map
		MapManager.getInstance().setScanMap();
		// save the visted position;
		Coordinate currPos = new Coordinate(getPosition());
		addVisted(currPos);
		Coordinate goal = new Coordinate(35,0);
	    GoalExplore.getInstance().moveToPos(goal);
		
	}
	
	public Coordinate hasAvailableRoad(Coordinate pos, MapTile trap) {
		HashMap<Coordinate, MapTile> successors = MapManager.getInstance().getSuccessors(pos);
		for (Coordinate sucPos : successors.keySet()) {
			if(successors.get(sucPos) != null) {
				if(successors.get(sucPos).isType(Type.ROAD) && !visted.contains(sucPos)) {
					return sucPos;
				}
			}
		}
		return null;
	}
	
	public ArrayList<Coordinate> canExplore(HashMap<Coordinate, MapTile> temp) {
		ArrayList<Coordinate> canExplore = new ArrayList<>();
		for (Coordinate pos : temp.keySet()) {
			if(hasAvailableRoad(pos, temp.get(pos)) != null) {
				canExplore.add(hasAvailableRoad(pos, temp.get(pos)));
			}
		}
		return canExplore;
	}
	
	public Coordinate randomPick(ArrayList<Coordinate> canExplore) {
		Random random = new Random();
		int index = random.nextInt(canExplore.size());
		Coordinate goal = canExplore.get(index);
		return goal;
	}
	
	public Coordinate escapePoint(WorldSpatial.Direction orientation, Coordinate currPos) {
		Coordinate goal = null;
		while(goal == null) {
			Coordinate nextPos = SafeExplore.getInstance().findNextCoordinate(orientation, currPos);
			if(MapManager.getInstance().getrealMap().get(nextPos).isType(Type.ROAD)) {
				goal = nextPos;
			}
		}
		return goal;
	}
	
}
