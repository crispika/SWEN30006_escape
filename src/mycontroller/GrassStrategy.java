package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class GrassStrategy extends TrapStrategy{
	/*
	 * strategy when facing grassTrap
	 */
	
	
	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted,float health) {
		// TODO Auto-generated method stub
		canExplore = new ArrayList<>();
		canExplore.addAll(canExplore(temp,visted));
		
		
		if(canExplore.size()>0) {
			Coordinate currGoal = randomPick(canExplore);
			return currGoal;
		}
		return null;
	}
	
	

}
