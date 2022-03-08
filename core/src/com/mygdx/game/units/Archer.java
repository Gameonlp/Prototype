package com.mygdx.game.units;

import com.mygdx.game.player.Player;
import com.mygdx.game.units.weapons.Bow;

public class Archer extends Unit{
    public Archer(Player owner, int positionX, int positionY){
        super(owner, "textures/Archer.png", 4, new Bow(2), positionX, positionY,true, false, false);
    }
}
