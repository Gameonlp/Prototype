package com.tilebased.game.player.aiplayer.strategy.plan;

import java.util.Map;

public abstract class Step {
    private final String type;

    public Step(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Step{" +
                "type='" + type + '\'' +
                '}';
    }
}
