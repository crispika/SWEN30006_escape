package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.maps.Map;

import tiles.HealthTrap;
import tiles.MapTile;
import utilities.Coordinate;

public class HealthStrategy extends TrapStrategy{
	
	private boolean inHealth = false;
	private Coordinate futureGoal = new Coordinate(-1,-1);
	

	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted,float health) {
		// TODO Auto-generated method stub
		canExplore = new ArrayList<>();
		canExplore.addAll(canExplore(temp,visted));
		if(health < 100) {
			Coordinate currGoal = randomPickHealthPos(temp);
			futureGoal = randomPick(canExplore);
			System.out.println("-------------futureGoal setted to:" + futureGoal);
			inHealth = true;
			System.out.println("---------------in health setted to true--------------------");
			return currGoal;
		}
		else {
			if(canExplore.size()>0) {
				Coordinate currGoal = randomPick(canExplore);
				return currGoal;
			}
			return null;
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
	
}
