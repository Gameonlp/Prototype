package com.mygdx.game.units;

import com.mygdx.game.weapons.Bow;

public class Archer extends Unit{
    public Archer(int owner, int positionX, int positionY){
        super(owner, "textures/Archer.png", 3, 4, new Bow(2), positionX, positionY,true, false, false);
    }
}
