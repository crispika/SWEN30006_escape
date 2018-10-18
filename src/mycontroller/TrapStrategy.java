package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import utilities.Coordinate;
import world.WorldSpatial;

public abstract class TrapStrategy {
	protected ArrayList<Coordinate> canExplore;
	
	public abstract Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted,float health);
	
	


	public Coordinate hasAvailableRoad(Coordinate pos, MapTile trap, ArrayList<Coordinate> visted) {
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
	
	public ArrayList<Coordinate> canExplore(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted) {
		ArrayList<Coordinate> canExplore = new ArrayList<>();
		for (Coordinate pos : temp.keySet()) {
			if(hasAvailableRoad(pos, temp.get(pos),visted) != null) {
				canExplore.add(hasAvailableRoad(pos, temp.get(pos),visted));
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
	
	public ArrayList<Coordinate> getCanExplore(){
		return canExplore;
	}
	
	
//	
//	public Coordinate backToSafePoint(WorldSpatial.Direction orientation, Coordinate currPos) {
//		Coordinate goal = null;
//		Coordinate tempPos = currPos;
//		System.err.println("currPos: "+ tempPos);
//		while(goal == null) {
//			Coordinate nextPos = SafeExplore.getInstance().findBehindCoordinate(orientation, tempPos);
//			System.err.println("nextPos"+nextPos);
//			tempPos = nextPos;
//			if(MapManager.getInstance().getrealMap().get(nextPos).isType(Type.ROAD)) {
//				goal = nextPos;
//			}
//		}
//		return goal;
//	}
//	
//	public Coordinate escapetoOutPoint(WorldSpatial.Direction orientation, Coordinate currPos) {
//		Coordinate goal = null;
//		Coordinate tempPos = currPos;
//		while(goal == null) {
//			Coordinate nextPos = SafeExplore.getInstance().findNextCoordinate(orientation, tempPos);
//			tempPos = nextPos;
//			System.out.println("nextPos: "+ nextPos);
//			if(MapManager.getInstance().getrealMap().get(nextPos).isType(Type.ROAD)) {
//				goal = nextPos;
//			}
//		}
//		System.err.println("escapte point: " + goal);
//		return goal;
//	}	
	

	
}
