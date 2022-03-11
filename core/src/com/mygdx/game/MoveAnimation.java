package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.units.Unit;
import com.mygdx.game.util.Point;

import java.util.Arrays;

public class MoveAnimation extends Animation{
    private final Unit toMove;
    private final Point targetPoint;
    private Point[] path;
    private final SpriteBatch batch;

    public MoveAnimation(long startTime, Unit toMove, Point targetPoint, Point[] path, SpriteBatch batch){
        super(startTime);
        this.toMove = toMove;
        this.targetPoint = targetPoint;
        int i = 0;
        while (i < path.length) {
            if (path[i] == null)
                break;
            i++;
        }
        this.path = Arrays.copyOfRange(path,0, i);
        System.out.println(Arrays.toString(this.path));
        this.batch = batch;
    }

    @Override
    public void handleMessage(Message message) {
    }

    @Override
    public void play(long time) {
        long delta = time - startTime;
        long stepTime = 100 / path.length;
        for (int i = 0; i < path.length; i++) {
            if (stepTime * (i + 1) > delta){
                batch.setColor(toMove.getOwner().getPlayerColor());
                batch.draw(toMove.getTexture(), path[i].x * 64, path[i].y * 64, 64, 64);
                batch.setColor(Color.WHITE);
                break;
            }
        }
    }

    @Override
    public boolean isDone(long time) {
        return time - startTime > 100;
    }
}
