package com.mygdx.game.player.aiplayer.strategy.plan;

import com.mygdx.game.util.Point;
import com.mygdx.game.units.Unit;

public class MoveStep extends Step{
    public Unit toMove;
    public Point moveTo;

    public MoveStep(Unit toMove, Point moveTo) {
        super("move");
        this.toMove = toMove;
        this.moveTo = moveTo;
    }

    @Override
    public String toString() {
        return "MoveStep{" +
                "toMove=" + toMove +
                ", moveTo=" + moveTo +
                '}';
    }
}
