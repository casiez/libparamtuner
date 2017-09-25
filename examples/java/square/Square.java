/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Baloup, Veïs Oudjail, Géry Casiez
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

public class Square extends Application {
	GraphicsContext gc;
	Canvas canvas;
	int x = 10, y = 10, width = 400, height = 200;
	String message = "Hello world";
	
    @Override
	public void start(Stage stage) {
		// Load settings file 
		ParamTuner.load("settings.xml", true);

		ParamTuner.bind("x", Integer.class, v -> x = v);
		ParamTuner.bind("y", Integer.class, v -> y = v);
		ParamTuner.bind("width", Integer.class, v -> width = v);
		ParamTuner.bind("height", Integer.class, v -> height = v);
		ParamTuner.bind("message", String.class, v -> message = v);
		
		ParamTuner.update();
    	
	    VBox root = new VBox();
	    canvas = new Canvas(600, 400);
	    gc = canvas.getGraphicsContext2D();
	    root.getChildren().add(canvas);
	
	    stage.setTitle("Hello libparamtuner");
	    stage.setScene(new Scene(root));
	    stage.show();
		stage.setOnCloseRequest((we) -> System.exit(0));
	    
        new AnimationTimer() {
        	private long lastUpdate = 0;
        	
            @Override
			public void handle(long now) {
            	if (now - lastUpdate >= 15_000_000) { // 15 ms
            		update();
            		lastUpdate = now;
            	}
            }
        }.start();
    }

	public void update() {
		ParamTuner.update();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.setFill(Color.RED);
	    gc.fillRect(x, y, width, height);
		gc.setFill(Color.BLACK);
		gc.fillText(message, x+3, y+20);
	}
    
    public static void main(String[] args) {
        launch(args);
    }
}

