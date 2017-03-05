package me.dico.satellite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.dico.satellite.fxml.SimulationController;
import me.dico.satellite.simulation.graphing.CanvasGrapher;
import me.dico.satellite.utils.Logger;

public class Satellite extends Application {

    private static Satellite instance;
    
    private Logger logger;
    private CanvasGrapher grapher = null;
    private SimulationController controller;
    
    private Stage stage;
    private Scene simulatorScene, menuScene;

    public Satellite() {
        logger = new Logger("Satellite");
        logger.setDebugging(true);
        instance = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
    	stage.setTitle("Satellite Game");
    	this.stage = stage;
    	this.simulatorScene = new Scene(FXMLLoader.load(getClass().getResource("/me/dico/satellite/fxml/simulator.fxml")), 800, 650);
    	this.menuScene = new Scene(FXMLLoader.load(getClass().getResource("/me/dico/satellite/fxml/menu.fxml")), 600, 500);
    	
    	stage.setScene(simulatorScene);
        stage.show();
        
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public void log(String string) {
        logger.debug(string);
    }
    
    
    
    // Getters and setters
    
    public static Satellite getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }
    
    public CanvasGrapher getGrapher() {
    	return grapher;
    }
    
    public Stage getPrimaryStage() {
    	return stage;
    }
    
    public Scene getSimulatorScene() {
    	return simulatorScene;
    }
    
    public Scene getMenuScene() {
    	return menuScene;
    }
    
    public SimulationController getController() {
    	return controller;
    }
    
    public void setController(SimulationController controller) {
    	this.controller = controller;
    }
    
    public void setGrapher(CanvasGrapher grapher) {
    	this.grapher = grapher;
    }
    
}
