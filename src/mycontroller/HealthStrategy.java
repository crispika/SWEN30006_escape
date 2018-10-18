package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.maps.Map;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import utilities.Coordinate;

public class HealthStrategy extends TrapStrategy{
	
	private boolean inHealth = false;
	private Coordinate futureGoal = new Coordinate(-1,-1);
	private LinkedList<Coordinate> keyPos = new LinkedList<>();
	

	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted,float health) {
		// TODO Auto-generated method stub
		inHealth = false;
		canExplore = new ArrayList<>();
		canExplore.addAll(canExplore(temp,visted));
		if(health < 100) {
			Coordinate currGoal = randomPickHealthPos(temp);
			System.err.println("-----------health point goal is setted to: " + currGoal);
			if(canExplore.size() > 0) {
				futureGoal = randomPick(canExplore);
			}
			System.err.println("-------------futureGoal setted to: " + futureGoal);
			inHealth = true;
			System.err.println("---------------in health setted to true--------------------");
			return currGoal;
		}
		else {
			inFire = false;
			keyPos.clear();
			keyPos.addAll(lavaKey(temp,visted));
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
	}
		
	
	public boolean getInHealth() {
		return inHealth;
	}
	
	public Coordinate getFutureGoal() {
		return futureGoal;
	}
	
	
	
	public Coordinate randomPickHealthPos(HashMap<Coordinate, MapTile> temp) {
		System.out.println("-------------temp: " +temp);
		Random random = new Random();
		ArrayList<Coordinate> keys = new ArrayList<>();
		for(Coordinate key: temp.keySet()) {
			if(temp.get(key) instanceof HealthTrap) {
				keys.add(key);
			}
		}
		int index = random.nextInt(keys.size());
		Coordinate pos = keys.get(index);
		return pos;
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
		System.out.println("canExplore - all escapePoint: " + canExplore);
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
		if (keyPos.size() == 1 && keyPos.contains(new Coordinate(-9999,-9999))) {
			keyPos.clear();
		}
		System.err.println("-------------got key array: "+ keyPos);
		return keyPos;
		
	}
	
}
