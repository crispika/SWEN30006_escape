package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;

public class LavaStrategy extends TrapStrategy{
	
	
	private LinkedList<Coordinate> keyPos = new LinkedList<>();
	
	
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
				System.err.println(currGoal);
				System.out.println("canExplore: "+canExplore);
				return currGoal;
			}
			return null;
		}
		else{
			Coordinate currGoal = keyPos.pollLast();
			inFire = true;
			System.err.println("!!! infire has been setted");
			return currGoal;
		}
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
		System.out.println("canExplore - all escapePoint (lava): " + canExplore);
		Coordinate outsidePos = randomPick(canExplore);
		//Coordinate outsidePos = canExplore.get(2);
		escapePoint = outsidePos;
		System.out.println("---------escapePoint setted to (lava)-----------");
		System.out.println(escapePoint);
		System.out.println("-------------------");
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
		if (keyPos.size() == 1 && keyPos.contains(new Coordinate(-9999,-9999))) {
			keyPos.clear();
		}
		System.err.println("-------------got key array: "+ keyPos);
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
