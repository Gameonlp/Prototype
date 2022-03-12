package com.tilebased.game.player.aiplayer;

import com.badlogic.gdx.graphics.Color;
import com.tilebased.game.GameMap;
import com.tilebased.game.util.Point;
import com.tilebased.game.player.Player;
import com.tilebased.game.player.aiplayer.strategy.plan.Plan;
import com.tilebased.game.player.aiplayer.strategy.Strategy;
import com.tilebased.game.units.Unit;

import java.util.*;

public class AIPlayer extends Player {
    private final Strategy strategy;

    public AIPlayer(Color playerColor, Strategy strategy) {
        super(playerColor, PlayerType.AI);
        this.strategy = strategy;
    }

    public Plan handleTurn(GameMap map, Map<Point, Unit> unitPositions){
        return strategy.handleTurn(this, map,unitPositions);
    }
}
