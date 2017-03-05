package me.dico.satellite.fxml.menu;

public class MenuOptions {
    
    public String getUsername() {
        return name;
    }
    
    public String getDepartureRadius() {
        return departureRadius;
    }
    
    public String getArrivalRadius() {
        return arrivalRadius;
    }
    
    public int getAttempts() {
        return attempts;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    private String name, departureRadius, arrivalRadius;
    private int attempts;
    private boolean editable;
    
    public MenuOptions(String name, String departureRadius, String arrivalRadius, int attempts, boolean editable) {
        this.name = name;
        this.departureRadius = departureRadius;
        this.arrivalRadius = arrivalRadius;
        this.attempts = attempts;
        this.editable = editable;
    }
}
