package me.dico.satellite.fxml.game;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import me.dico.satellite.Satellite;
import me.dico.satellite.fxml.menu.MenuOptions;
import me.dico.satellite.simulation.Attempt;
import me.dico.satellite.simulation.SatelliteSimulator;
import me.dico.satellite.simulation.Situation;
import me.dico.satellite.simulation.graphing.CanvasGrapher;
import me.dico.satellite.simulation.graphing.ChartType;
import me.dico.satellite.simulation.graphing.PointList;


@SuppressWarnings("unused")
public class Controller {
    
    private SatelliteSimulator simulator;
    private boolean advancedFieldsEditable = true;
    private Scene scene;
    private boolean situationStatic;
    
    private Attempt[] attempts;
    private int attemptId = 0;
    private String username;
    
    private double startTime;
    
    public Controller() {
        Satellite.getInstance().setController(this);
        
        setHandlers();
        
        System.out.println("Controller initialized!");
    }
    
    /*
     * Sets all the eventhandlers for the window, including the buttons and resizing.
     * This is done in runLater to prevent this from being run before all fields are given objects by the FXMLLoader.
     */
    @SuppressWarnings("unchecked")
    private void setHandlers() {
        
        Satellite.getInstance().getPrimaryStage().setScene(Satellite.getInstance().getMenuScene());
        
        Platform.runLater(() -> {
            
            //--
            
            MenuOptions settings = Satellite.getInstance().getMenuOptions();
            
            departureRadius.setText(settings.getDepartureRadius());
            arrivalRadius.setText(settings.getArrivalRadius());
            Stage stage = Satellite.getInstance().getPrimaryStage();
            stage.setTitle(stage.getTitle() + " - " + settings.getUsername());
            
            if (!settings.isEditable()) {
                setFieldsEditable(false);
                this.advancedFieldsEditable = false;
                setFieldsEditable(true);
            }
            
            this.attempts = new Attempt[settings.getAttempts()];
            this.username = settings.getUsername();
            
            log(attemptsLeftLabel.toString());
            attemptsLeftLabel.setVisible(true);
            attemptsLeftLabel.setText("Attempts left: " + attemptsLeft());
            
            //--
            
            drawSituation();
            
            Node[] toChangeScalesOf = new Node[]{trajectoryCanvas, radiusChart, velocityChart, cursorCoords};
            
            graphsWrappingPane.widthProperty().addListener(new ChangeListener<Number>() {
                
                @Override
                public void changed(ObservableValue<? extends Number> ignored, Number oldWidth, Number newWidth) {
                    double changeRatio = newWidth.doubleValue() / oldWidth.doubleValue();
                    for (Node node : toChangeScalesOf) {
                        node.setScaleX(changeRatio * node.getScaleX());
                        node.setLayoutX(node.getLayoutX() + (newWidth.doubleValue() - oldWidth.doubleValue()) / 2);
                        
                    }
                }
            });
            
            graphsWrappingPane.heightProperty().addListener(new ChangeListener<Number>() {
                
                @Override
                public void changed(ObservableValue<? extends Number> ignored, Number oldHeight, Number newHeight) {
                    double changeRatio = newHeight.doubleValue() / oldHeight.doubleValue();
                    for (Node node : toChangeScalesOf) {
                        node.setScaleY(changeRatio * node.getScaleY());
                        node.setLayoutY(node.getLayoutY() + (newHeight.doubleValue() - oldHeight.doubleValue()) / 2);
                    }
                }
            });
            
            startButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    startErrorText.setVisible(false);
                    
                    if (simulating()) {
                        simulator.finish(false);
                        return;
                    }
                    
                    if (attemptsLeft() == 0)
                        return;
                    
                    try {
                        
                        if (getValue(tBoost2) * 2 > getValue(endTime)) {
                            endTime.setText(getValue(tBoost2) * 2 + "");
                        }
                        
                        simulator = new SatelliteSimulator(new Situation(
                                getValue(vBoost1),
                                getValue(vBoost2),
                                getValue(tBoost2),
                                getValue(departureRadius),
                                getValue(arrivalRadius),
                                getValue(precision),
                                getValue(endTime)
                        ));
                        
                        drawSituation();
                        
                    } catch (NumberFormatException e) {
                        startErrorText.setVisible(true);
                        return;
                    }
                    
                    setFieldsEditable(false);
                    startButton.setText("STOP");
                    simulator.start();
                    
                }
            });
            
            advancedButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    setAdvancedVisible(advancedButton.isSelected());
                }
            });
            
            velocityChartButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    setVisibleChart(velocityChart);
                }
            });
            
            radiusChartButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    setVisibleChart(radiusChart);
                }
            });
            
            trajectoryButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                
                @Override
                public void handle(MouseEvent event) {
                    setVisibleChart(trajectoryCanvas);
                }
            });
            
            for (LineChart<Number, Number> chart : new LineChart[]{radiusChart, velocityChart}) {
                
                chart.setOnMouseExited(new EventHandler<MouseEvent>() {
                    
                    @Override
                    public void handle(MouseEvent event) {
                        cursorCoords.setVisible(false);
                    }
                });
                
                
                chart.setOnMouseMoved(new EventHandler<MouseEvent>() {
                    
                    Axis<Number> xAxis = chart.getXAxis();
                    Axis<Number> yAxis = chart.getYAxis();
                    
                    Node background = chart.lookup(".chart-plot-background");
                    double offset = background.getLayoutX() + (chart == radiusChart ? 36.5 : 18);
                    String yType = chart == radiusChart ? "r" : "v";
                    
                    XYChart.Data<Number, Number> point = null;
                    
                    @Override
                    public void handle(MouseEvent event) {
                        double time = xAxis.getValueForDisplay(event.getX() - offset).doubleValue();
                        try {
                            cursorCoords.setVisible(true);
                            
                            if (point != null) point.getNode().setVisible(false);
                            
                            point = chart.getData().get(0).getData().get(fastFloor((time - startTime) / 20D));
                            point.getNode().setVisible(true);
                            
                            double yValue = point.getYValue().doubleValue();
                            cursorCoords.setText(String.format("t = %.2f\n%s = %.2f", time, yType, yValue));
                        } catch (IndexOutOfBoundsException e) {
                            cursorCoords.setVisible(false);
                        }
                    }
                });
            }
            
            TextField[] inputs = {vBoost1, vBoost2, tBoost2, departureRadius, arrivalRadius, precision, endTime};
            for (TextField input : inputs) {
                input.textProperty().addListener(new ChangeListener<String>() {
                    
                    ObservableList<String> styleClass = input.getStyleClass();
                    boolean hasError = false;
                    
                    boolean justRan = false;
                    
                    @Override
                    public void changed(ObservableValue<? extends String> ignored, String oldValue, String newValue) {
                        if (justRan)
                            return;
                        try {
                            double value = Double.parseDouble(newValue);
                            if ((input == departureRadius || input == arrivalRadius) && value < 6500000) {
                                input.setText(oldValue); //calls everything again
                                justRan = true;
                                return;
                            }
                        } catch (NumberFormatException e) {
                            if (!styleClass.contains("error")) {
                                styleClass.add("error");
                                hasError = true;
                            }
                            return;
                        }
                        if (hasError) {
                            styleClass.remove("error");
                            hasError = false;
                        }
                    }
                });
                
                input.setOnScroll(new EventHandler<ScrollEvent>() {
                    
                    @Override
                    public void handle(ScrollEvent event) {
                        if (!input.isEditable())
                            return;
                        double value;
                        try {
                            value = Double.parseDouble(input.getText());
                        } catch (NumberFormatException e) {
                            return;
                        }
                        
                        int change = 10;
                        if (input == departureRadius || input == arrivalRadius)
                            change = 100000;
                        else if (input == precision)
                            change = 1;
                        else if (input == endTime)
                            change = 5000;
                        
                        value += (event.getDeltaY() > 0) ? change : -change;
                        input.setText(String.format("%.0f", value));
                    }
                });
            }
            TextField[] radiuses = {departureRadius, arrivalRadius};
            
            for (TextField field : radiuses) {
                field.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ignored, Boolean oldValue, Boolean newValue) {
                        if (oldValue) {
                            try {
                                CanvasGrapher grapher = Satellite.getInstance().getGrapher();
                                if ((field == departureRadius && getValue(field) == grapher.getDepartureRadius())
                                        || (field == arrivalRadius && getValue(field) == grapher.getArrivalRadius())) //No change
                                    return;
                                drawSituation();
                            } catch (NumberFormatException ignoredException) {
                            } //Just don't draw!
                        }
                    }
                });
            }
        });
    }
    
    private void drawSituation() {
        Satellite.getInstance().setGrapher(new CanvasGrapher(trajectoryCanvas, getValue(departureRadius), getValue(arrivalRadius)));
    }
    
    // Faster than Math.round()
    int fastFloor(double d) {
        int i = (int) d;
        return i < d ? i : i - 1;
    }
    
    private void setFieldsEditable(boolean editable) {
        vBoost1.setEditable(editable);
        vBoost2.setEditable(editable);
        tBoost2.setEditable(editable);
        endTime.setEditable(editable);
        
        if (advancedFieldsEditable) {
            departureRadius.setEditable(editable);
            arrivalRadius.setEditable(editable);
            precision.setEditable(editable);
        }
    }
    
    private void setAdvancedVisible(boolean visible) {
        precision.setVisible(visible);
        endTime.setVisible(visible);
        departureRadius.setVisible(visible);
        arrivalRadius.setVisible(visible);
        precisionLabel.setVisible(visible);
        endTimeLabel.setVisible(visible);
        departureRadiusLabel.setVisible(visible);
        arrivalRadiusLabel.setVisible(visible);
    }
    
    @FXML
    public Button startButton, trajectoryButton, radiusChartButton, velocityChartButton;
    @FXML
    public ToggleButton advancedButton;
    @FXML
    public TextField vBoost1, vBoost2, tBoost2, precision, endTime, departureRadius, arrivalRadius;
    @FXML
    public Label precisionLabel, endTimeLabel, departureRadiusLabel, arrivalRadiusLabel, cursorCoords, attemptsLeftLabel;
    @FXML
    public Text startErrorText;
    @FXML
    public Canvas trajectoryCanvas;
    @FXML
    public LineChart<Number, Number> radiusChart, velocityChart;
    @FXML
    public Pane graphsWrappingPane;
    
    private void setVisibleChart(Node toBeVisible) {
        trajectoryCanvas.setVisible(false);
        radiusChart.setVisible(false);
        velocityChart.setVisible(false);
        toBeVisible.setVisible(true);
    }
    
    public double getValue(TextField field) {
        return Double.parseDouble(field.getText());
    }
    
    public void log(String msg) {
        Satellite.getInstance().log("[Controller] " + msg);
    }
    
    public void finish(PointList points) {
        points.pushToChart(radiusChart, ChartType.RADIUS);
        points.pushToChart(velocityChart, ChartType.VELOCITY);
        startButton.setText("START");
        setFieldsEditable(true);
    }
    
    public void setSuccesfull(boolean succesfull) {
        log("Succesfull simulation: " + succesfull);
        Satellite.getInstance().getGrapher().drawSuccesfullImage(succesfull);
        
        if (succesfull) {
            startButton.getStyleClass().add("no-attempts");
            attemptId = attempts.length;
        }
    }
    
    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
    
    public boolean simulating() {
        return simulator == null ? false : simulator.isTicking();
    }
    
    public void setSituationStatic(boolean situationStatic) {
        this.situationStatic = situationStatic;
    }
    
    //--------------Getters---------------
    
    public TextField getvBoost1() {
        return vBoost1;
    }
    
    public TextField getvBoost2() {
        return vBoost2;
    }
    
    public TextField gettBoost2() {
        return tBoost2;
    }
    
    public TextField getPrecision() {
        return precision;
    }
    
    public TextField getEndTime() {
        return endTime;
    }
    
    public TextField getDepartureRadius() {
        return departureRadius;
    }
    
    public TextField getArrivalRadius() {
        return arrivalRadius;
    }
    
    public Canvas getTrajectoryCanvas() {
        return trajectoryCanvas;
    }
    
    public LineChart<Number, Number> getRadiusChart() {
        return radiusChart;
    }
    
    public LineChart<Number, Number> getVelocityChart() {
        return velocityChart;
    }
    
    public boolean isSituationStatic() {
        return situationStatic;
    }
    
    public void newAttempt(Situation situation, PointList data) {
        attempts[attemptId++] = new Attempt(username, situation, data);
        
        attemptsLeftLabel.setText("Attempts left: " + attemptsLeft());
        
        if (attemptsLeft() == 0)
            startButton.getStyleClass().add("no-attempts");
    }
    
    public int attemptsLeft() {
        return attempts.length - attemptId;
    }
    
}
