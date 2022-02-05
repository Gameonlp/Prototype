package com.mygdx.game.weapons;

import com.mygdx.game.UndoableCommand;
import com.mygdx.game.units.Unit;

public class Sword extends Weapon{
    private int baseDamage;

    public Sword(int baseDamage){
        super(1, 1);
        this.baseDamage = baseDamage;
    }

    @Override
    public void dealDamage(Unit user, Unit target) {
        target.reduceHealth(baseDamage);
    }
}
