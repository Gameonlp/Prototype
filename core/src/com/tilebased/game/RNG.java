package com.tilebased.game;

import com.badlogic.gdx.utils.Logger;

import java.util.Random;

public class RNG {
    private static final Logger LOGGER = new Logger(GameMap.class.getName());
    private final Random random;

    public RNG(){
        random = new Random();
    }

    public boolean nextBoolean(String reason) {
        LOGGER.debug(reason);
        return random.nextBoolean();
    }

    public double nextDouble(String reason) {
        LOGGER.debug(reason);
        return random.nextDouble();
    }

    public int nextInt(int bound, String reason) {
        LOGGER.debug(reason);
        return random.nextInt(bound);
    }

    public float nextFloat(String reason) {
        LOGGER.debug(reason);
        return random.nextFloat();
    }

    public int nextInt(String reason) {
        LOGGER.debug(reason);
        return random.nextInt();
    }

    public long nextLong(String reason) {
        LOGGER.debug(reason);
        return random.nextLong();
    }

    public void nextBytes(byte[] bytes, String reason) {
        LOGGER.debug(reason);
        random.nextBytes(bytes);
    }

    public synchronized void setSeed(long seed, String reason) {
        LOGGER.debug(reason);
        random.setSeed(seed);
    }
}
