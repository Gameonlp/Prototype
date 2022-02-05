package com.mygdx.game.units;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.UndoableCommand;
import com.mygdx.game.weapons.Weapon;

public class Unit {
    private int owner;
    private Texture texture;
    private int healthPoints;
    private int maxHealthPoints;
    private int movePoints;
    private int maxMovePoints;
    private int positionX;
    private int positionY;
    private boolean canFly;
    private boolean canWalk;
    private boolean canSwim;
    private Weapon weapon;

    public Unit(int owner, String texturePath, int healthPoints, int movePoints, Weapon weapon, int positionX, int positionY, boolean canWalk, boolean canFly, boolean canSwim){
        this.owner = owner;
        this.texture = new Texture(texturePath);
        this.healthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.movePoints = movePoints;
        this.maxMovePoints = movePoints;
        this.weapon = weapon;
        this.positionX = positionX;
        this.positionY = positionY;
        this.canFly = canFly;
        this.canWalk = canWalk;
        this.canSwim = canSwim;
    }

    public UndoableCommand move(int x, int y){
        return new UndoableCommand() {
            int points;
            int oldX, oldY;

            @Override
            public void execute() {
                points = Math.abs(positionX - x) + Math.abs(positionY - y);
                movePoints -= points;
                oldY = positionY;
                positionY = y;
                oldX = positionX;
                positionX = x;
            }

            @Override
            public void undo() {
                movePoints += points;
                positionX = oldX;
                positionY = oldY;
            }
        };
    }

    public void endTurn(){
        movePoints = maxMovePoints;
    }

    public void destroy(){
        texture.dispose();
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isFlying() {
        return canFly;
    }

    public boolean isWalking() {
        return canWalk;
    }

    public boolean isSwimming() {
        return canSwim;
    }

    public int getMovePoints() {
        return movePoints;
    }

    public int getPositionX(){
        return positionX;
    }

    public int getPositionY(){
        return positionY;
    }

    public void reduceHealth(int damage) {
        healthPoints -= damage;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int getOwner() {
        return owner;
    }

    public int getHealth() {
        return healthPoints;
    }
}
