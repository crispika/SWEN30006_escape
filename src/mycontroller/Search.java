package mycontroller;

import utilities.Coordinate;
import java.util.ArrayList;
import java.util.Stack;

public class Search {

    public static ArrayList DFS(Coordinate start) {
        ArrayList<Coordinate> reachable = new ArrayList<Coordinate>();
        Stack<Coordinate> dfStack = new Stack<Coordinate>();
        MapManager map = MapManager.getInstance();

        dfStack.push(start);
        reachable.add(start);
        while (!dfStack.isEmpty()) {
            Coordinate pos = dfStack.pop();
            for (Coordinate key: map.getSuccessors(pos).keySet()) {
                if (map.getSuccessors(pos).get(key) != null && !reachable.contains(key)) {
                    dfStack.push(key);
                    reachable.add(key);
                }
            }
        }
        return reachable;
    }

}
