package com.tilebased.game;

import com.badlogic.gdx.graphics.Texture;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.TextureResource;

public class Tile {
    private final TextureResource textureResource;
    private final boolean isWalkable;
    private final boolean isFlyable;
    private final boolean isSwimmable;
    private final boolean isBuildable;

    public Tile(String texturePath, boolean isWalkable, boolean isFlyable, boolean isSwimmable, boolean isBuildable){
        this.textureResource = ResourceManager.getInstance().getTexture(texturePath);
        this.isWalkable = isWalkable;
        this.isFlyable = isFlyable;
        this.isSwimmable = isSwimmable;
        this.isBuildable = isBuildable;
    }

    public boolean isSwimmable() {
        return isSwimmable;
    }

    public boolean isFlyable() {
        return isFlyable;
    }

    public boolean isWalkable() {
        return isWalkable;
    }

    public Texture getTexture() {
        return textureResource.getResource();
    }

    public boolean isBuildable() {
        return isBuildable;
    }

    public void destroy() {
        textureResource.dispose();
    }
}
