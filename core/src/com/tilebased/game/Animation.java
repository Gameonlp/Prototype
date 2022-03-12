package com.tilebased.game;

public abstract class Animation{
    protected long startTime;

    public Animation(long startTime){
        this.startTime = startTime;
    }

    public abstract void play(long time);

    public abstract boolean isDone(long time);
}
