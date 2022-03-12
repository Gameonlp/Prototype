package com.tilebased.game.units.weapons;

import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Timer;
import com.tilebased.game.RNG;
import com.tilebased.game.TileBased;
import com.tilebased.game.player.Player;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.SoundResource;
import com.tilebased.game.units.Selector;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.SoundFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Bow extends Weapon{
    private final int baseDamage;
    private final int damageRange;
    private final Random random = new Random();
    private final List<SoundResource> bowSoundResources;
    private final List<SoundResource> arrowSoundResources;

    public Bow(int baseDamage, int damageRange){
        super(1, 3);
        this.baseDamage = baseDamage;
        this.damageRange = damageRange;
        bowSoundResources = new LinkedList<>();
        for (int i = 1; i <= 3; i++) {
            try {
                bowSoundResources.add(ResourceManager.getInstance().getSound("sounds/Bow" + i + ".mp3"));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
        arrowSoundResources = new LinkedList<>();
        for (int i = 1; i <= 2; i++) {
            try {
                arrowSoundResources.add(ResourceManager.getInstance().getSound("sounds/Arrow" + i + ".mp3"));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
    }

    @Override
    public void dealDamage(Unit user, Unit target, RNG random) {
        target.reduceHealth(baseDamage + random.nextInt(damageRange + 1, "Calculate damage Range for " + user));
    }

    public SoundFunction getAttackSound(){
        return () -> {
            try
            {
                bowSoundResources.get(bowSoundResources.size() - 1 - random.nextInt(bowSoundResources.size())).getResource().play();
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        arrowSoundResources.get(arrowSoundResources.size() - 1 - random.nextInt(arrowSoundResources.size())).getResource().play();
                    }
                }, 0.1f);
            } catch(
                    IndexOutOfBoundsException e)

            {
                // Asset did not exist
            }
        };
    }

    @Override
    public void destroy() {
        for (SoundResource soundResource : bowSoundResources) {
            soundResource.dispose();
        }
        for (SoundResource soundResource : arrowSoundResources) {
            soundResource.dispose();
        }
    }

    @Override
    public Selector target(Player owner) {
        return target -> target.getOwner() != owner;
    }
}
