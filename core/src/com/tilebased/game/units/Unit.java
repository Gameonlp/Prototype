package com.tilebased.game.units;

import com.badlogic.gdx.graphics.Texture;
import com.tilebased.game.RNG;
import com.tilebased.game.resource.ResourceManager;
import com.tilebased.game.resource.TextureResource;
import com.tilebased.game.logic.Command;
import com.tilebased.game.player.Player;
import com.tilebased.game.util.Point;
import com.tilebased.game.logic.UndoableCommand;
import com.tilebased.game.units.weapons.Weapon;

import java.util.Random;

public abstract class Unit {
    private boolean hasAttacked;
    private Player owner;
    private TextureResource textureResource;
    private int healthPoints;
    private int maxHealthPoints;
    private int movePoints;
    private int maxMovePoints;
    private int positionX;
    private int positionY;
    private boolean canFly;
    private boolean canWalk;
    private boolean canSwim;
    private MoveCost costs;
    private Weapon weapon;
    private boolean draw;

    public Unit(Player owner, String texturePath, int movePoints, Weapon weapon, int positionX, int positionY, boolean canWalk, boolean canFly, boolean canSwim) {
        this(owner, texturePath, 10, movePoints, weapon, positionX, positionY, canWalk, canFly, canSwim);
    }

    public Unit(Player owner, String texturePath, int healthPoints, int movePoints, Weapon weapon, int positionX, int positionY, boolean canWalk, boolean canFly, boolean canSwim) {
        this(owner, texturePath, healthPoints, movePoints, weapon, positionX, positionY, canWalk, canFly, canSwim, x -> 1);
    }

    public Unit(Player owner, String texturePath, int healthPoints, int movePoints, Weapon weapon, int positionX, int positionY, boolean canWalk, boolean canFly, boolean canSwim, MoveCost costs){
        this.owner = owner;
        this.textureResource = ResourceManager.getInstance().getTexture(texturePath);
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
        this.costs = costs;
        this.hasAttacked = false;
        this.draw = true;
    }

    public  UndoableCommand move(Point point){
        return move(point.x, point.y);
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
        hasAttacked = false;
    }

    public void destroy(){
        textureResource.dispose();
        weapon.destroy();
    }

    public Texture getTexture() {
        return textureResource.getResource();
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

    public int getMaxMovePoints() {
        return maxMovePoints;
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

    public Command dealDamage(Unit target, RNG random){
        Unit attacker = this;
        return () -> {
            if (target != null) {
                attacker.hasAttacked = true;
                weapon.dealDamage(attacker, target, random);
                attacker.movePoints = 0;
            }
        };
    }

    public Command revenge(Unit target, RNG random){
        Unit attacker = this;
        return () -> {
            if (target != null) {
                weapon.dealDamage(attacker, target, random);
            }
        };
    }

    public Player getOwner() {
        return owner;
    }

    public int getHealth() {
        return healthPoints;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "hasAttacked=" + hasAttacked +
                ", owner=" + owner +
                ", texture=" + textureResource +
                ", healthPoints=" + healthPoints +
                ", movePoints=" + movePoints +
                ", maxMovePoints=" + maxMovePoints +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", canFly=" + canFly +
                ", canWalk=" + canWalk +
                ", canSwim=" + canSwim +
                ", weapon=" + weapon +
                '}';
    }

    public int getMaxHealthPoints() {
        return maxHealthPoints;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }
}
