package com.mygdx.game;

import java.util.HashMap;
import java.util.Map;

public class SettingsManager {
    Map<String, Boolean> booleanSettings;
    Map<String, Integer> integerSettings;

    private static final SettingsManager instance = new SettingsManager();

    private SettingsManager(){
        booleanSettings = new HashMap<>();
        integerSettings = new HashMap<>();
        this.loadSettings();
    }

    public static SettingsManager getInstance(){
        return instance;
    }

    private void loadSettings(){
        booleanSettings.put("DebugShowDistances", false);
        booleanSettings.put("DebugPrintStates", false);
    }

    public boolean getBooleanSetting(String name){
        Boolean setting = booleanSettings.get(name);
        if (setting != null) {
            return setting;
        }
        return false;
    }

    public int getIntegerSetting(String name){
        Integer setting = integerSettings.get(name);
        if (setting != null) {
            return setting;
        }
        return Integer.MAX_VALUE;
    }
}
