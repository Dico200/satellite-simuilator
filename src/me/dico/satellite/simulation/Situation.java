package me.dico.satellite.simulation;

import java.io.Serializable;

public class Situation implements Serializable {
    private static final long serialVersionUID = -8666963275736908775L;
    
    private double vBoost1, vBoost2, tBoost2, precision, endTime, departureRadius, arrivalRadius;
    
    public Situation(double vBoost1, double vBoost2, double tBoost2, double d, double e, double precision, double endTime) {
        this.departureRadius = d;
        this.arrivalRadius = e;
        this.endTime = endTime;
        this.vBoost1 = vBoost1;
        this.vBoost2 = vBoost2;
        this.tBoost2 = tBoost2;
        this.precision = precision;
    }
    
    public double getDepartureRadius() {
        return departureRadius;
    }
    
    public double getArrivalRadius() {
        return arrivalRadius;
    }
    
    public double getEndTime() {
        return endTime;
    }
    
    public double getvBoost1() {
        return vBoost1;
    }
    
    public double getvBoost2() {
        return vBoost2;
    }
    
    public double gettBoost2() {
        return tBoost2;
    }
    
    public double getPrecision() {
        return precision;
    }
    
}
