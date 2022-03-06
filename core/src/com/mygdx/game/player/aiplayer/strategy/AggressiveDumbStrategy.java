package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.Command;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.Range;
import com.mygdx.game.player.Player;
import com.mygdx.game.units.Unit;

import java.util.*;

public class AggressiveDumbStrategy implements Strategy{
    private static class PointDistanceTuple implements Comparable<PointDistanceTuple> {
        private final Point point;
        private final int distance;

        public PointDistanceTuple(Point point, int distance){
            this.point = point;
            this.distance = distance;
        }

        @Override
        public int compareTo(PointDistanceTuple other) {
            return this.distance - other.distance;
        }
    }
    @Override
    public List<Command> handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions){
        List<Command> commands = new LinkedList<>();
        List<Unit> myUnits = new LinkedList<>();
        List<Point> enemyUnits = new LinkedList<>();
        for (Unit unit : map.getUnits()) {
            if (unit.getOwner() == owner) {
                myUnits.add(unit);
            }
        }
        for (Unit unit : map.getUnits()) {
            if (unit.getOwner() != owner) {
                enemyUnits.add(new Point(unit));
            }
        }
        List<Point> taken = new LinkedList<>();
        Map<Unit, Point> newPositions = new HashMap<>();
        for (Unit unit : myUnits){
            Range range = new Range(map, unitPositions, unit);
            List<Point> reachable = new LinkedList<>();
            for (int x = -unit.getMovePoints(); x < unit.getMovePoints(); x++){
                for (int y = -unit.getMovePoints(); y < unit.getMovePoints(); y++){
                    int posX = unit.getPositionX() + x;
                    int posY = unit.getPositionY() + y;
                    int distance = range.getDistance(posX, posY);
                    System.out.println(distance);
                    if (distance >= 0 && distance < Integer.MAX_VALUE){
                        reachable.add(new Point(posX, posY));
                    }
                }
            }
            reachable.removeAll(taken);
            List<PointDistanceTuple> attackDistance = new LinkedList<>();
            for (Point point : reachable){
                List<Integer> distances = new LinkedList<>();
                Range attackable = new Range(map, unitPositions, point, unit, true);
                for (Point enemy : enemyUnits){
                    distances.add(Math.abs(attackable.getDistance(enemy)));
                }
                attackDistance.add(new PointDistanceTuple(point ,Collections.min(distances)));
            }
            attackDistance.sort(null);
            taken.add(attackDistance.get(0).point);
            newPositions.put(unit, attackDistance.get(0).point);
            commands.add(unit.move(attackDistance.get(0).point));
        }
        for (Unit myUnit : myUnits) {
            Range attackable = new Range(map, unitPositions, newPositions.get(myUnit), myUnit, true);
            for (Point point : enemyUnits){
                int distance = attackable.getDistance(point);
                if (distance >= 0 && distance < Integer.MAX_VALUE){
                    commands.add(myUnit.dealDamage(unitPositions.get(point)));
                    break;
                }
            }
        }
        return commands;
    }
}
