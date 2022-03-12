package com.tilebased.game.player.aiplayer.strategy.plan;

import com.tilebased.game.units.Unit;

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
