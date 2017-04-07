/*
 *  libParamTuner
 *  Copyright (C) 2017 Marc Baloup, Veïs Oudjail
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
package fr.univ_lille1.libparamtuner.gui;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import fr.univ_lille1.libparamtuner.gui.parameters_panel.ParameterPanel;
import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.ParameterFile;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MainFrame extends Application {
	
	private Stage stage;
	private Scene scene;
	
	
	private VBox contentPanel;
	private TextField textField;
	public ScrollPane contentScroll; // TODO private
	private Button btnSave;

	public DoubleProperty minLabelSize = new SimpleDoubleProperty(0); // TODO private
	
	
	private ParameterFile loadedFile = null;
	private boolean saved = true;
	private boolean autosave = true;
	
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setTitle("ParamTuner GUI");
		
		stage.setOnCloseRequest((event) -> {
			if (confirmSaveBeforeClosingFile()) {
				stage.hide();
				System.exit(0);
			}
			else {
				event.consume();
			}
		});
		
		BorderPane globalPanel = new BorderPane();
		
		scene = new Scene(globalPanel, 500, 600);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();
		
		globalPanel.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, null, null, new BorderWidths(3))));
		
		
		
		HBox topPanel = new HBox(3);
		topPanel.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, null, null, new BorderWidths(2, 2, 3, 2))));
		globalPanel.setTop(topPanel);
		topPanel.setAlignment(Pos.CENTER);
		
		Label lblNewLabel = new Label("XML File :");
		topPanel.getChildren().add(lblNewLabel);
		
		textField = new TextField();
		topPanel.getChildren().add(textField);
		textField.setPrefWidth(20);
		HBox.setHgrow(textField, Priority.ALWAYS);
		textField.setOnAction(e -> loadFile(textField.getText()));
		
		Button btnCharger = new Button("Load");
		topPanel.getChildren().add(btnCharger);
		btnCharger.setOnAction(e -> loadFile(textField.getText()));
		
		btnSave = new Button("Save");
		btnSave.setOnAction(e -> saveFile());
		btnSave.setDisable(true);
		topPanel.getChildren().add(btnSave);
		
		ToggleButton chckbxAutosave = new ToggleButton("Autosave");
		chckbxAutosave.setSelected(autosave);
		chckbxAutosave.setOnAction(e -> setAutosave(chckbxAutosave.isSelected()));
		topPanel.getChildren().add(chckbxAutosave);
		
		contentScroll = new ScrollPane();
		contentScroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		contentScroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		globalPanel.setCenter(contentScroll);
		
		contentPanel = new VBox();
		contentPanel.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, null, null, new BorderWidths(3))));
		contentScroll.setContent(contentPanel);
		contentScroll.setFitToWidth(true);
		
		stage.show();
		
	}
	
	
	
	
	
	
	private void saveFile() {
		if (saved || loadedFile == null)
			return;
		
		try {
			loadedFile.save();
		} catch (TransformerException | ParserConfigurationException e) {
			FXDialogUtils.showExceptionDialog("Unable to save the file", "Path: " + loadedFile.file, e);
			return;
		}
		
		setSaved(true);
		
	}
	
	/**
	 * Ask to the user if he want to save or not save the file before closing it.
	 * If the user want to save the file, it is saved inside this method.
	 * @return true if the user want to close the file (with or without saving) or if the file is already saved, false if the user click cancel.
	 */
	private boolean confirmSaveBeforeClosingFile() {
		if (saved)
			return true;
		String ret = FXDialogUtils.showConfirmDialog("Save current file ?", null, "Do you want to save the current file ?",
				"Yes", "No", "Cancel");
		if (ret == null || ret.equals("Cancel"))
			return false;
		if (ret.equals("Yes"))
			saveFile();
		return true;
	}
	
	public void loadFile(String path) {
		
		if (path.trim().isEmpty()) {
			FXDialogUtils.showMessageDialog(AlertType.ERROR, "Error", null, "Please specify a file path");
			return;
		}
		
		if (!confirmSaveBeforeClosingFile())
			return;
		
		clearConfigEntries();
		
		
		ParameterFile pFile;
		
		
		try {
			pFile = new ParameterFile(path, true, true);
			
			for (Parameter p : pFile.getAll()) {
				addConfigEntry(p);
			}
			
			
			
			
			loadedFile = pFile;
			
		} catch (Exception e) {
			FXDialogUtils.showExceptionDialog("Unable to load the file", "Path: " + path, e);
			clearConfigEntries();
			return;
		}
		
	}
	
	
	private void clearConfigEntries() {
		contentPanel.getChildren().forEach(entry -> ((Label)((ParameterPanel)entry).getChildren().get(0)).minWidthProperty().unbind());
		contentPanel.getChildren().clear();
		minLabelSize.setValue(0);
	}
	
	private void addConfigEntry(Parameter p) {
		contentPanel.getChildren().add(ParameterPanel.fromParameter(this, contentPanel.getChildren().size(), p));
	}
	
	
	public void setFilePathAndLoad(String path) {
		textField.setText(path);
		loadFile(path);
	}
	
	
	
	public void onContentModify() {
		setSaved(false);
		if (autosave)
			saveFile();
	}
	
	private void setSaved(boolean s) {
		saved = s;
		updateSaveButton();
	}
	
	
	private void setAutosave(boolean as) {
		autosave = as;
		updateSaveButton();
		if (autosave && !saved) {
			saveFile();
		}
	}
	
	
	private void updateSaveButton() {
		btnSave.setDisable(autosave || saved);
	}
	
}
