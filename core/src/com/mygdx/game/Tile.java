package com.mygdx.game;

public class Tile {
    String texturePath;
    boolean isWalkable;
    boolean isFlyable;
    boolean isSwimmable;
    boolean isBuildable;

    public Tile(String texturePath, boolean isWalkable, boolean isFlyable, boolean isSwimmable, boolean isBuildable){
        this.texturePath = texturePath;
        this.isWalkable = isWalkable;
        this.isFlyable = isFlyable;
        this.isSwimmable = isSwimmable;
        this.isBuildable = isBuildable;
    }
}
