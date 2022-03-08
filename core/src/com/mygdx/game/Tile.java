package com.mygdx.game;

public class Tile {
    private String texturePath;
    private boolean isWalkable;
    private boolean isFlyable;
    private boolean isSwimmable;
    private boolean isBuildable;

    public Tile(String texturePath, boolean isWalkable, boolean isFlyable, boolean isSwimmable, boolean isBuildable){
        this.texturePath = texturePath;
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

    public String getTexturePath() {
        return texturePath;
    }

    public boolean isBuildable() {
        return isBuildable;
    }
}
