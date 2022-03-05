package com.mygdx.game.player.aiplayer.strategy;

import com.mygdx.game.Command;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.player.Player;
import com.mygdx.game.units.Unit;

import java.util.List;
import java.util.Map;

public interface Strategy {
    public List<Command> handleTurn(Player owner, GameMap map, Map<Point, Unit> unitPositions);
}
