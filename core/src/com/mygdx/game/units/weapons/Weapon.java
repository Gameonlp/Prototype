package com.mygdx.game.units.weapons;

import com.mygdx.game.player.Player;
import com.mygdx.game.units.Selector;
import com.mygdx.game.units.Unit;

public abstract class Weapon {
    private final int minDistance;
    private final int maxDistance;

    public Weapon(int minDistance, int maxDistance){
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public abstract void dealDamage(Unit user, Unit target);

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public abstract void destroy();

    public abstract Selector target(Player owner);
}
