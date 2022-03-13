package com.tilebased.game;

import com.badlogic.gdx.graphics.Texture;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.TextureResource;

public class Tile {
    public static class TileData{
        private final String texturePath;
        private final boolean isWalkable;
        private final boolean isFlyable;
        private final boolean isSwimmable;
        private final boolean isBuildable;

        public TileData(String texturePath, boolean isWalkable, boolean isFlyable, boolean isSwimmable, boolean isBuildable) {
            this.texturePath = texturePath;
            this.isWalkable = isWalkable;
            this.isFlyable = isFlyable;
            this.isSwimmable = isSwimmable;
            this.isBuildable = isBuildable;
        }
    }

    private final TextureResource textureResource;
    private final boolean isWalkable;
    private final boolean isFlyable;
    private final boolean isSwimmable;
    private final boolean isBuildable;

    public Tile(TileData tileData) {
        this(tileData.texturePath, tileData.isWalkable, tileData.isFlyable, tileData.isSwimmable, tileData.isBuildable);
    }

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

    public Texture getTexture(long time) {
        return textureResource.getResource();
    }

    public boolean isBuildable() {
        return isBuildable;
    }

    public void destroy() {
        textureResource.dispose();
    }
}
