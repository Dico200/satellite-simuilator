package me.dico.satellite.utils;

public enum SceneType {

    MENU("/me/dico/satellite/fxml/menu/menu.fxml"), 
    GAME("/me/dico/satellite/fxml/game/simulator.fxml");

    private String scene;

    private SceneType(String scene) {
        this.scene = scene;
    }

    public String getSceneRes() {
        return this.scene;
    }
}
