/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Géry Casiez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import fr.univ_lille1.libparamtuner.ParamTuner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Carre extends Application {
	GraphicsContext gc;
	Canvas canvas;
	int x = 30, y = 30;
	String message = "Hello world";
	
    public void start(Stage stage) {
		// Load settings file 
		ParamTuner.load("settings.xml");

		ParamTuner.bind("x", Integer.class, v -> x = v);
		ParamTuner.bind("y", Integer.class, v -> y = v);
		ParamTuner.bind("message", String.class, v -> message = v);
    	
	    VBox root = new VBox();
	    canvas = new Canvas (300, 300);
	    gc = canvas.getGraphicsContext2D();
	    gc.setFill(Color.ORANGE);
	    gc.fillRect(40, 100, 20, 20);
	    gc.setStroke(Color.BLACK);
	    gc.strokeRect(40, 100, 20, 20);
	    root.getChildren().add(canvas);
	
	    Scene scene = new Scene(root);
	    stage.setTitle("Hello libparamtuner");
	    stage.setScene(scene);
	    stage.show();
	    
        new AnimationTimer() {
        	private long lastUpdate = 0 ;
        	
            public void handle(long now) {
            	if (now - lastUpdate >= 15_000_000) { // 15 ms
            		reaffichage();
            		lastUpdate = now ;
            	}
            }
        }.start();
    }

	public void reaffichage() {
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.BLACK);
		gc.fillText(message, x, y);
	}
    
    public static void main(String[] args) {
            Application.launch(args);
    }
}

