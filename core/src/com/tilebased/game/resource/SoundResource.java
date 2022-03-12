package com.tilebased.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundResource extends ResourceManager.Resource<Sound> {
    public SoundResource(String resourcePath) {
        super(resourcePath);
    }

    @Override
    protected Sound loadResource() {
        return Gdx.audio.newSound(Gdx.files.internal(resourcePath));
    }
}
