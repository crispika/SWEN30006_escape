package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;

public class GrassStrategy extends TrapStrategy{
	
	private ArrayList<Coordinate> localAllunExplore = new ArrayList<>();
	
	
	@Override
	public Coordinate chooseGoal(HashMap<Coordinate, MapTile> temp, ArrayList<Coordinate> visted) {
		// TODO Auto-generated method stub
		ArrayList<Coordinate> canExplore = canExplore(temp,visted);
		
		
		if(canExplore.size()>0) {
			Coordinate currGoal = randomPick(canExplore);
			return currGoal;
		}
		return null;
	}

}
