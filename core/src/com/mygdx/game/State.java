package com.mygdx.game;

public class State extends HierarchicalStateMachine {
    public State(String name) {
        super(name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
