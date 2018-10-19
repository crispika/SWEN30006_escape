package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PrimitiveIterator.OfDouble;

import controller.CarController;
import tiles.GrassTrap;
import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.Car;

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
	private static boolean carForward = true;
	private Coordinate escapeGoal;
	private boolean hasGoaltoCatchKey;
	private Coordinate catchKeyGoal;
	
	private boolean inCatchKey;
	private Coordinate furtherKey;
	
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
		
		
		//get the key and exit
		if(MapManager.getInstance().foundAllkey()) {
			//System.exit(0);
			ArrayList<Coordinate> unCatchedKey = MapManager.getInstance().unCatchedKey();
			
			if(!hasGoaltoCatchKey) {
				//TODO find nearest health point
				MapManager.getInstance().cleanHealthPos();
				ArrayList<Coordinate> path = Search.BFS_findPathToCloestH(currPos);
				catchKeyGoal = path.get(path.size() -1);
				GoalExplore.getInstance().moveToPos(catchKeyGoal);
				hasGoaltoCatchKey = true;
				inHealth = true;
				System.out.println("--------------------hasGoalToCatchKey setted to true----------");
			}
			else {
				if(currPos.equals(catchKeyGoal)) {
					if(inHealth) {
						System.out.println("----------------------inHealth - catchKey---------------");
						if(getHealth() < 100) {
							applyBrake();
						}
						else {
							inHealth = false;
						}
					}
					else if(getHealth() < 70) {
						System.out.println("-------------------to nearest health point ---------------");
						catchKeyGoal = MapManager.getInstance().findNearestHealth(currPos);
						System.err.println();
						inHealth = true;
					}
					else {
						if(unCatchedKey.size() == 0) {
							System.out.println("-----------!!!nice!!!---to the exit!!!!!!!------------");
							catchKeyGoal = MapManager.getInstance().getFinish();
							
						}
						else {
							catchKeyGoal = unCatchedKey.get(0);
							System.err.println("--------to catch key: " + catchKeyGoal);
						}
					}
				}
				else {
					System.out.println("---------------to Key-----------------");
					System.out.println("current pos: "+currPos);
					System.out.println("next key： "+catchKeyGoal);
					GoalExplore.getInstance().moveToPos(catchKeyGoal);
				}
			}
		}
		
		// explore the map
		else {
			if(currPos.equals(SafeExplore.getInstance().getHitWallPoint()) && safeCounter > 0 || stepCounter > 500){
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
				
				//no newly MapTile found;	
				if(trapCount.size() == 0) {
					System.err.println("---------------no new this detected --------------");
					combineCanExplore(new ArrayList<>());
					if(allunExplore.size() > 0) {
						//System.err.println("allunExplore: "+allunExplore);
						currGoal = healthDealer.randomPick(allunExplore);
					}
					else {
						currGoal = healthDealer.randomPick(MapManager.getInstance().unScannedPoint());
					}
					//System.err.println(allunExplore);
					System.err.println("------currGoal is setted to: " + currGoal);
					//GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
					
				}
				//only lava case or "lava and grass" case, use lavadealer to solve
				else if (trapCount.size() == 1 && trapCount.contains("Lava")
						|| (trapCount.size() == 2 && trapCount.contains("Grass") && trapCount.contains("Lava"))) {
					
					if(getHealth() < 70) { //catch one key and escape
						currGoal = lavaDealer.chooseGoal(temp, visted,getHealth());
						if(currGoal == null) {
							currGoal = lavaDealer.randomPick(allunExplore);
						}
						combineCanExplore(lavaDealer.getCanExplore());
						inFire = lavaDealer.getInfire();
						System.err.println("----------inFire is: " + inFire);
						escapeGoal = lavaDealer.getescapePoint();
						System.err.println("--------received escape point: "+ escapeGoal);
						//GoalExplore.getInstance().initGoalExplore();
						GoalExplore.getInstance().moveToPos(currGoal);
					}
					else { // catch 2 key and escape
						currGoal = lavaDealer.catch2Keys(temp, visted);
						if(currGoal == null) {
							currGoal = lavaDealer.randomPick(allunExplore);
						}
						combineCanExplore(lavaDealer.getCanExplore());
						
						inCatchKey =lavaDealer.getInCatchKey();
						System.err.println("----------inCatchKey is: " + inCatchKey);
						furtherKey = lavaDealer.getFurtherKey();
						System.err.println("---------furtherkeyPos is "+ furtherKey);
						inFire = lavaDealer.getInfire();
						System.err.println("----------inFire is: " + inFire);
						escapeGoal = lavaDealer.getescapePoint();
						System.err.println("--------received escape point: "+ escapeGoal);
						//GoalExplore.getInstance().initGoalExplore();
						GoalExplore.getInstance().moveToPos(currGoal);
					}
					
						
				}
				//only grass case
				else if(trapCount.size() == 1 && trapCount.contains("Grass")) {		
					currGoal = grassDealer.chooseGoal(temp, visted,getHealth());
					if(currGoal == null) {
						currGoal = grassDealer.randomPick(allunExplore);
					}
					combineCanExplore(grassDealer.getCanExplore());
					//GoalExplore.getInstance().initGoalExplore();
					GoalExplore.getInstance().moveToPos(currGoal);
				}
				
				//when found health trap:
				else{
					currGoal = healthDealer.chooseGoal(temp, visted, getHealth());
					if(currGoal == null) {
						currGoal = healthDealer.randomPick(allunExplore);
					}
					
					System.out.println("--------Health case: currGoal: " + currGoal);
					combineCanExplore(healthDealer.getCanExplore());
					inHealth = healthDealer.getInHealth();
					inFire = healthDealer.getInfire();
					System.err.println("----------inFire is: " + inFire);
					escapeGoal = healthDealer.getescapePoint();
					System.err.println("--------received escape point: "+ escapeGoal);
					
					futureGoal = healthDealer.getFutureGoal();
					if (futureGoal.equals(new Coordinate(-1,-1))) {
						futureGoal = healthDealer.randomPick(allunExplore);
					}
					System.err.println("-----getFutureGoal: " + futureGoal);
					
					//GoalExplore.getInstance().initGoalExplore();
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
//					MapManager.getInstance().cleanHealthPos();
					
//					Coordinate nearHealth = MapManager.getInstance().findNearestHealth(currPos);
					int toNearestHeathPos = 9999;
//					if( !(nearHealth == null) ) {
//						toNearestHeathPos = Search.BFS_findPathToCloestH(currPos).size();
//					}
					ArrayList<Coordinate> path = Search.BFS_findPathToCloestH(currPos);
					if(path != null) {
						toNearestHeathPos = path.size();
					}
					
					
					if(inFire) {
						System.out.println("-------------------infire---------------------");
						//TODO Escape after get key:
						System.out.println("escaping to: " + lavaDealer.getescapePoint());

						//ArrayList canExplore = lavaDealer.getCanExplore();
						currGoal = escapeGoal;
						
						//for possible goal is escapePoint is not valid
						HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getGoalTempMap();
						//System.out.println("originTemp: " + temp);
						cleanTemp(temp);
						//System.out.println("cleanedTemp: " + temp);
						ArrayList<Coordinate> localcanExplore = lavaDealer.canExplore(temp,visted);
						System.err.println("Canexplore: " + localcanExplore);
						combineCanExplore(localcanExplore);
						possibleGoal = lavaDealer.nearestSafePoint(localcanExplore, currPos);
						System.err.println("posiblegoal: "+possibleGoal);
						
						
						//GoalExplore.getInstance().initGoalExplore();
						//GoalExplore.getInstance().moveToPos(currGoal);
						inFire = false;
						System.err.println("-------------infire set to false---------------");
						System.out.println("-----------get the key-----------");
						
					}
					else if(!inCatchKey && MapManager.getInstance().getrealMap().get(currPos) instanceof LavaTrap) {
						System.out.println("---------------escaping by explore-----------------");
						//TODO Escape from undetected lavaTrap:
						currGoal = possibleGoal;
						//GoalExplore.getInstance().initGoalExplore();
						//GoalExplore.getInstance().moveToPos(currGoal);
					}
					else if(inHealth) {
						if (getHealth()<100) {
							applyBrake();
							System.out.println("---------------stop for getting health---------------");
						}
						else {
							MapTile futuregoal = MapManager.getInstance().getrealMap().get(futureGoal);
							if( (futuregoal.isType(Type.ROAD) || futuregoal instanceof HealthTrap) && !MapManager.getInstance().isDeadRoad(futureGoal)) {
								currGoal = futureGoal;
								System.err.println("future goal can work : " + futureGoal);
							}
							else {
								HashMap<Coordinate, MapTile> temp = MapManager.getInstance().getGoalTempMap();
								//System.out.println("originTemp: " + temp);
								cleanTemp(temp);
								//System.out.println("cleanedTemp: " + temp);
								ArrayList<Coordinate> canExplore = lavaDealer.canExplore(temp,visted);
								System.err.println("inHealth case, redetected Canexplore goal: " + canExplore);
								combineCanExplore(canExplore);
								currGoal = healthDealer.randomPick(canExplore);
								System.err.println("future goal can't work, changed to goal: " + currGoal);
							}
							
							System.out.println("health 100, move to futureGoal: " + futureGoal);
							inHealth = false;
							System.out.println("-------------inhealth setted to false-----------------");
							//GoalExplore.getInstance().initGoalExplore();
							//GoalExplore.getInstance().moveToPos(currGoal);
						}
						
					}
					else if(inCatchKey) {
						currGoal = furtherKey;
						inCatchKey = false;
						System.out.println("-------------inCatchKey setted to false-----------------");
						inFire = true;
					}
					
					else if (getHealth() < 65 && toNearestHeathPos <20) {
						System.err.println("-----------------health<65, to catch health------------------------");
						//ArrayList<Coordinate> path = Search.BFS_findPathToCloestH(currPos);
						currGoal = path.get(path.size() -1);
//						MapTile mapTile = MapManager.getInstance().getrealMap().get(currPos);
//						if( ! (mapTile instanceof LavaTrap ) && ! (mapTile instanceof GrassTrap)) {
//						}
						futureGoal = currPos;
						inHealth = true;
					}
					else {
						System.out.println("---------------start next round of safeExplore--------------");
						inSafeExplore = true;
						SafeExplore.getInstance().initSafeExplore();
						SafeExplore.getInstance().safeExplore();
					}
				}
				else {
					//System.err.println(currGoal);
					System.out.println("------------Moving to Goal (Controller)------------");
					System.out.println(currPos + " TO " +currGoal);
					System.out.println("------------------");
					GoalExplore.getInstance().moveToPos(currGoal);
					
				}
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
	
	
	public static void setCarFoward(boolean carOrien) {
		carForward = carOrien;
	}

	public static boolean getCarForward(){
		return carForward;
	}


	public void combineCanExplore(ArrayList<Coordinate> canExplore) {
		Iterator<Coordinate> iterator = allunExplore.iterator();
		while(iterator.hasNext()) {
			Coordinate pos =iterator.next();
			if (visted.contains(pos) || !MapManager.getInstance().getrealMap().get(pos).isType(Type.ROAD) 
					|| !MapManager.getInstance().isReachable(pos)) {
				iterator.remove();
			}
		}
		System.out.println("set allunExplore: "+allunExplore);
		for (Coordinate newPos: canExplore) {
			if (!allunExplore.contains(newPos)) {
				allunExplore.add(newPos);
				//System.out.println("add explore: "+newPos);
			}
		}
	}
}
