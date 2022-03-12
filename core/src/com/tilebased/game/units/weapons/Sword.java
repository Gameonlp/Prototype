package com.tilebased.game.units.weapons;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.tilebased.game.RNG;
import com.tilebased.game.player.Player;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.SoundResource;
import com.tilebased.game.units.Selector;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.SoundFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sword extends Weapon{
    private final int baseDamage;
    private final Random random = new Random();
    private final List<SoundResource> soundResources;
    private final int damageRange;

    public Sword(int baseDamage, int damageRange){
        super(1, 1);
        this.baseDamage = baseDamage;
        this.damageRange = damageRange;
        soundResources = new LinkedList<>();
        for (int i = 1; i <= 7; i++) {
            try {
                soundResources.add(ResourceManager.getInstance().getSound("sounds/Sword" + i + ".mp3"));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
    }

    @Override
    public void dealDamage(Unit user, Unit target, RNG random) {
        target.reduceHealth(baseDamage + random.nextInt(damageRange + 1, "Calculate damage Range for " + user));
    }

    @Override
    public SoundFunction getAttackSound() {
        return () -> {
            try {
                soundResources.get(soundResources.size() - 1 - random.nextInt(soundResources.size())).getResource().play();
            } catch (IndexOutOfBoundsException e){
                // Asset did not exist
            }
        };
    }

    @Override
    public void destroy() {
        for (SoundResource soundResource : soundResources) {
            soundResource.dispose();
        }
    }

    @Override
    public Selector target(Player owner) {
        return target -> target.getOwner() != owner;
    }
}
