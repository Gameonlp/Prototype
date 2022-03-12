package com.tilebased.game.player.aiplayer.strategy.plan;

import com.tilebased.game.Path;
import com.tilebased.game.util.Point;
import com.tilebased.game.units.Unit;

public class MoveStep extends Step{
    public Unit toMove;
    public Point moveTo;
    public Path moveAlong;

    public MoveStep(Unit toMove, Point moveTo, Path moveAlong) {
        super("move");
        this.toMove = toMove;
        this.moveTo = moveTo;
        this.moveAlong = moveAlong;
    }

    @Override
    public String toString() {
        return "MoveStep{" +
                "toMove=" + toMove +
                ", moveTo=" + moveTo +
                '}';
    }
}
