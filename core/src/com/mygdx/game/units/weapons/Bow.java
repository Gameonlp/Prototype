package com.mygdx.game.units.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.player.Player;
import com.mygdx.game.units.Selector;
import com.mygdx.game.units.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Bow extends Weapon{
    private int baseDamage;
    private final Random random = new Random();
    private List<Sound> bowSounds;
    private List<Sound> arrowSounds;

    public Bow(int baseDamage){
        super(1, 3);
        this.baseDamage = baseDamage;
        bowSounds = new LinkedList<>();
        for (int i = 1; i <= 3; i++) {
            try {
                bowSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/Bow" + i + ".mp3")));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
        arrowSounds = new LinkedList<>();
        for (int i = 1; i <= 2; i++) {
            try {
                arrowSounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/Arrow" + i + ".mp3")));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
    }

    @Override
    public void dealDamage(Unit user, Unit target) {
        target.reduceHealth(baseDamage);
        try {
            bowSounds.get(bowSounds.size() - 1 - random.nextInt(bowSounds.size())).play();
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    arrowSounds.get(arrowSounds.size() - 1 - random.nextInt(arrowSounds.size())).play();
                }
            }, 0.1f);
        } catch (IndexOutOfBoundsException e){
            // Asset did not exist
        }
    }

    @Override
    public void destroy() {
        for (Sound sound : bowSounds) {
            sound.dispose();
        }
        for (Sound sound : arrowSounds) {
            sound.dispose();
        }
    }

    @Override
    public Selector target(Player owner) {
        return target -> target.getOwner() != owner;
    }
}
