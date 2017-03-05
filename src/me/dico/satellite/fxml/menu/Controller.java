package me.dico.satellite.fxml.menu;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import me.dico.satellite.Satellite;
import me.dico.satellite.utils.SceneType;
import me.dico.satellite.utils.Scenes;

import java.util.HashMap;
import java.util.Map.Entry;

public class Controller {
    
    @FXML
    public TextField username, departureRadius, arrivalRadius, attempts;
    @FXML
    public Button playButton;
    
    public Controller() {
        log("Controller initialized!");
        
        setHandlers();
        
    }
    
    private void setHandlers() {
        Platform.runLater(() -> {
            setValuesEditable(false);
            
            HashMap<TextField, String> inputs = new HashMap<>();
            inputs.put(username, "Username: ");
            inputs.put(departureRadius, "Starting radius: ");
            inputs.put(arrivalRadius, "Target radius: ");
            inputs.put(attempts, "Number of attempts: ");
            
            username.setText("Username: ");
            
            playButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                private String getValue(TextField field) {
                    return field.getText().substring(inputs.get(field).length());
                }
                
                @Override
                public void handle(MouseEvent arg0) {
                    String username, departureRadius, arrivalRadius;
                    int attempts;
                    try {
                        username = getValue(Controller.this.username);
                        departureRadius = getValue(Controller.this.departureRadius);
                        arrivalRadius = getValue(Controller.this.arrivalRadius);
                        attempts = Integer.parseInt(getValue(Controller.this.attempts));
                        
                        Double.parseDouble(departureRadius);
                        Double.parseDouble(arrivalRadius);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    
                    if (username.equals("")) return;
                    
                    Scenes.setScene(SceneType.GAME);
                    Satellite.getInstance().setMenuOptions(new MenuOptions(username, departureRadius, arrivalRadius, attempts, false));
                    
                }
                
            });
            
            for (Entry<TextField, String> entry : inputs.entrySet()) {
                
                entry.getKey().textProperty().addListener(new ChangeListener<String>() {
                    
                    private TextField input = entry.getKey();
                    private String start = entry.getValue();
                    
                    ObservableList<String> styleClass = input.getStyleClass();
                    boolean hasError = false;
                    
                    boolean justSet = false;
                    
                    @Override
                    public void changed(ObservableValue<? extends String> ignored, String oldValue, String newValue) {
                        if (justSet) {
                            justSet = false;
                        } else if (!newValue.startsWith(start)) {
                            justSet = true;
                            input.setText(oldValue); //Calls this entire thing again
                        } else { //Check if number inputs are valid
                            if (input == username)
                                return;
                            
                            String value = input.getText().substring(start.length());
                            
                            try {
                                if (input == departureRadius || input == arrivalRadius) {
                                    Double.parseDouble(value);
                                } else if (input == attempts) {
                                    Integer.parseInt(value);
                                }
                            } catch (NumberFormatException e) {
                                
                                if (!styleClass.contains("error")) {
                                    styleClass.add("error");
                                    hasError = true;
                                }
                                return;
                            }
                            log("Result: VALID");
                            if (hasError) {
                                log("Removing 'error' from styleClass");
                                styleClass.remove("error");
                                hasError = false;
                            }
                        }
                    }
                    
                });
                
            }
            
        });
    }
    
    public void log(String text) {
        Satellite.getInstance().log("[MENUCONTROLLER]" + text);
    }
    
    private void setValuesEditable(boolean editable) {
        departureRadius.setEditable(editable);
        arrivalRadius.setEditable(editable);
        attempts.setEditable(editable);
        departureRadius.setVisible(editable);
        arrivalRadius.setVisible(editable);
        attempts.setVisible(editable);
    }
    
}
