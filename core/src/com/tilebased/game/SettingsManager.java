package com.tilebased.game;

import java.util.*;

public class SettingsManager {
    Map<String, Boolean> booleanSettings;
    Map<String, Integer> integerSettings;
    Map<String, List<String>> stringListSettings;

    private static final SettingsManager instance = new SettingsManager();

    private SettingsManager(){
        booleanSettings = new HashMap<>();
        integerSettings = new HashMap<>();
        stringListSettings = new HashMap<>();
        this.loadSettings();
    }

    public static SettingsManager getInstance(){
        return instance;
    }

    private void loadSettings(){
        booleanSettings.put("DebugShowDistances", false);
        booleanSettings.put("DebugPrintStates", false);
        stringListSettings.put("WittySentences", new LinkedList<>(Arrays.asList("Try right clicking!", "I am error!", "It's dangerous to go alone, take this!")));
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

    public List<String> getStringListSetting(String name){
        List<String> setting = stringListSettings.get(name);
        if (setting != null) {
            return setting;
        }
        return new LinkedList<>();
    }
}
