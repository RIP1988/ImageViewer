package com.starterkit.viewerfx.controller;

import java.io.File;


import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


import org.apache.log4j.Logger;

import com.starterkit.viewerfx.model.FileSearch;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.DirectoryChooser;

public class ViewerController {

	private static final Logger LOG = Logger.getLogger(ViewerController.class);
	private final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
	private final String[] acceptedExtensions = {"png", "jpg", "tif", "gif", "bmp"};
	private Timer timer;
	private List<File> files = new ArrayList<File>();
	private File file;
	
	private FileSearch model = new FileSearch();
	
	@FXML
	private ResourceBundle resources;
	
	@FXML
	private Button directoryButton;
	
	@FXML
	private Button slideShowButton;
	
	@FXML
	private Button slideShowEndButton;
	
	@FXML
	private Button previousButton;
	
	@FXML
	private Button nextButton;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private TableView<FileSearch> resultTable;
	
	@FXML
	private TableColumn<FileSearch, String> fileNameColumn;
	
	@FXML
	private TableColumn<FileSearch, String> fileTypeColumn;

	@FXML
	private ImageView imageView;

	@FXML
	private void initialize() {
		LOG.debug("initializing application...");
		initializeResultTable();
		imageView.preserveRatioProperty().set(true);
		scrollPane.setPannable(true);
		// REV: dlaczego InvalidationListener, a nie ChangeListener?
		zoomProperty.addListener(new InvalidationListener() {
			@Override
            public void invalidated(Observable arg0) {
                imageView.setFitWidth(zoomProperty.get() * 4);
                imageView.setFitHeight(zoomProperty.get() * 3);
            }
		});
	
		// REV: dlaczego EventFilter, a nie EventHandler?
		scrollPane.addEventFilter(ScrollEvent.ANY,
                new EventHandler<ScrollEvent>() {
                     @Override
                     public void handle(ScrollEvent event) {
                    	 LOG.debug("Scrolling for zoom...");
                          if (event.getDeltaY() > 0) {
                               zoomProperty.set(zoomProperty.get() * 1.1);
                          } else if (event.getDeltaY() < 0) {
                               zoomProperty.set(zoomProperty.get() / 1.1);
                          }
                          event.consume();
                     }
                });
		
		// REV: lepiej zrobic to przez listenera na selectedItem
		resultTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				int index = resultTable.getSelectionModel().getSelectedIndex();
					file = files.get(index);
					setImage();
			}
			
		});

		LOG.debug("Application initialized.");
	}

	
	private void initializeResultTable() {
		fileNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFileName()));
		fileTypeColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getFileType()));
		resultTable.itemsProperty().bind(model.resultProperty());
	}

		@FXML
	private void directoryButtonAction(ActionEvent event) {
		LOG.debug("Choose directory button clicked.");
		DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = 
        		// REV: okno wyboru katalogu powinno byc modalne
                directoryChooser.showDialog(null);
         
        if(selectedDirectory == null){
        	// REV: zmien warunek
        }
        else{
        	String extension = "";
        	// REV: listFiles() moze przefiltrowac pliki
        	File[] allFiles = selectedDirectory.listFiles();
        	for (File file: allFiles) {
        		int i = file.getName().lastIndexOf('.');
        		if (i > 0) {
            	    extension = file.getName().substring(i+1);
        		}
        		for (String acceptedExtension: acceptedExtensions) {
        			if (acceptedExtension.equals(extension)) {
        				files.add(file);
        				break;
        			}
        		}
        	}
            file = files.get(0);
            setImage();
            
            List<FileSearch> loadedFiles = new ArrayList<FileSearch>();
            
            for (File file: files) {
            	extension = "";
            	String name = "";

            	int i = file.getName().lastIndexOf('.');
            	if (i > 0) {
            	    extension = file.getName().substring(i+1);
            	    name = file.getName().substring(0, i);
            	}
            	FileSearch loadedFile = new FileSearch();
            	loadedFile.setFileName(name);
            	loadedFile.setFileType(extension);
            	loadedFiles.add(loadedFile);
            }
            model.setResult(loadedFiles);
        }
        
	}
		
		
		@FXML
		private void slideShowButtonAction(ActionEvent event) {
			LOG.debug("Slide show button clicked");
			timer = new Timer();
			timer.schedule(new TimerTask(){
				@Override
				public void run() {
					// REV: przekazanie null do handlera to zly pomysl, aktualizacja UI powinna byc wykonana w watku JavaFX
					nextButtonAction(null);
				}
			}, 0, 2000);
		}
		
		@FXML
		private void slideShowEndButtonAction(ActionEvent event) {
			LOG.debug("End slide show button clicked");
			timer.cancel();
		}
		
		@FXML
		private void previousButtonAction(ActionEvent event) {
			LOG.debug("Previous image button clicked");
			int currentIndex = files.indexOf(file);
			if (currentIndex==0) {
				file=files.get(files.size()-1);
				setImage();
			}
			else {
				file=files.get(currentIndex-1);
				setImage();
			}
		}
		
		@FXML
		private void nextButtonAction(ActionEvent event) {
			LOG.debug("Next image button clicked");
			int currentIndex = files.indexOf(file);
			if (currentIndex==files.size()-1) {
				file=files.get(0);
				setImage();
			}
			else {
				file=files.get(currentIndex+1);
				setImage();
			}
		}
		
		private void setImage() {
			LOG.debug("Setting new image");
			Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            // REV: to nie bedzie konieczne, gdy poprawisz FXMLa
            scrollPane.setContent(imageView);
		}
	

}

