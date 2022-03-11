package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.GameMap;
import com.mygdx.game.util.Point;
import com.mygdx.game.util.Range;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.aiplayer.strategy.plan.MoveStep;
import com.mygdx.game.player.aiplayer.strategy.plan.NopStep;
import com.mygdx.game.player.aiplayer.strategy.plan.Plan;
import com.mygdx.game.units.Unit;

import java.util.*;

public class RandomStrategy implements Strategy{
    @Override
    public Plan handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions){
        Plan commands = new Plan();
        List<Unit> myUnits = new LinkedList<>();
        for (Unit unit : map.getUnits()) {
            if (unit.getOwner() == owner) {
                myUnits.add(unit);
            }
        }
        commands.setStep(new NopStep());
        Plan parent = commands;
        for (Unit unit : myUnits){
            Plan current = new Plan();
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
            Random random = new Random();
            if (reachable.size() > 0) {
                current.setStep(new MoveStep(unit, reachable.get(random.nextInt(reachable.size()))));
                parent.addSubPlans(new LinkedList<>(Collections.singleton(current)));
                parent = current;
            }
        }
        return commands;
    }
}
