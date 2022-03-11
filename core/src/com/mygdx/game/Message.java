package com.mygdx.game;

public abstract class Message {
    private String type;

    public Message(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
