package com.tilebased.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class ContextMenu {

    private final int x;
    private final int y;
    private final SpriteBatch batch;
    private final Texture[] textures;

    public ContextMenu(int x, int y, SpriteBatch batch, Texture... textures){
        this.x = Math.min(x, Gdx.graphics.getWidth() - 100);
        this.y = Math.min(y, Gdx.graphics.getHeight() - 50 * textures.length);
        this.batch = batch;
        this.textures = textures;
    }

    public void draw() {
        for (int i = 0; i < textures.length; i++) {
            Texture texture = textures[i];
            batch.draw(texture, x, textures.length * 50 + y - 50 * (i + 1));
        }
    }

    public int getClickedButton(int x, int y){
        for (int i = 0; i < textures.length; i++) {
            if (y > this.y && y <= this.y + 50 * (i + 1) && x > this.x && x <= this.x + 100){
                return textures.length - (i + 1);
            }
        }
        return -1;
    }

    public abstract void clickMenu(int x, int y);
}
