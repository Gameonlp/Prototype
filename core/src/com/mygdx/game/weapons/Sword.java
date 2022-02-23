package com.mygdx.game.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.Selector;
import com.mygdx.game.UndoableCommand;
import com.mygdx.game.units.Unit;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sword extends Weapon{
    private int baseDamage;
    private final Random random = new Random();
    private List<Sound> sounds;

    public Sword(int baseDamage){
        super(1, 1);
        this.baseDamage = baseDamage;
        sounds = new LinkedList<>();
        for (int i = 1; i < 8; i++) {
            try {
                sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/Sword" + i + ".mp3")));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
    }

    @Override
    public void dealDamage(Unit user, Unit target) {
        target.reduceHealth(baseDamage);
        try {
            sounds.get(sounds.size() - random.nextInt(sounds.size() + 1)).play();
        } catch (IndexOutOfBoundsException e){
            // Asset did not exist
        }
    }

    @Override
    public void destroy() {
        for (Sound sound : sounds) {
            sound.dispose();
        }
    }

    @Override
    public Selector target(int owner) {
        return target -> target.getOwner() != owner;
    }
}
