package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Logger;

import java.util.*;

public class GameMap {
    private static final Logger LOGGER = new Logger(GameMap.class.getName());
    private final Tile[] map;
    private final int height;
    private final int width;
    private HashMap<Tile, Texture> uniqueTextures;

    private static final Tile[] tileAtlas = new Tile[256];

    static {
        tileAtlas[0] = new Tile("textures/black.png",false, false, false, false);
        tileAtlas[1] = new Tile("textures/grey.png",false, false, false, false);
        tileAtlas[2] = new Tile("textures/tile.png",true, false, false, false);
    }

    private GameMap(int width, int height, Tile[] map, HashMap<Tile, Texture> uniqueTextures){
        this.width = width;
        this.height = height;
        this.map = map;
        this.uniqueTextures = uniqueTextures;
    }

    public Tile getTile(int x, int y){
        return map[(height - y - 1) * width + x];
    }

    public static GameMap loadMap(String path){
        int height = 0;
        int width = 0;
        Tile[] map = null;
        HashMap<Tile, Texture> uniqueTextures = new HashMap<>();
        try(Scanner fileScanner = new Scanner(Gdx.files.internal(path).read())) {
            if (fileScanner.hasNext("\\d*?,\\d+?")) {
                String dimensionString = fileScanner.next("\\d*?,\\d*?");
                String[] dimensions = dimensionString.split(",");
                height = Integer.parseInt(dimensions[0]);
                width = Integer.parseInt(dimensions[1]);
                map = new Tile[width * height];
            } else {
                LOGGER.error("Map " + path + " is broken.");
                System.exit(-1);
            }
            fileScanner.nextLine();
            int lineNumber = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] entries = line.split(",");
                for(int i = 0; i < width; i++){
                    Tile tile = tileAtlas[Integer.parseInt(entries[i])];
                    map[width * lineNumber + i] = tile;
                    uniqueTextures.put(tile, new Texture(tile.texturePath));
                }
                lineNumber++;
            }
        }// catch (IOException e){
        //    LOGGER.error(e.toString());
        //    System.exit(-1);
        //}
        return new GameMap(width, height, map, uniqueTextures);
    }

    public void setTile(int y, int x, Tile entry){
        map[y * width + x] = entry;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Texture getTexture(int x, int y){
        return uniqueTextures.get(map[y * width + x]);
    }

    public void destroyMap(){
        for(Texture texture : uniqueTextures.values()){
            texture.dispose();
        }
    }
}
