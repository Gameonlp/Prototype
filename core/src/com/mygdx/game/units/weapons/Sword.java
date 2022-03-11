package com.mygdx.game.units.weapons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.game.BattleSoundMessage;
import com.mygdx.game.MessageQueue;
import com.mygdx.game.player.Player;
import com.mygdx.game.units.Selector;
import com.mygdx.game.units.Unit;
import com.mygdx.game.util.SoundFunction;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Sword extends Weapon{
    private int baseDamage;
    private final Random random = new Random();
    private List<Sound> sounds;
    private int damageRange;

    public Sword(int baseDamage, int damageRange){
        super(1, 1);
        this.baseDamage = baseDamage;
        this.damageRange = damageRange;
        sounds = new LinkedList<>();
        for (int i = 1; i <= 7; i++) {
            try {
                sounds.add(Gdx.audio.newSound(Gdx.files.internal("sounds/Sword" + i + ".mp3")));
            } catch (GdxRuntimeException e){
                // Asset does not exist
            }
        }
    }

    @Override
    public void dealDamage(Unit user, Unit target) {
        target.reduceHealth(baseDamage + random.nextInt(damageRange + 1));
    }

    @Override
    public SoundFunction getAttackSound() {
        return () -> {
            try {
                sounds.get(sounds.size() - 1 - random.nextInt(sounds.size())).play();
            } catch (IndexOutOfBoundsException e){
                // Asset did not exist
            }
        };
    }

    @Override
    public void destroy() {
        for (Sound sound : sounds) {
            sound.dispose();
        }
    }

    @Override
    public Selector target(Player owner) {
        return target -> target.getOwner() != owner;
    }
}
