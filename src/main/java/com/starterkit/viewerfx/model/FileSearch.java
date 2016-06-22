package com.starterkit.viewerfx.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class FileSearch {
	private final StringProperty fileName = new SimpleStringProperty();
	private final StringProperty fileType = new SimpleStringProperty();
	private final ListProperty<FileSearch> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));
	
	public String getFileName() {
		return fileName.get();
	}
	public void setFileName(String value) {
		fileName.set(value);
	}
	public StringProperty fileNameProperty() {
		return fileName;
	}
	public String getFileType() {
		return fileType.get();
	}
	public void setFileType(String value) {
		fileType.set(value);
	}
	public StringProperty fileTypeProperty() {
		return fileType;
	}
	public ListProperty<FileSearch> resultProperty() {
		return result;
	}
	public List<FileSearch> getResult() {
		return result.get();
	}
	public void setResult(List<FileSearch> value) {
		result.setAll(value);
	}
}
