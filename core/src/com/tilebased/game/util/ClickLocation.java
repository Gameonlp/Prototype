package com.tilebased.game.util;

import com.badlogic.gdx.Gdx;

public class ClickLocation {
    public boolean clickInBoundingBox(int[] lastClick, int left, int right, int top, int bottom){
        return lastClick[0] >= left && lastClick[0] <= right && lastClick[1] >= Gdx.graphics.getHeight() - top && lastClick[1] <= Gdx.graphics.getHeight() - bottom;
    }
}
