package com.tilebased.game.units;

import com.tilebased.game.player.Player;
import com.tilebased.game.units.weapons.Bow;

public class Archer extends Unit{
    public Archer(Player owner, int positionX, int positionY){
        super(owner, "textures/Archer.png", 4, new Bow(2, 2), positionX, positionY,true, false, false);
    }
}
