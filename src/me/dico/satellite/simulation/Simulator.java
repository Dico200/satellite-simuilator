package me.dico.satellite.simulation;

import me.dico.satellite.Satellite;

public abstract class Simulator {
    
    private boolean ticking;
    private Thread simulator;
    
    public Simulator() {
        this.ticking = true;
        
        simulator = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (ticking)
                        tick();
                } catch (Exception e) {
                    log("An exception has occurred: " + e);
                    throw e;
                }
                
            }
        });
    }
    
    protected void log(String text) {
        Satellite.getInstance().log("[Simulator] " + text);
    }
    
    protected abstract void tick();
    
    public boolean isTicking() {
        return ticking;
    }
    
    public void start() {
        simulator.start();
    }
    
    protected void finish() {
        ticking = false; //DONT REMOVE THIS PLS
        simulator.interrupt();
    }
}
