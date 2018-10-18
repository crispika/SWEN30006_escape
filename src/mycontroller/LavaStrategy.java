package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;

public class LavaStrategy extends TrapStrategy{
	
	private Coordinate escapePoint;
	private LinkedList<Coordinate> keyPos = new LinkedList<>();
	private boolean inFire = false;
	private ArrayList<Coordinate> localAllunExplore = new ArrayList<>();
	
	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted) {
		// TODO Auto-generated method stub
		inFire = false;
		keyPos.clear();
		keyPos.addAll(lavaKey(temp,visted));
		
		System.out.println("KeyPos After: " + keyPos);
		
		ArrayList<Coordinate> canExplore = canExplore(temp,visted);
		
		if(keyPos.size() == 0) {
			Coordinate currGoal = randomPick(canExplore);
			System.err.println(currGoal);
			System.out.println("canExplore: "+canExplore);
			return currGoal;
		}
		else{
			Coordinate currGoal = keyPos.pollLast();
			inFire = true;
			System.err.println("!!! infire has been setted");
			return currGoal;
		}
	}
	
	public boolean getInfire() {
		return inFire;
	}
	
	public LinkedList<Coordinate> lavaKey(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted){
		
		LinkedList<Coordinate> keyPos = new LinkedList<>();
		for (Coordinate pos: temp.keySet()) {
			int keyNo = ((LavaTrap)temp.get(pos)).getKey();
			if( keyNo > 0 && MapManager.getInstance().isReachable(pos)) {
				keyPos.add(pos);
			}
		}
		ArrayList<Coordinate> canExplore = canExplore(temp, visted);
		Coordinate outsidePos = randomPick(canExplore);
		System.err.println("----------------escapePoint setted to-----------");
		escapePoint = outsidePos;
		System.err.println(escapePoint);
		System.err.println("-------------------");
		Coordinate outKey = new Coordinate(-9999,-9999);
		int nearest = 99999;
		for (Coordinate keypos: keyPos) {
			int mDistance = Math.abs(keypos.x - outsidePos.x) + Math.abs(keypos.y - outsidePos.y);
			if(mDistance < nearest){
				nearest = mDistance;
				outKey = keypos;
			}
		}
		keyPos.remove(outKey);
		keyPos.addLast(outKey);
		System.err.println("key array: "+ keyPos);
		return keyPos;
		
	}
	
	public Coordinate getescapePoint() {
		return escapePoint;
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
