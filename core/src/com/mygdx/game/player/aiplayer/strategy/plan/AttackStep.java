package com.mygdx.game.player.aiplayer.strategy.plan;

import com.mygdx.game.Point;
import com.mygdx.game.units.Unit;

public class AttackStep extends Step{
    public Unit attacker;
    public Unit target;

    public AttackStep(Unit attacker, Unit target) {
        super("attack");
        this.attacker = attacker;
        this.target = target;
    }

    @Override
    public String toString() {
        return "AttackStep{" +
                "attacker=" + attacker +
                ", target=" + target +
                '}';
    }
}
