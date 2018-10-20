package mycontroller;

import controller.CarController;
import tiles.HealthTrap;
import tiles.MapTile;
import utilities.Coordinate;

import java.util.*;

import com.badlogic.gdx.graphics.g3d.particles.ParticleSorter.Distance;

/*
 * static helper methods such of DFS, BFS, ManhatonDistance
 */

public class Search {

    public static ArrayList DFS(Coordinate start) {
        ArrayList<Coordinate> visited = new ArrayList<Coordinate>();
        Stack<Coordinate> dfStack = new Stack<Coordinate>();
        MapManager map = MapManager.getInstance();

        dfStack.push(start);
        visited.add(start);
        while (!dfStack.isEmpty()) {
            Coordinate pos = dfStack.pop();
            for (Coordinate key: map.getSuccessors(pos).keySet()) {
                if (map.getSuccessors(pos).get(key) != null && !visited.contains(key)) {
                    dfStack.push(key);
                    visited.add(key);
                }
            }
        }
        return visited;
    }

    public static ArrayList BFS_findPathToPos(Coordinate startPos, Coordinate goalPos) {
        ArrayList<Coordinate> visited = new ArrayList<Coordinate>();
        HashMap<Coordinate, ArrayList> allPath = new HashMap<Coordinate, ArrayList>();


        Queue<Coordinate> bfsQueue = new LinkedList<Coordinate>();
        MapManager map = MapManager.getInstance();

        bfsQueue.offer(startPos);
        visited.add(startPos);

        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(startPos);
        allPath.put(startPos,path);

        while (!bfsQueue.isEmpty()) {
            Coordinate pos = bfsQueue.poll();
            for (Coordinate key: map.getSuccessors(pos).keySet()) {
                path = new ArrayList<Coordinate>(allPath.get(pos));

                if (map.getSuccessors(pos).get(key) != null && !visited.contains(key)) {
                    bfsQueue.offer(key);
                    visited.add(key);
                    path.add(key);
                    allPath.put(key,path);

                    if (key.equals(goalPos)){
                        return allPath.get(key);
                    }
                }
            }
        }
        return null;
    }

    public static ArrayList uniCostSearch(Coordinate startPos, Coordinate goalPos) {
        int lavaCost = 2;
        int grassCost = 3;
        int roadCost = 1;


        ArrayList<Coordinate> visited = new ArrayList<Coordinate>();
        HashMap<Coordinate, ArrayList> allPath = new HashMap<Coordinate, ArrayList>();
        HashMap<Coordinate, Integer> pathCost = new HashMap<Coordinate, Integer>();
        HashMap<Coordinate,MapTile> realMap = MapManager.getInstance().getrealMap();

        Queue<Coordinate> priQueue = new LinkedList<Coordinate>();
        MapManager map = MapManager.getInstance();

        priQueue.offer(startPos);
        visited.add(startPos);

        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(startPos);
        allPath.put(startPos,path);
        pathCost.put(startPos,0);

        while (!priQueue.isEmpty()) {
            Coordinate pos = priQueue.poll();
            for (Coordinate key: map.getSuccessors(pos).keySet()) {
                path = new ArrayList<Coordinate>(allPath.get(pos));

                if (map.getSuccessors(pos).get(key) != null && !visited.contains(key)) {
                    priQueue.offer(key);
                    visited.add(key);
                    path.add(key);
                    allPath.put(key,path);

                    int pointCost = 0;
                    MapTile pointTile = realMap.get(key);
                    switch(pointTile.getType()){
                        case TRAP:

                            break;
                        case ROAD:

                            break;
                        default:
                            break;
                    }




                    if (key.equals(goalPos)){
                        return allPath.get(key);
                    }
                }
            }
        }
        return null;
    }








    
    public static int manhatonDistance(Coordinate x, Coordinate y) {
    	int mdistance = Math.abs(x.x - y.x) + Math.abs(x.y-y.y);
    	return mdistance;
    }


    public static ArrayList BFS_findPathToCloestH(Coordinate startPos) {
        ArrayList<Coordinate> visited = new ArrayList<Coordinate>();
        HashMap<Coordinate, ArrayList> allPath = new HashMap<Coordinate, ArrayList>();


        Queue<Coordinate> bfsQueue = new LinkedList<Coordinate>();
        MapManager map = MapManager.getInstance();

        bfsQueue.offer(startPos);
        visited.add(startPos);

        ArrayList<Coordinate> path = new ArrayList<Coordinate>();
        path.add(startPos);
        allPath.put(startPos,path);

        while (!bfsQueue.isEmpty()) {
            Coordinate pos = bfsQueue.poll();
            for (Coordinate key: map.getSuccessors(pos).keySet()) {
                path = new ArrayList<Coordinate>(allPath.get(pos));

                if (map.getSuccessors(pos).get(key) != null && !visited.contains(key)) {
                    bfsQueue.offer(key);
                    visited.add(key);
                    path.add(key);
                    allPath.put(key,path);


                    HashMap<Coordinate,MapTile> realMap = map.getrealMap();
                    MapTile tile = realMap.get(key);
                    if (tile instanceof HealthTrap){
                        return allPath.get(key);
                    }
                }
            }
        }
        return null;
    }



}
