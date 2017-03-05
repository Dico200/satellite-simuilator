package me.dico.satellite;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.dico.satellite.fxml.game.Controller;
import me.dico.satellite.fxml.menu.MenuOptions;
import me.dico.satellite.simulation.graphing.CanvasGrapher;
import me.dico.satellite.utils.Logger;
import me.dico.satellite.utils.SceneType;
import me.dico.satellite.utils.Scenes;

import java.io.IOException;

public class Satellite extends Application {
    
    private static Satellite instance;
    
    private Logger logger;
    private CanvasGrapher grapher = null;
    private Controller controller;
    private MenuOptions menuOptions;
    
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
        
        Scenes.setScene(SceneType.MENU);
        
        addResizingHandler();
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }
    
    public void updateScene() {
        String res = Scenes.getScene().getSceneRes();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource(res));
        } catch (IOException oops) {
            oops.printStackTrace();
            return;
        }
        int width = 788, height = 650;
        if (Scenes.getScene() == SceneType.MENU) {
            width = 600;
            height = 400;
        }
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        if (!stage.isShowing()) {
            stage.show();
        }
    }
    
    public void log(String string) {
        logger.debug(string);
    }
    
    private void addResizingHandler() {
        
        stage.heightProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> ignored, Number oldValue, Number newValue) {
                if (newValue.doubleValue() < 550) {
                    stage.setHeight(550);
                }
            }
        });
        
        stage.widthProperty().addListener(new ChangeListener<Number>() {
            
            @Override
            public void changed(ObservableValue<? extends Number> ignored, Number oldValue, Number newValue) {
                if (newValue.doubleValue() < 600) {
                    stage.setWidth(600);
                }
            }
        });
        
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
    
    public Controller getController() {
        return controller;
    }
    
    public void setController(Controller controller) {
        this.controller = controller;
    }
    
    public void setGrapher(CanvasGrapher grapher) {
        this.grapher = grapher;
    }
    
    public MenuOptions getMenuOptions() {
        return menuOptions;
    }
    
    public void setMenuOptions(MenuOptions menuOptions) {
        this.menuOptions = menuOptions;
    }
    
    public static void drawPoint(double x, double y) {
        instance.grapher.drawPoint(x, y);
    }
    
}
