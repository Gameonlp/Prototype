package com.mygdx.game.player.aiplayer;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.Command;
import com.mygdx.game.GameMap;
import com.mygdx.game.Point;
import com.mygdx.game.Range;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.aiplayer.strategy.Strategy;
import com.mygdx.game.units.Unit;

import java.util.*;

public class AIPlayer extends Player {
    private final Strategy strategy;

    public AIPlayer(Color playerColor, Strategy strategy) {
        super(playerColor, PlayerType.AI);
        this.strategy = strategy;
    }

    public List<Command> handleTurn(GameMap map, Map<Point, Unit> unitPositions){
        return strategy.handleTurn(this, map,unitPositions);
    }
}
