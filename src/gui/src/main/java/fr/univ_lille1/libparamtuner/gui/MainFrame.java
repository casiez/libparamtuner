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
package fr.univ_lille1.libparamtuner.gui;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import fr.univ_lille1.libparamtuner.gui.parameters_panel.ParameterPanel;
import fr.univ_lille1.libparamtuner.parameters.Parameter;
import fr.univ_lille1.libparamtuner.parameters.ParameterFile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainFrame extends Application {
	
	private Stage stage;
	private Scene scene;
	
	final FileChooser fileChooser = new FileChooser();
	private VBox contentPanel;
	public ScrollPane contentScroll; // TODO private
	private MenuItem btnSave;
	Menu openrecent;

	public DoubleProperty minLabelSize = new SimpleDoubleProperty(0); // TODO private
	
	private File file;
	private ParameterFile loadedFile = null;
	private boolean saved = true;
	private boolean autosave = true;
	
	private SaveThread saveThread = new SaveThread();

	private Preferences prefs;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icon.png")));
		
		stage.setOnCloseRequest((event) -> {
			if (confirmSaveBeforeClosingFile()) {
				stage.hide();
				try {
					prefs.sync();
				} catch (BackingStoreException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			}
			else {
				event.consume();
			}
		});

		// Save settings in a persistent way
		prefs = Preferences.userRoot().node(this.getClass().getName());
		if (getParameters().getRaw().size() > 0)
			file = new File(getParameters().getRaw().get(0));
		else
			file = new File(prefs.get("lastpath",""));
		autosave = prefs.getBoolean("autosave", true);
		
		BorderPane globalPanel = new BorderPane();
		
		double windowWidth = prefs.getDouble(getCode(file.getPath()) + "width", 300);
		double windowHeight = prefs.getDouble(getCode(file.getPath()) + "height", 300);
		
		scene = new Scene(globalPanel, windowWidth, windowHeight);
		stage.setScene(scene);
		stage.sizeToScene();
		stage.centerOnScreen();
		
		stage.setMinWidth(250);
		stage.setMinHeight(250);

		
		MenuBar menuBar = new MenuBar();
		menuBar.setUseSystemMenuBar(true);
		
		// File
		Menu menu1 = new Menu("File");
		KeyCombination krfile = new KeyCodeCombination(KeyCode.F, KeyCombination.ALT_DOWN);
		menu1.setAccelerator(krfile);
		
		// Open...
		MenuItem open = new MenuItem("Open...");
		KeyCombination kropen = new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN);
		open.setAccelerator(kropen);
		open.setOnAction(e -> {
			File f = fileChooser.showOpenDialog(stage);
			if (f != null) {
				setFilePathAndLoad(f.getPath());
			}
		});
		
		// Open recent
		//openrecent = new Menu("Open Recent");
		SeparatorMenuItem smi = new SeparatorMenuItem();
		
		// Save
		btnSave = new MenuItem("Save");
		KeyCombination krsave = new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN);
		btnSave.setAccelerator(krsave);		
		btnSave.setOnAction(e -> saveFile());
		btnSave.setDisable(true);
		
		// Auto save
		CheckMenuItem chckbxAutosave = new CheckMenuItem("Auto save");
		chckbxAutosave.setSelected(autosave);
		chckbxAutosave.setOnAction(e -> setAutosave(chckbxAutosave.isSelected()));
		SeparatorMenuItem smi2 = new SeparatorMenuItem();
		
		// Revert file
		MenuItem btnRevert = new MenuItem("Revert file");
		KeyCombination krrevert = new KeyCodeCombination(KeyCode.R, KeyCombination.SHORTCUT_DOWN);
		btnRevert.setAccelerator(krrevert);		
		btnRevert.setOnAction(e -> {
			/* manually set 'saved' to true to avoid dialog that ask for saving while
			 * we wan't to revert file (useless to revert just after saving)
			 */
			saved = true;
			loadFile(file);
		});
		
		menu1.getItems().addAll(open, smi, btnSave, chckbxAutosave, smi2, btnRevert);
		
		// Help
		Menu menu2 = new Menu("Help");
		
		// About
		MenuItem btnAbout = new MenuItem("About");
		btnAbout.setOnAction(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText(null);
			alert.setContentText("ParamTunerGUI version 1.1\n\nParamTunerGUI is part of libParamTuner\nGet more information on https://github.com/casiez/libparamtuner");
			
			alert.showAndWait();
		});
		menu2.getItems().add(btnAbout);
		menuBar.getMenus().addAll(menu1, menu2);
		globalPanel.setTop(menuBar);
		
		fileChooser.setInitialDirectory(new File("."));
		ExtensionFilter xmlFilter = new ExtensionFilter("XML Files", "*.xml");
		fileChooser.getExtensionFilters().add(xmlFilter);
		fileChooser.getExtensionFilters().add(new ExtensionFilter("All files", "*"));
		fileChooser.setSelectedExtensionFilter(xmlFilter);
		
		
		contentScroll = new ScrollPane();
		contentScroll.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		//contentScroll.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		globalPanel.setCenter(contentScroll);
		
		contentPanel = new VBox();
		contentScroll.setContent(contentPanel);
		contentScroll.setFitToWidth(true);

		
		scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
		        prefs.putDouble(getCode(file.getPath()) + "width", (double)newSceneWidth);
		});
		
		scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {
		    prefs.putDouble(getCode(file.getPath()) + "height", (double)newSceneHeight);
		});
		
		saveThread.start();
		
		loadFile(file);
		updateTitle();
		stage.show();
	}
	
	private static String getCode(String filepath) {
		String res = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte bytes[] = md.digest(filepath.getBytes("ISO-8859-1"));
			res = new String(bytes, "ISO-8859-1");
			res = res.substring(0, 10); 
			//System.out.println(res);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private void saveFile() {
		if (saved || loadedFile == null)
			return;
		
		saveThread.askForSave();
		
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
		String ret = FXDialogUtils.showConfirmDialog("Save current file ?", null, "Do you want to save the current file?",
				"Yes", "No", "Cancel");
		if (ret == null || ret.equals("Cancel"))
			return false;
		if (ret.equals("Yes"))
			saveFile();
		return true;
	}
	
	public void loadFile(File f) {
		
		if (new File("").equals(f)) {
			String value = FXDialogUtils.showConfirmDialog("Error", null, "Path not specified. Do you want to open?", "Yes", "No");
			if ("Yes".equals(value)) {
				f = fileChooser.showOpenDialog(stage);
				if (f != null)
					setFilePathAndLoad(f.getPath());
			}
			return;
		}
		
		if (!confirmSaveBeforeClosingFile())
			return;
		
		clearConfigEntries();
		
		
		ParameterFile pFile;
		prefs.put("lastpath", f.getPath());
		
		
		try {
			pFile = new ParameterFile(f.getPath(), true);
			
			for (Parameter p : pFile.getAll()) {
				addConfigEntry(p);
			}
			
			
			
			
			loadedFile = pFile;
			
		} catch (Exception e) {
			e.printStackTrace();
			clearConfigEntries();
			String value = FXDialogUtils.showConfirmDialog("Unable to load the file", "Path: " + f.getPath(), e.getMessage()+"\n\nDo you want to open another file?", "Yes", "No");
			if ("Yes".equals(value)) {
				f = fileChooser.showOpenDialog(stage);
				if (f != null)
					setFilePathAndLoad(f.getPath());
			}
			return;
		}
		
	}
	
	private void clearConfigEntries() {
		contentPanel.getChildren().forEach(entry -> ((Label)((HBox)((ParameterPanel)entry).getCenter()).getChildren().get(0)).minWidthProperty().unbind());
		contentPanel.getChildren().clear();
		minLabelSize.setValue(0);
	}
	
	private void addConfigEntry(Parameter p) {
		contentPanel.getChildren().add(ParameterPanel.fromParameter(this, contentPanel.getChildren().size(), p));
	}
	
	private void setFileAndLoad(File f) {
		file = f;
		updateTitle();
		loadFile(file);
	}
	
	public void setFilePathAndLoad(String path) {
		setFileAndLoad(new File(path.trim()));
	}
	
	public void onContentModify() {
		setSaved(false);
		if (autosave)
			saveFile();
	}
	
	private void setSaved(boolean s) {
		saved = s;
		updateTitle();
		updateSaveButton();
	}
	
	private void updateTitle() {
		String t = "ParamTuner GUI";
		if (file != null && !new File("").equals(file)) {
			t = file.getName() + " - " + t;
			if (!saved)
				t = "*" + t;
		}
		stage.setTitle(t);
	}
	
	private void setAutosave(boolean as) {
		autosave = as;
		prefs.putBoolean("autosave", as);
		updateSaveButton();
		if (autosave && !saved) {
			saveFile();
		}
	}
		
	private void updateSaveButton() {
		btnSave.setDisable(autosave || saved);
	}
	
	private class SaveThread extends Thread {
		
		private AtomicBoolean wantToSave = new AtomicBoolean(false);
		
		@Override
		public void run() {
			try {
				for(;;) {
					AtomicUtils.waitForValue(wantToSave, true, 50);
					wantToSave.set(false);
					
					try {
						loadedFile.save();
					} catch (Exception e) {
						Platform.runLater(() -> FXDialogUtils.showExceptionDialog("Unable to save the file", "Path: " + loadedFile.file, e));
						return;
					}
					
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				return;
			}
		}
		
		public void askForSave() {
			wantToSave.set(true);
		}	
	}
}
