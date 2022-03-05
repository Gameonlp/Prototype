package com.mygdx.game.player;

import com.badlogic.gdx.graphics.Color;

public class Player {
    private final Color playerColor;
    private final PlayerType playerType;

    public Color getPlayerColor() {
        return playerColor;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public enum PlayerType{
        AI,
        HUMAN,
        NETWORK
    }
    public Player(Color playerColor, PlayerType playerType){
        this.playerColor = playerColor;
        this.playerType = playerType;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerColor=" + playerColor +
                ", playerType=" + playerType +
                '}';
    }
}
