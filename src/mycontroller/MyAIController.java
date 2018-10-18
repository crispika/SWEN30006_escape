package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

import com.badlogic.gdx.Input.Orientation;

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
	
	private GrassStrategy grassDealer = new GrassStrategy();
	private LavaStrategy lavaDealer = new LavaStrategy();

	private final int CAR_MAX_SPEED = 1;
	private int view;
	private Coordinate start;
	private ArrayList<Coordinate> visted = new ArrayList<>(); 
	private boolean inSafeExplore = true;
	private Coordinate currGoal;
	private int safeCounter = 0;
	private boolean inFire = false;
	private int stepCounter = 0;
	//private static boolean carForward = true;
	//private Coordinate escapePoint;
	private Coordinate possibleGoal; // when catching the key, if the calculated escapePoint is a undetected lava, use this goal to escape to the safepoint
	
	private ArrayList<Coordinate> allunExplore = new ArrayList<>();// maybe for use of collect all unExplore points
	
	
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
		
		
	
		if(currPos.equals(SafeExplore.getInstance().getHitWallPoint()) && safeCounter > 1 || stepCounter > 500){
			System.out.println("-------------1--------------");
			System.err.println("StepCounter: " + stepCounter);
			
			inSafeExplore = false;
			safeCounter = 0;
			stepCounter = 0;
			
			
			HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getTempMap();
			cleanTemp(temp);
			//System.err.println("Safetemp: " + temp);
		
			
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
					
					currGoal = lavaDealer.chooseGoal(temp, visted);
					inFire = lavaDealer.getInfire();
					GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					
				}
				else if(trapCount.size() == 1 && trapCount.contains("Grass")) {
					
					currGoal = grassDealer.chooseGoal(temp, visted);
					GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					
				}
				//Strategy combine Lava and Grass
				else {
					
				}
			}
		}
		
		
		if(inSafeExplore) {
			SafeExplore.getInstance().safeExplore();
			stepCounter +=1;
			if (currPos.equals(SafeExplore.getInstance().getHitWallPoint())) {
				safeCounter+=1;
				System.out.println("safecounter: " +  safeCounter);
			}
		}
		else {
			if(currPos.equals(currGoal)) {
				System.err.println(currGoal);
				//get all key find
//				if(inFire && keyPos.size()> 1) {
//					System.out.println("---------------infire--------------");
//					currGoal = backToSafePoint(getOrientation(), currPos);
//					System.err.println("backToGoal"+currGoal);
//					inFire =false;
//				}
//				else if(!inFire && keyPos.size()>0) {
//					currGoal = keyPos.pollFirst();
//					inFire = true;
//					System.err.println("!!! infire has been setted");
//					GoalExplore.getInstance().moveToPos(currGoal);
//					
//				}
//				else if (inFire && keyPos.size() ==1) {
//					//TODO Escape
//					currGoal = escapetoOutPoint(getOrientation(), currPos);
//					inFire = false;
//				}
				if(inFire) {
					//TODO Escape
//					System.out.println("speed: "+ getSpeed());
//					System.out.println(getOrientation());
//					System.out.println("currPos: " + currPos);
//					if(carForward) {
//						currGoal = escapetoOutPoint(getOrientation(), currPos);
//					}
//					else {
//						currGoal = backToSafePoint(getOrientation(), currPos);
//					}
					System.out.println("escapePoint: " + lavaDealer.getescapePoint());
					currGoal = lavaDealer.getescapePoint();
					
					HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getGoalTempMap();
					//System.out.println("originTemp: " + temp);
					cleanTemp(temp);
					//System.out.println("cleanedTemp: " + temp);
					ArrayList<Coordinate> canExplore = lavaDealer.canExplore(temp,visted);
					System.err.println("Canexplore: " + canExplore);
					possibleGoal = lavaDealer.nearestSafePoint(canExplore, currPos);
					System.err.println("posiblegoal: "+possibleGoal);
					
					
					GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					inFire = false;
					System.out.println("-------------infire set to false---------------");
					System.out.println("-----------get the key-----------");
					
				}
				else if( MapManager.getInstance().getrealMap().get(currPos) instanceof LavaTrap) {
					System.out.println("---------------escaping by explore-----------------");
					//TODO Escape
//					System.out.println("speed: "+ getSpeed());
//					if(carForward) {
//						currGoal = escapetoOutPoint(getOrientation(), currPos);
//					}
//					else {
//						currGoal = backToSafePoint(getOrientation(), currPos);
//					}
					
					currGoal = possibleGoal;
					
					GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					
					
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
	
	//remove unreachable points from newly detected tempHashmap 
	public void cleanTemp(HashMap<Coordinate, MapTile> temp) {
		MapManager.getInstance().resetReachable();
		Iterator<Coordinate> iterator = temp.keySet().iterator();
		while(iterator.hasNext()) {
			Coordinate pos = iterator.next();
			if(!MapManager.getInstance().isReachable(pos)) {
				iterator.remove();
			}
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
	
	
//	public static void setCarFoward(boolean carOrien) {
//		carForward = carOrien; 
//	}
	

}
