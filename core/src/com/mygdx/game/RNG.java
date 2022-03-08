package com.mygdx.game;

import java.util.Random;

public class RNG {
    private static Random instance;

    public static Random getInstance() {
        if (instance == null)
            instance = new Random();
        return instance;
    }
}
