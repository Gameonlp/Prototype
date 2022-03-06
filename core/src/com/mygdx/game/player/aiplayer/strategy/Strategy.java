package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.aiplayer.strategy.plan.Plan;
import com.mygdx.game.units.Unit;

import java.util.Map;

public interface Strategy {
    Plan handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions);
}
