package me.dico.satellite.simulation.graphing;

import javafx.scene.canvas.Canvas;

class CanvasCoordinateFixer {
	
	private double sizeConstant;
	private int xOffset, yOffset;
	
	protected CanvasCoordinateFixer(Canvas canvas, double departureRadius, double arrivalRadius) {
		xOffset = (int) (canvas.getWidth() / 2);
		yOffset = (int) (canvas.getHeight() / 2);
		
		sizeConstant = (Math.min(canvas.getWidth(), canvas.getHeight()) / Math.max(arrivalRadius, departureRadius)) / 2.5;
	}
	
	protected double getCanvasX(double x) {
		return sizeConstant * x + xOffset;
	}
	
	protected double getCanvasY(double y) {
		return sizeConstant * y + yOffset;
	}
	
	protected double getSizeConstant() {
		return sizeConstant;
	}

}
