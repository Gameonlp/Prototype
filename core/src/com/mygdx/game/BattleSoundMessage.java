package com.mygdx.game;

import com.mygdx.game.units.Unit;
import com.mygdx.game.util.SoundFunction;

public class BattleSoundMessage extends Message{
    private final SoundFunction soundFunction;
    private final Unit attacker;
    private final Unit attacked;

    public BattleSoundMessage(SoundFunction soundFunction, Unit attacker, Unit attacked) {
        super("BattleSound");
        this.soundFunction = soundFunction;
        this.attacker = attacker;
        this.attacked = attacked;
    }

    public Unit getAttacker() {
        return attacker;
    }

    public Unit getAttacked() {
        return attacked;
    }

    public SoundFunction getSoundFunction() {
        return soundFunction;
    }
}
