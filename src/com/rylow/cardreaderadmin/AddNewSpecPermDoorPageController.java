package com.rylow.cardreaderadmin;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class AddNewSpecPermDoorPageController implements Initializable {
	
	@FXML
	Label lblDoorName;
	
	@FXML
	ChoiceBox<String> cboxFN, cboxFromH, cboxFromM, cboxToH, cboxToM;
	
	@FXML
	CheckBox checkMon, checkTue, checkWed, checkThu, checkFri, checkSat, checkSun;
	
	@FXML
	Button bntSave, bntCancel;
	
	private String doorName;
	
	private int doorID;
	
	private static ObservableList<String> hoursList = FXCollections.observableArrayList();
	private static ObservableList<String> minutesList = FXCollections.observableArrayList();
	
	private DoorPageController dpg;
	
	private int button = 0;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		hoursList.removeAll(hoursList);
		minutesList.removeAll(minutesList);
		
		for (int i = 0; i < 24; i++){
			
			if (i < 10)
				hoursList.add("0" + String.valueOf(i));
			else
				hoursList.add(String.valueOf(i));
		}
		
		for (int i = 0; i < 60; i++){
			
			if (i < 10)
				minutesList.add("0" + String.valueOf(i));
			else
				minutesList.add(String.valueOf(i));
			
			i = i + 9;
		}
		
		minutesList.add("59");
		
		cboxFromH.setItems(hoursList);
		cboxToH.setItems(hoursList);
		
		cboxFromM.setItems(minutesList);
		cboxToM.setItems(minutesList);
		
		cboxFromH.getSelectionModel().selectFirst();
		cboxFromM.getSelectionModel().selectFirst();
		cboxToH.getSelectionModel().selectFirst();
		cboxToM.getSelectionModel().selectFirst();
		
		cboxFN.setItems(SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682")));
		cboxFN.getSelectionModel().selectFirst();
		
	}

	public String getDoorName() {
		return doorName;
	}

	public void setDoorName(String doorName) {
		this.doorName = doorName;
		lblDoorName.setText(doorName);
	}
	
	@FXML
	public void btnSaveOnAction(ActionEvent event){

		int from = Integer.valueOf(cboxFromH.getSelectionModel().getSelectedItem()) * 60 + Integer.valueOf(cboxFromM.getSelectionModel().getSelectedItem());
		int to = Integer.valueOf(cboxToH.getSelectionModel().getSelectedItem()) * 60 + Integer.valueOf(cboxToM.getSelectionModel().getSelectedItem());
		
		String daySequence = "";
		
		if (checkMon.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkTue.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkWed.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkThu.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkFri.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkSat.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		if (checkSun.isSelected())
			daySequence += "1";
		else
			daySequence += "0";
		
		switch (button){
		
		case (1):SQLConnector.addNewSpecPermDoorSchedule(new DoorSchedule(cboxFN.getSelectionModel().getSelectedItem(), String.valueOf(from), String.valueOf(to), daySequence), doorID);
				 break;
		
		}

		dpg.tblSpecialAccess.setItems(SQLConnector.fillDoorSpecPermSecGroupTable(doorID));
		
		
		((Stage) cboxFromH.getScene().getWindow()).close();
		
	}

	public int getDoorID() {
		return doorID;
	}

	public void setDoorID(int doorID) {
		this.doorID = doorID;
	}

	public DoorPageController getDpg() {
		return dpg;
	}

	public void setDpg(DoorPageController dpg) {
		this.dpg = dpg;
	}

	public int getButton() {
		return button;
	}

	public void setButton(int button) {
		this.button = button;
	}

}
