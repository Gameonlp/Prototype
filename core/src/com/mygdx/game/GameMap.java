package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.player.Player;
import com.mygdx.game.player.aiplayer.AIPlayer;
import com.mygdx.game.player.aiplayer.strategy.AggressiveDumbStrategy;
import com.mygdx.game.player.aiplayer.strategy.RandomStrategy;
import com.mygdx.game.units.Archer;
import com.mygdx.game.units.Commander;
import com.mygdx.game.units.Unit;

import java.util.*;

public class GameMap {
    private static final Logger LOGGER = new Logger(GameMap.class.getName());
    private final Tile[] map;
    private List<Unit> units;
    private List<Player> players;
    private final int height;
    private final int width;
    private HashMap<Tile, Texture> uniqueTextures;

    private static final Tile[] tileAtlas = new Tile[256];
    private static final HashMap<String, Color> colorMap = new HashMap<>();
    private static final HashMap<String, Player.PlayerType> playerTypeMap = new HashMap<>();

    static {
        tileAtlas[0] = new Tile("textures/Black.png",false, false, false, false);
        tileAtlas[1] = new Tile("textures/Grey.png",false, false, false, false);
        tileAtlas[2] = new Tile("textures/Tile.png",true, false, false, false);

        colorMap.put("blue", Color.BLUE);
        colorMap.put("red", Color.RED);
        colorMap.put("green", Color.GREEN);
        colorMap.put("yellow", Color.YELLOW);

        playerTypeMap.put("human", Player.PlayerType.HUMAN);
        playerTypeMap.put("ai", Player.PlayerType.AI);
    }

    private GameMap(int width, int height, Tile[] map, List<Player> players, List<Unit> units, HashMap<Tile, Texture> uniqueTextures){
        this.width = width;
        this.height = height;
        this.map = map;
        this.players = players;
        this.units = units;
        this.uniqueTextures = uniqueTextures;
    }

    public Tile getTile(int x, int y){
        return map[(height - y - 1) * width + x];
    }

    public static GameMap loadMap(String path){
        int height = 0;
        int width = 0;
        Tile[] map = null;
        List<Player> players = new LinkedList<>();
        List<Unit> units = new LinkedList<>();
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
            int state = 0;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                switch (line){
                    case "Players":
                        state = 1;
                        continue;
                    case "Units":
                        state = 2;
                        continue;
                }
                switch (state) {
                    case 0: {
                        String[] entries = line.split(",");
                        for (int i = 0; i < width; i++) {
                            Tile tile = tileAtlas[Integer.parseInt(entries[i])];
                            map[width * lineNumber + i] = tile;
                            uniqueTextures.put(tile, new Texture(Gdx.files.internal(tile.getTexturePath())));
                        }
                        lineNumber++;
                        break;
                    }
                    case 1:{
                        String[] entries = line.split(",");
                        if (playerTypeMap.get(entries[1]) == Player.PlayerType.AI){
                            players.add(new AIPlayer(colorMap.get(entries[0]), new AggressiveDumbStrategy()));
                        } else {
                            players.add(new Player(colorMap.get(entries[0]), playerTypeMap.get(entries[1])));
                        }
                        break;
                    }
                    case 2:{
                        String[] entries = line.split(",");
                        if (entries[1].equals("Commander")) {
                            units.add(new Commander(players.get(Integer.parseInt(entries[0])), Integer.parseInt(entries[2]), Integer.parseInt(entries[3])));
                        }
                        if (entries[1].equals("Archer")) {
                            units.add(new Archer(players.get(Integer.parseInt(entries[0])), Integer.parseInt(entries[2]), Integer.parseInt(entries[3])));
                        }
                        break;
                    }
                    default:
                        LOGGER.error("Could not load map! Map contained: " + line);
                        System.exit(-1);
                }
            }
        }// catch (IOException e){
        //    LOGGER.error(e.toString());
        //    System.exit(-1);
        //}
        return new GameMap(width, height, map, players, units, uniqueTextures);
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
        for (Unit unit : units) {
            unit.destroy();
        }
    }

    public List<Unit> getUnits() {
        return units;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
