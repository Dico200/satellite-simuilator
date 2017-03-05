package me.dico.satellite.utils;

import me.dico.satellite.Satellite;

public class Scenes {
	
    private static final SceneType defaultScene = SceneType.MENU;
    private static SceneType currentScene;

    public static void setScene(SceneType scene) {
        currentScene = scene;
        Satellite.getInstance().updateScene();
    }

    public static SceneType getScene() {
        if (currentScene == null) {
            currentScene = defaultScene;
        }
        
        if (currentScene == SceneType.MENU)
        	Satellite.getInstance().getPrimaryStage().setResizable(false);
        else if (currentScene == SceneType.GAME)
        	Satellite.getInstance().getPrimaryStage().setResizable(true);
        
        return currentScene;
    }

}