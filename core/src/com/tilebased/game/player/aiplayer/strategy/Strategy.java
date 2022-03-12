package com.tilebased.game.player.aiplayer.strategy;

import com.tilebased.game.GameMap;
import com.tilebased.game.util.Point;
import com.tilebased.game.player.Player;
import com.tilebased.game.player.aiplayer.strategy.plan.Plan;
import com.tilebased.game.units.Unit;

import java.util.Map;

public interface Strategy {
    Plan handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions);
}
