package mycontroller;

import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Future;

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
	private HealthStrategy healthDealer = new HealthStrategy();

	private final int CAR_MAX_SPEED = 1;
	private int view;
	private Coordinate start;
	private ArrayList<Coordinate> visted = new ArrayList<>(); 
	private boolean inSafeExplore = true;
	private Coordinate currGoal;
	private int safeCounter = 0;
	private boolean inFire = false;
	private int stepCounter = 0;
	
	private boolean inHealth = false;
	private Coordinate possibleGoal; // when catching the key, if the calculated escapePoint is a undetected lava, use this goal to escape to the unexplored safepoint;
	private Coordinate futureGoal; // when stopped to get health, saved the future goal to move;
	
	private ArrayList<Coordinate> allunExplore = new ArrayList<>();// maybe for use of collect all unExplore points
	//private static boolean carForward = true;
	
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
			System.out.println("-------------End of the safeExplore--------------");
			System.err.println("StepCounter: " + stepCounter);
			
			inSafeExplore = false;
			safeCounter = 0;
			stepCounter = 0;
			//get newly collected trap info by safeExplore
			HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getTempMap();
			//remove unreachable points in the Hashmap to make sure all traps detected can be reached/not surrounded by wall\MudTrap etc.
			cleanTemp(temp);
			
			//Analysis situation and find suitable strategy
			ArrayList<String> trapCount = new ArrayList<>();
			for (Coordinate pos: temp.keySet()) {
				if (temp.get(pos) instanceof HealthTrap && !trapCount.contains("Health")) {
					trapCount.add("Health");
				}
				if (temp.get(pos) instanceof LavaTrap && !trapCount.contains("Lava")) {
					trapCount.add("Lava");
				}
				if (temp.get(pos) instanceof GrassTrap && !trapCount.contains("Grass")) {
					trapCount.add("Grass");
				}
			}
			//only lava case	
			if (trapCount.size() == 1 && trapCount.contains("Lava")) {
				currGoal = lavaDealer.chooseGoal(temp, visted,getHealth());
				combineCanExplore(lavaDealer.getCanExplore());
				inFire = lavaDealer.getInfire();
				GoalExplore.getInstance().initGoalExplore();
				GoalExplore.getInstance().moveToPos(currGoal);
					
			}
			//only grass case
			else if(trapCount.size() == 1 && trapCount.contains("Grass")) {		
				currGoal = grassDealer.chooseGoal(temp, visted,getHealth());
				combineCanExplore(grassDealer.getCanExplore());
				GoalExplore.getInstance().initGoalExplore();
				GoalExplore.getInstance().moveToPos(currGoal);
			}
			
			else if(trapCount.size() == 1 && trapCount.contains("Health")){
				currGoal = healthDealer.chooseGoal(temp, visted, getHealth());
				combineCanExplore(healthDealer.getCanExplore());
				inHealth = healthDealer.getInHealth();
				futureGoal = healthDealer.getFutureGoal();
				System.err.println("-----getFutureGoal: " + futureGoal);
				
				GoalExplore.getInstance().initGoalExplore();
				GoalExplore.getInstance().moveToPos(currGoal);
				
			}
			//Strategy combine Lava and Grass 
			else if(trapCount.size() == 2 && trapCount.contains("Grass") && trapCount.contains("Lava")) {
				//Use grass case to solve, don't achieve for the key on the way;
				currGoal = grassDealer.chooseGoal(temp, visted,getHealth());
				combineCanExplore(grassDealer.getCanExplore());
				GoalExplore.getInstance().initGoalExplore();
				GoalExplore.getInstance().moveToPos(currGoal);
			}
			else {
				//other case same as pure health case
				currGoal = healthDealer.chooseGoal(temp, visted, getHealth());
				combineCanExplore(healthDealer.getCanExplore());
				inHealth = healthDealer.getInHealth();
				futureGoal = healthDealer.getFutureGoal();
				GoalExplore.getInstance().initGoalExplore();
				GoalExplore.getInstance().moveToPos(currGoal);
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
					System.out.println("-------------------infire---------------------");
					//TODO Escape after get key:
//					System.out.println("speed: "+ getSpeed());
//					System.out.println(getOrientation());
//					System.out.println("currPos: " + currPos);
//					if(carForward) {
//						currGoal = escapetoOutPoint(getOrientation(), currPos);
//					}
//					else {
//						currGoal = backToSafePoint(getOrientation(), currPos);
//					}
					System.out.println("escaping to: " + lavaDealer.getescapePoint());
					currGoal = lavaDealer.getescapePoint();
					
					HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getGoalTempMap();
					//System.out.println("originTemp: " + temp);
					cleanTemp(temp);
					//System.out.println("cleanedTemp: " + temp);
					ArrayList<Coordinate> localcanExplore = lavaDealer.canExplore(temp,visted);
					System.err.println("Canexplore: " + localcanExplore);
					possibleGoal = lavaDealer.nearestSafePoint(localcanExplore, currPos);
					System.err.println("posiblegoal: "+possibleGoal);
					
					
					GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					inFire = false;
					System.err.println("-------------infire set to false---------------");
					System.out.println("-----------get the key-----------");
					
				}
				else if( MapManager.getInstance().getrealMap().get(currPos) instanceof LavaTrap) {
					System.out.println("---------------escaping by explore-----------------");
					//TODO Escape from undetected lavaTrap:
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
				else if(inHealth) {
					if (getHealth()<100) {
						applyBrake();
						System.out.println("---------------stop for getting health---------------");
					}
					else {
						if(MapManager.getInstance().getrealMap().get(futureGoal).isType(Type.ROAD)) {
							currGoal = futureGoal;
						}
						else {
							HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getGoalTempMap();
							//System.out.println("originTemp: " + temp);
							cleanTemp(temp);
							//System.out.println("cleanedTemp: " + temp);
							ArrayList<Coordinate> canExplore = lavaDealer.canExplore(temp,visted);
							System.err.println("Canexplore: " + canExplore);
							combineCanExplore(canExplore);
							currGoal = healthDealer.randomPick(canExplore);
						}
						
						System.out.println("health 100, move to futureGoal: " + futureGoal);
						inHealth = false;
						System.out.println("-------------inhealth setted to false-----------------");
						GoalExplore.getInstance().initGoalExplore();
						GoalExplore.getInstance().moveToPos(currGoal);
					}
					
				}
				else {
					System.out.println("---------------start next round of safeExplore--------------");
					inSafeExplore = true;
					SafeExplore.getInstance().initSafeExplore();
					SafeExplore.getInstance().safeExplore();
				}
			}
			else {
				System.err.println(currGoal);
				System.out.println("---------------Moving to Goal--------------");
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
	
	
//	public static void setCarFoward(boolean carOrien) {
//		carForward = carOrien; 
//	}
	public void combineCanExplore(ArrayList<Coordinate> canExplore) {
		Iterator<Coordinate> iterator = allunExplore.iterator();
		while(iterator.hasNext()) {
			Coordinate pos =iterator.next();
			if (visted.contains(pos)) {
				iterator.remove();
			}
			if(!MapManager.getInstance().getrealMap().get(pos).isType(Type.ROAD)) {
				iterator.remove();
			}
		}
		for (Coordinate newPos: canExplore) {
			if (!allunExplore.contains(newPos)) {
				allunExplore.add(newPos);
			}
		}
	}
}
