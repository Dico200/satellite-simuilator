package me.dico.satellite.simulation;

import javafx.application.Platform;
import me.dico.satellite.Satellite;
import me.dico.satellite.simulation.graphing.PointList;

public class SatelliteSimulator extends Simulator {
    
    private static double E(double base, int exponent) {
        return base * Math.pow(10, exponent);
    }
    
    private static final double GM = E(3.986004418, 14);
    
    public SatelliteSimulator(Situation presets) {
        
        this.presets = presets;
        this.points = new PointList();
        
        x = 0;
        y = presets.getDepartureRadius();
        vx = Math.sqrt(GM / y);
        time = -(2 * Math.PI * y) / vx;
        vy = 0;
        endTime = presets.getEndTime();
        calcDelay = 1 / presets.getPrecision();
        calcsPerPoint = (20 * presets.getPrecision());
        
        checkTime = presets.gettBoost2();
        lowerBound = presets.getArrivalRadius() * 0.98;
        upperBound = presets.getArrivalRadius() * 1.02;
        
        Satellite.getInstance().getController().setStartTime(time);
    }
    
    private Situation presets;
    private PointList points;
    
    public Situation getPresets() {
        return presets;
    }
    
    double time;
    double x;
    double y;
    double v;
    double vx;
    double vy;
    double r;
    double a, ax, ay;
    double calcDelay;
    double endTime;
    
    double checkTime; //When to start checking if the orbit was achieved. The radius must be within 2% of the end orbit at all times from here.
    double lowerBound;
    double upperBound;
    
    double calcsPerPoint;
    int callCount = 0;
    
    boolean succesfullSimulation = true;
    
    @Override
    protected void tick() {
        if (time >= presets.gettBoost2() && time - calcDelay < presets.gettBoost2()) {
            log("Second speed boost executed");
            
            double boost = presets.getvBoost2();
            double v = Math.hypot(vx, vy);
            vx += vx * boost / v;
            vy += vy * boost / v;
        }
        
        if (time >= 0 && time - calcDelay < 0) {
            
            x = 0;
            y = presets.getDepartureRadius();
            vx = Math.sqrt(GM / y) + presets.getvBoost1();
            vy = 0;
            
        }
        
        r = Math.hypot(x, y);
        
        if (r < 5900000) { //Crashed into earth!
            finish(false);
        }
        
        a = GM / Math.pow(r, 2);
        ax = -a * x / r;
        ay = -a * y / r;
        
        x += vx * calcDelay;
        y += vy * calcDelay;
        
        if (fastfloor(callCount % calcsPerPoint) == 0) {
            points.add(time, x, y, r, Math.hypot(vx, vy));
        }
        
        vx += ax * calcDelay;
        vy += ay * calcDelay;
        
        log(String.format("X: %s, Y: %s, VX: %s, VY: %s, R: %s, Time: %s", (int) x, (int) y, (int) vx, (int) vy, (int) r, (int) time));
        
        time += calcDelay;
        
        if (time >= checkTime) {
            if (!(lowerBound < r && r < upperBound))
                succesfullSimulation = false;
            
            if (time >= endTime)
                finish(true);
        }
        
        callCount++;
        
    }
    
    public void finish(boolean endTimeReached) {
        super.finish();
        Platform.runLater(() -> {
            Satellite.getInstance().getController().finish(points);
            if (endTimeReached) {
                Satellite.getInstance().getController().setSuccesfull(succesfullSimulation);
            }
        });
        
    }
    
    private int fastfloor(double d) { //Approximately 9x faster than Math.floor()
        int i = (int) d;
        return i < d ? i : i - 1;
    }
    
}
