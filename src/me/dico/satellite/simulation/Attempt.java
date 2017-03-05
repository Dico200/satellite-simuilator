package me.dico.satellite.simulation;

import me.dico.satellite.simulation.graphing.PointList;

import java.io.Serializable;

public class Attempt implements Serializable {
    private static final long serialVersionUID = 1633683545707839205L;
    
    private String name;
    private PointList data;
    private Situation situation;
    
    public Attempt(String name, Situation situation, PointList data) {
        this.data = data;
        this.situation = situation;
    }
    
    public String getName() {
        return name;
    }
    
    public PointList getData() {
        return data;
    }
    
    public Situation getSituation() {
        return situation;
    }
}
