package com.tilebased.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tilebased.game.units.Unit;
import com.tilebased.game.util.Point;

public class MoveAnimation extends Animation{
    private final Unit toMove;
    private final Path path;
    private final SpriteBatch batch;

    public MoveAnimation(long startTime, Unit toMove, Path path, SpriteBatch batch){
        super(startTime);
        this.toMove = toMove;
        this.path = path;
        this.batch = batch;
    }

    @Override
    public void play(long time) {
        long delta = time - startTime;
        int length = path.getConcreteLength();
        long stepTime = 200 / (length - 1);
        for (int i = 0; i < length - 1; i++) {
            if (stepTime * (i + 1) > delta){
                float interpolationFactor = ((float) (delta - i * stepTime) / stepTime);
                batch.setColor(toMove.getOwner().getPlayerColor());
                batch.draw(toMove.getTexture(), path.get(i).x * 64 + (path.get(i + 1).x - path.get(i).x) * 64 * interpolationFactor,
                        path.get(i).y * 64 + (path.get(i + 1).y - path.get(i).y) * 64 * interpolationFactor, 64, 64);
                batch.setColor(Color.WHITE);
                break;
            }
        }
    }

    @Override
    public boolean isDone(long time) {
        return time - startTime > 200;
    }
}
