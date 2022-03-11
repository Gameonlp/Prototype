package com.mygdx.game;

public abstract class Animation implements MessageQueue.Observer {
    protected long startTime;

    public Animation(long startTime){
        this.startTime = startTime;
    }

    public abstract void play(long time);

    public abstract boolean isDone(long time);
}
