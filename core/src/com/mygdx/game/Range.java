package com.mygdx.game;

import com.mygdx.game.player.Player;
import com.mygdx.game.units.Unit;

import java.util.*;

public class Range {
    private class Distance{
        private final int dist;
        private final int x;
        private final int y;

        Distance(int dist, int x, int y){
            this.dist = dist;
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Distance{" +
                    "dist=" + dist +
                    ", x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private final GameMap map;
    private Map<Point, Unit> units;
    private final int minDistance;
    private final int maxDistance;
    private final int x;
    private final int y;
    private final boolean canWalk;
    private final boolean canFly;
    private final boolean canSwim;
    private final boolean ignoreUnits;
    private Player owner;
    private int[] reachable;

    public Range(GameMap map, Map<Point, Unit> units, Unit unit){
        this(map, units, unit.getMovePoints(), unit.getPositionX(), unit.getPositionY(), unit.isWalking(), unit.isFlying(), unit.isSwimming(), unit.getOwner());
    }

    public Range(GameMap map, Map<Point, Unit> units, int maxDistance, int x, int y, boolean canWalk, boolean canFly, boolean canSwim, Player owner) {
        this(map, units, 1, maxDistance, x, y, canWalk, canFly, canSwim, false, owner);
    }

    public Range(GameMap map, Map<Point, Unit> units, int minDistance, int maxDistance, int x, int y, boolean canWalk, boolean canFly, boolean canSwim, boolean ignoreUnits) {
        this(map, units, minDistance, maxDistance, x, y, canWalk, canFly, canSwim, ignoreUnits, null);
    }

    public Range(GameMap map, Map<Point, Unit> units, int minDistance, int maxDistance, int x, int y, boolean canWalk, boolean canFly, boolean canSwim, boolean ignoreUnits, Player owner){
        this.map = map;
        this.units = units;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.x = x;
        this.y = y;
        this.canWalk = canWalk;
        this.canFly = canFly;
        this.canSwim = canSwim;
        this.ignoreUnits = ignoreUnits;
        this.owner = owner;
        this.reachable = calculateRange();
    }

    private int calculatePosition(int x, int y){
        int width = map.getWidth();
        return y * width + x;
    }

    private int[] calculateRange(){
        int height = map.getHeight();
        int width = map.getWidth();
        reachable = new int[height * width];
        Arrays.fill(reachable, Integer.MAX_VALUE);
        Queue<Distance> toCheck = new LinkedList<>();

        toCheck.add(new Distance(maxDistance, x, y));
        while(!toCheck.isEmpty()){
            Distance current = toCheck.remove();
            int position = calculatePosition(current.x, current.y);
            boolean inMap = current.x >= 0 && current.x < width
                    && current.y >= 0 && current.y < height;
            boolean canTraverse = ((map.getTile(current.x, current.y).isWalkable && canWalk)
                    || (map.getTile(current.x, current.y).isSwimmable && canSwim)
                    || (map.getTile(current.x, current.y).isFlyable && canFly));
            boolean avoidUnits = (ignoreUnits
                    || units.get(new Point(current.x, current.y)) == null)
                    || (units.get(new Point(current.x, current.y)) != null
                    && units.get(new Point(current.x, current.y)).getOwner() == owner);
            if(reachable[position] == Integer.MAX_VALUE
                    && inMap
                    && canTraverse
                    && avoidUnits) {
                reachable[position] = current.dist;
                toCheck.add(new Distance(current.dist - 1, current.x + 1, current.y));
                toCheck.add(new Distance(current.dist - 1, current.x - 1, current.y));
                toCheck.add(new Distance(current.dist - 1, current.x, current.y + 1));
                toCheck.add(new Distance(current.dist - 1, current.x, current.y - 1));
            }
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.abs(x - this.x) + Math.abs(y - this.y) < minDistance){
                    reachable[calculatePosition(x, y)] = Integer.MAX_VALUE;
                }
            }
        }
        if(!ignoreUnits) {
            for (Point point : units.keySet()) {
                reachable[calculatePosition(point.x, point.y)] = Integer.MAX_VALUE;
            }
        }
        return reachable;
    }

    public int getDistance(int x, int y){
        if (x < map.getWidth() && x >= 0 && y < map.getHeight() && y >= 0) {
            return reachable[calculatePosition(x, y)];
        }
        return Integer.MAX_VALUE;
    }
}
