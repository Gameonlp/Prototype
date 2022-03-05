package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.Command;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.Range;
import com.mygdx.game.player.Player;
import com.mygdx.game.units.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomStrategy implements Strategy{
    @Override
    public List<Command> handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions){
        List<Command> commands = new LinkedList<>();
        List<Unit> myUnits = new LinkedList<>();
        for (Unit unit : map.getUnits()) {
            if (unit.getOwner() == owner) {
                myUnits.add(unit);
            }
        }
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
            Random random = new Random();
            if (reachable.size() > 0) {
                System.out.println(reachable);
                commands.add(unit.move(reachable.get(random.nextInt(reachable.size()))));
            }
        }
        return commands;
    }
}
