package com.tilebased.game.units;

import com.tilebased.game.player.Player;
import com.tilebased.game.units.weapons.Sword;

public class Commander extends Unit{
    public Commander(Player owner, int positionX, int positionY) {
        super(owner, "textures/Commander.png", 5, new Sword(2, 3), positionX, positionY,true, false, false);
    }
}
