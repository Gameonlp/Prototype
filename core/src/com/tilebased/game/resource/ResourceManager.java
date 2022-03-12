package com.tilebased.game.resource;

import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    public static abstract class Resource<T extends Disposable> implements Disposable{
        private T resource;
        private int counter;
        protected String resourcePath;

        public Resource(String resourcePath){
            this.resourcePath = resourcePath;
        }

        private void load(){
            synchronized (this) {
                if (counter == 0) {
                    resource = loadResource();
                }
                counter++;
            }
        }

        public final T getResource(){
            return resource;
        }

        protected abstract T loadResource();

        @Override
        public final void dispose() {
            synchronized (this) {
                counter--;
                if (counter == 0) {
                    resource.dispose();
                    resource = null;
                }
            }
        }
    }

    private static final ResourceManager instance = new ResourceManager();
    private final Map<String, Resource<?>> loadedResources;

    private ResourceManager(){
        loadedResources = new HashMap<>();
    }

    public TextureResource getTexture(String resourcePath){
        synchronized (this) {
            if (!loadedResources.containsKey(resourcePath)) {
                loadedResources.put(resourcePath, new TextureResource(resourcePath));
            }
            Resource<?> res = loadedResources.get(resourcePath);
            res.load();
            return (TextureResource) res;
        }
    }

    public SoundResource getSound(String resourcePath){
        synchronized (this) {
            if (!loadedResources.containsKey(resourcePath)) {
                loadedResources.put(resourcePath, new SoundResource(resourcePath));
            }
            Resource<?> res = loadedResources.get(resourcePath);
            res.load();
            return (SoundResource) res;
        }
    }

    public static ResourceManager getInstance() {
        return instance;
    }
}
