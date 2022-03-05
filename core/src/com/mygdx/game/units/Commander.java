package com.mygdx.game.units;

import com.mygdx.game.player.Player;
import com.mygdx.game.weapons.Sword;

public class Commander extends Unit{
    public Commander(Player owner, int positionX, int positionY) {
        super(owner, "textures/Commander.png", 5, 5, new Sword(2), positionX, positionY,true, false, false);
    }
}
