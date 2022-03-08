package com.mygdx.game.units;

import com.mygdx.game.player.Player;
import com.mygdx.game.units.weapons.Sword;

public class Commander extends Unit{
    public Commander(Player owner, int positionX, int positionY) {
        super(owner, "textures/Commander.png", 5, new Sword(2, 3), positionX, positionY,true, false, false);
    }
}
