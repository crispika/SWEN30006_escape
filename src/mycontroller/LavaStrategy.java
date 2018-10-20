package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;

/*
 * strategy when facing Lava
 * if health is enough, will try to catch 2 keys. 1st is the nearest key around the init explore point and 2nd is the nearest key around the escaping point to out this area
 * chooseGoal is for finding 1 key
 * catch2Keys is for catching 2 keys when health is enough
 */

public class LavaStrategy extends TrapStrategy{
	
	
	private LinkedList<Coordinate> keyPos = new LinkedList<>();
	private Coordinate furtherKey;
	private boolean inCatchKey;
	
	
	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted,float health) {
		// TODO Auto-generated method stub
		inFire = false;
		keyPos.clear();
		keyPos.addAll(lavaKey(temp,visted));
		
		canExplore = new ArrayList<>();
		canExplore.addAll(canExplore(temp,visted));
		
		if(keyPos.size() == 0) {
			if(canExplore.size() > 0) {
				Coordinate currGoal = randomPick(canExplore);
				//System.err.println(currGoal);
				//System.out.println("canExplore: "+canExplore);
				return currGoal;
			}
			return null;
		}
		else{
			Coordinate currGoal = keyPos.pollLast();
			inFire = true;
			//System.err.println("!!! infire has been setted");
			return currGoal;
		}
	}
	
	
	public Coordinate catch2Keys(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted) {
		inFire = false;
		inCatchKey = false;
		keyPos.clear();
		keyPos.addAll(lavaKey(temp,visted));
		
		canExplore = new ArrayList<>();
		canExplore.addAll(canExplore(temp,visted));
		
		if(keyPos.size() == 0) {
			if(canExplore.size() > 0) {
				Coordinate currGoal = randomPick(canExplore);
				//System.err.println(currGoal);
				//System.out.println("canExplore: "+canExplore);
				return currGoal;
			}
			return null;
		}
		else if(keyPos.size() == 1){
			Coordinate currGoal = keyPos.pollLast();
			inFire = true;
			//System.err.println("!!! infire has been setted");
			return currGoal;
		}
		else {
			//System.err.println("ALL KEY pos are: " + keyPos);
			Coordinate currGoal = keyPos.pollFirst();
			furtherKey = keyPos.pollLast();
			inCatchKey = true;
			//System.err.println("!!! inCatchkey has been setted");
			return currGoal;
		}
	}
	
	public Coordinate getFurtherKey() {
		return furtherKey;
	}
	
	public boolean getInCatchKey() {
		return inCatchKey;
	}
	
	public LinkedList<Coordinate> lavaKey(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted){
		
		LinkedList<Coordinate> keyPos = new LinkedList<>();
		for (Coordinate pos: temp.keySet()) {
			if(temp.get(pos) instanceof LavaTrap) {
				int keyNo = ((LavaTrap)temp.get(pos)).getKey();
				if( keyNo > 0 && MapManager.getInstance().isReachable(pos)) {
					keyPos.add(pos);
				}
			}
		}
		ArrayList<Coordinate> canExplore = canExplore(temp, visted);
		//System.out.println("canExplore - all escapePoint (lava): " + canExplore);
		Coordinate outsidePos = randomPick(canExplore);
		//Coordinate outsidePos = canExplore.get(2);
		escapePoint = outsidePos;
		//System.out.println("---------escapePoint setted to (lava)-----------");
		//System.out.println(escapePoint);
		//System.out.println("-------------------");
		Coordinate outKey = new Coordinate(-9999,-9999);
		Coordinate insideNearestKey = outsidePos;
		int nearest = 99999;
		int farest = 0;
		for (Coordinate keypos: keyPos) {
			int mDistance = Math.abs(keypos.x - outsidePos.x) + Math.abs(keypos.y - outsidePos.y);
			if(mDistance < nearest){
				nearest = mDistance;
				outKey = keypos;
			}
			if(mDistance > farest) {
				insideNearestKey = keypos;
				farest = mDistance;
			}
		}
		keyPos.remove(outKey);
		keyPos.addLast(outKey);
		keyPos.remove(insideNearestKey);
		keyPos.addFirst(insideNearestKey);
		
		if(keyPos.contains(new Coordinate(-9999,-9999))) {
			keyPos.remove(new Coordinate(-9999,-9999));
		}
		
		if(keyPos.contains(outsidePos)) {
			keyPos.remove(outsidePos);
		}
		
		if (keyPos.size() == 1 && keyPos.contains(new Coordinate(-9999,-9999)) || keyPos.size() == 1 && keyPos.contains(outsidePos)) {
			keyPos.clear();
		}
		//System.err.println("-------------got key array: "+ keyPos);
		return keyPos;
	}
	

	
	
	public Coordinate nearestSafePoint(ArrayList<Coordinate> canExplore, Coordinate currPos) {
		int nearest = 99999;
		Coordinate nearPos = new Coordinate(-9999,-9999);
		for(Coordinate pos: canExplore) {
			if((Math.abs(pos.x - currPos.x) + Math.abs(pos.y - currPos.y)) < nearest){
				nearest = Math.abs(pos.x - currPos.x) + Math.abs(pos.y - currPos.y);
				nearPos = pos;
			}
		}
		return nearPos;
	}



}
