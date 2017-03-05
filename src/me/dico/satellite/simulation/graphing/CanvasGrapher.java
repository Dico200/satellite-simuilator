package me.dico.satellite.simulation.graphing;

import me.dico.satellite.Satellite;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.canvas.*;

public class CanvasGrapher {
	
	private void log(String text) {
		Satellite.getInstance().log("[Grapher] " + text);
	}
	
	private Image /*satelliteImage, */earthImage, backgroundImage, succesfullImage, notSuccesfullImage;
	private Canvas canv;
	private CanvasCoordinateFixer fixer;
	//private double satSize;
	private double departureRadius, arrivalRadius;
	
	public CanvasGrapher(Canvas canv, double departureRadius, double arrivalRadius) {
		this.backgroundImage = getImage("background.png");
		this.earthImage = getImage("earth.png");
		//this.satelliteImage = getImage("satellite.png");
		this.succesfullImage = getImage("succesfull_true.png");
		this.notSuccesfullImage = getImage("succesfull_false.png");
		
		this.departureRadius = departureRadius;
		this.arrivalRadius = arrivalRadius;
		this.canv = canv;
		
		this.fixer = new CanvasCoordinateFixer(canv, departureRadius, arrivalRadius);
		
		Platform.runLater(() -> {
			GraphicsContext context = canv.getGraphicsContext2D();
			context.clearRect(0, 0, canv.getWidth(), canv.getHeight());
			context.beginPath(); //Clear previous path, took a while to figure this out...
			
			//draw the backgrounnd image
			context.drawImage(backgroundImage, 0, 0, canv.getWidth(), canv.getHeight());
			
			double[] cp; //Circle points from getCirclePoints. Used to store top left x,y-coord and width/height
			context.setLineWidth(3.5);
			
			//Draw circle for departure orbit
			cp = getCirclePoints(departureRadius);
			context.setStroke(Color.WHITE);
			log(String.format("Values Departure orbit: %s, %s, %s, %s", cp[0], cp[1], cp[2], cp[3]));
			context.strokeOval(cp[0], cp[1], cp[2], cp[3]);
			log("Departure orbit drawn!");
			
			//Draw circle for arrival orbit
			cp = getCirclePoints(arrivalRadius);
			context.setStroke(Color.GREEN);
			log(String.format("Values Arrival orbit: %s, %s, %s, %s", cp[0], cp[1], cp[2], cp[3]));
			context.strokeOval(cp[0], cp[1], cp[2], cp[3]);
			log("Arrival orbit drawn");
			
			//Draw the earth.
			cp = getCirclePoints(6378100);
			context.drawImage(earthImage, cp[0], cp[1], cp[2], cp[3]);
			log("Earth drawn");
			//this.satSize = cp[2] / 3; //Sets a satellite size to a fifth of the earths size (abnormal of course, it's a simulation.)
			
			context.stroke();
			
			context.setLineWidth(1.5);
			context.setStroke(Color.RED);
		});
	}
	
	private double[] getCirclePoints(double radius) {
		double x = fixer.getCanvasX(-radius); //negative; top left corner
		double y = fixer.getCanvasY(-radius);
		double w = fixer.getCanvasX(radius) - x;
		double h = fixer.getCanvasY(radius) - y;
		return new double[]{x, y, w, h};
	}
	
	public void drawPoint(double x, double y) {
		Platform.runLater(() -> {
			double cx = fixer.getCanvasX(x);
			double cy = fixer.getCanvasY(y);
			
			GraphicsContext context = canv.getGraphicsContext2D();
			context.lineTo(cx, cy);
			context.stroke();
			
			log("CanvasX: " + (int)cx + ", CanvasY: " + (int)cy);
			
			//context.drawImage(satelliteImage, cx - (satSize/2), (cy - satSize/2), satSize, satSize);
			//log(String.format("Satellite image location: %.2f, %.2f, %.2f, %.2f", cx - (satSize/2), (cy - satSize/2), satSize, satSize));
		});
	}
	
	public Canvas getCanvas() {
		return canv;
	}
	
	public double getDepartureRadius() {
		return departureRadius;
	}
	
	public double getArrivalRadius() {
		return arrivalRadius;
	}
	
	private Image getImage(String resource) {
		return new Image(getClass().getResourceAsStream("/me/dico/satellite/simulation/graphing/img/" + resource));
	}
	
	public void drawSuccesfullImage(boolean succesfull) {
		log("Drawing result image: " + succesfull);
		GraphicsContext context = canv.getGraphicsContext2D();
		if (succesfull)
			context.drawImage(succesfullImage, 470, 470, 100, 100);
		else
			context.drawImage(notSuccesfullImage, 470, 470, 100, 100);
		context.stroke();
	}
	
}
