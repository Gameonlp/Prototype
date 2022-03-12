package com.tilebased.game.units.weapons;

import com.tilebased.game.RNG;
import com.tilebased.game.player.Player;
import com.tilebased.game.units.Selector;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.SoundFunction;

import java.util.Random;

public abstract class Weapon {
    private final int minDistance;
    private final int maxDistance;

    public Weapon(int minDistance, int maxDistance){
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public abstract void dealDamage(Unit user, Unit target, RNG random);

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public abstract SoundFunction getAttackSound();

    public abstract void destroy();

    public abstract Selector target(Player owner);
}
