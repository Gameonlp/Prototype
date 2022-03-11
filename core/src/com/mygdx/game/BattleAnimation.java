package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.units.Unit;

public class BattleAnimation extends Animation{
    private final Unit attacker;
    private final Unit attacked;
    private boolean revenge;
    private boolean noRevenge;
    private boolean soundPlayed;
    private final SpriteBatch batch;

    public BattleAnimation(long startTime, Unit attacker, Unit attacked, SpriteBatch batch){
        super(startTime);
        this.attacker = attacker;
        this.attacked = attacked;
        this.batch = batch;
    }

    @Override
    public void handleMessage(Message message) {
    }

    @Override
    public void play(long time) {
        if (!revenge && !soundPlayed) {
            soundPlayed = true;
            attacker.getWeapon().getAttackSound().play();
        }
        if (revenge && !soundPlayed) {
            soundPlayed = true;
            attacked.getWeapon().getAttackSound().play();
        }
    }

    public void revenge(){
        revenge = true;
        soundPlayed = false;
    }

    public void noRevenge(){
        noRevenge = true;
    }

    @Override
    public boolean isDone(long time) {
        return time - startTime > 1000 || noRevenge;
    }

    public boolean firstDone(long time){
        return time - startTime > 500;
    }

    public boolean isRevenging(){
        return revenge;
    }
}
