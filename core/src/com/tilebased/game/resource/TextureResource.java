package com.tilebased.game.resource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureResource extends ResourceManager.Resource<Texture> {
    public TextureResource(String resourcePath) {
        super(resourcePath);
    }

    @Override
    protected Texture loadResource() {
        return new Texture(Gdx.files.internal(resourcePath));
    }
}
