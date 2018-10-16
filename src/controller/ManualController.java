package controller;

import java.util.Set;
import com.badlogic.gdx.Input;

import mycontroller.MapManager;
import world.Car;
import swen30006.driving.Simulation;
import tiles.LavaTrap;
import utilities.Coordinate;

// Manual Controls for the car
public class ManualController extends CarController {
	
	public ManualController(Car car){
		super(car);
		MapManager.getInstance().initialize(this);
	}
	
	public void update(){
		//System.out.println(MapManager.getInstance().getrealMap());
		for (Coordinate key : MapManager.getInstance().getrealMap().keySet()) {
			if(MapManager.getInstance().getrealMap().get(key) instanceof LavaTrap) {
				System.out.println("I found lava!!!!!!!!!!!!");
			}
		}
		Set<Integer> keys = Simulation.getKeys();
		Simulation.resetKeys();
		 //System.out.print("Get Keys: ");
         //System.out.println(keys);
        for (int k : keys){
		     switch (k){
		        case Input.Keys.B:
		        	applyBrake();
		            break;
		        case Input.Keys.UP:
		        	applyForwardAcceleration();
		            break;
		        case Input.Keys.DOWN:
		        	applyReverseAcceleration();
		        	break;
		        case Input.Keys.LEFT:
		        	turnLeft();
		        	break;
		        case Input.Keys.RIGHT:
		        	turnRight();
		        	break;
		        default:
		      }
		  }
	}
}
