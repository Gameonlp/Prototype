package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.Command;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.Range;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.aiplayer.strategy.plan.AttackStep;
import com.mygdx.game.player.aiplayer.strategy.plan.Condition;
import com.mygdx.game.player.aiplayer.strategy.plan.MoveStep;
import com.mygdx.game.player.aiplayer.strategy.plan.Plan;
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

    private Plan plan(List<Unit> units, Condition condition, Player owner, GameMap map, Map<Point, Unit> unitPositions){
        System.out.println(unitPositions);
        Plan plan = new Plan();
        if (units.isEmpty()){
            return plan;
        }
        Unit unit = units.get(0);
        List<Point> enemyUnits = new LinkedList<>();
        for (Point point : unitPositions.keySet()) {
            if (unitPositions.get(point).getOwner() != owner) {
                enemyUnits.add(point);
            }
        }
        Range range = new Range(map, unitPositions, unit);
        List<Point> reachable = new LinkedList<>();
        for (int x = -unit.getMovePoints(); x < unit.getMovePoints(); x++){
            for (int y = -unit.getMovePoints(); y < unit.getMovePoints(); y++){
                int posX = unit.getPositionX() + x;
                int posY = unit.getPositionY() + y;
                int distance = range.getDistance(posX, posY);
                if (distance >= 0 && distance < Integer.MAX_VALUE){
                    reachable.add(new Point(posX, posY));
                }
            }
        }
        List<PointDistanceTuple> attackDistance = new LinkedList<>();
        for (Point point : reachable){
            List<Integer> distances = new LinkedList<>();
            Range attackable = new Range(map, unitPositions, point, unit, true);
            for (Point enemy : enemyUnits){
                distances.add(Math.abs(attackable.getDistance(enemy)));
            }
            try {
                attackDistance.add(new PointDistanceTuple(point ,Collections.min(distances)));
            } catch (NoSuchElementException e){
                return plan;
            }
        }
        attackDistance.sort(null);
        Point newPosition = attackDistance.get(0).point;
        plan.setStep(new MoveStep(unit, attackDistance.get(0).point));
        Plan attackPlan = new Plan();
        Range attackable = new Range(map, unitPositions, newPosition, unit, true);
        List<Unit> restUnits = new LinkedList<>(units);
        restUnits.remove(0);
        for (Point point : enemyUnits){
            int distance = attackable.getDistance(point);
            if (distance >= 0 && distance < Integer.MAX_VALUE){
                attackPlan.setStep(new AttackStep(unit, unitPositions.get(point)));
                List<Plan> subPlans = new LinkedList<>();
                subPlans.add(this.plan(restUnits, () -> unit.getHealth() > 0 && unitPositions.get(point).getHealth() > 0,owner, map, unitPositions));
                Map<Point, Unit> iDieMap = new HashMap<>(unitPositions);
                iDieMap.remove(new Point(unit));
                subPlans.add(this.plan(restUnits, () -> unit.getHealth() <= 0 && unitPositions.get(point).getHealth() > 0,owner, map, iDieMap));
                Map<Point, Unit> theyDieMap = new HashMap<>(unitPositions);
                theyDieMap.remove(point);
                subPlans.add(this.plan(restUnits, () -> unit.getHealth() > 0 && unitPositions.get(point).getHealth() <= 0,owner, map, theyDieMap));
                Map<Point, Unit> bothDieMap = new HashMap<>(unitPositions);
                bothDieMap.remove(point);
                bothDieMap.remove(new Point(unit));
                subPlans.add(this.plan(restUnits, () -> unit.getHealth() <= 0 && unitPositions.get(point).getHealth() <= 0,owner, map, bothDieMap));
                attackPlan.addSubPlans(subPlans);
                plan.addSubPlans(new LinkedList<>(Collections.singleton(attackPlan)));
                return plan;
            }
        }
        Plan subPlan = this.plan(restUnits, () -> true, owner, map, unitPositions);
        plan.addSubPlans(new LinkedList<>(Collections.singleton(subPlan)));
        return plan;
    }

    @Override
    public Plan handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions){
        List<Unit> myUnits = new ArrayList<>();
        for (Unit unit : map.getUnits()) {
            if (unit.getOwner() == owner) {
                myUnits.add(unit);
            }
        }
        return plan(myUnits, () -> true, owner, map, unitPositions);
    }
}
