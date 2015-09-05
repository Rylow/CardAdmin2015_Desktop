package com.rylow.cardreaderadmin;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AddNewSpecTempDoorPageController implements Initializable {
	
	@FXML
	Label lblDoorName;
	
	@FXML
	ChoiceBox<String> cboxTH, cboxToH, cboxToM;
	
	@FXML
	Button bntSave, bntCancel;
	
	@FXML
	RadioButton rbGuest, rbFamily, rbStaff;
	
	@FXML
	final ToggleGroup rbGroup = new ToggleGroup();
	
	private String doorName;
	
	private int doorID;
	
	private static ObservableList<String> hoursList = FXCollections.observableArrayList();
	private static ObservableList<String> minutesList = FXCollections.observableArrayList();
	
	private DoorPageController dpg;
	
	private int button = 0;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		rbGuest.setToggleGroup(rbGroup);
		rbGuest.setUserData("guest");
		rbFamily.setToggleGroup(rbGroup);
		rbFamily.setUserData("family");
		rbStaff.setToggleGroup(rbGroup);
		rbStaff.setUserData("staff");
		
		rbFamily.setSelected(true);
		
		cboxTH.setItems(SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682")));
		cboxTH.getSelectionModel().selectFirst();
		
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
		
		cboxToH.setItems(hoursList);
		
		cboxToM.setItems(minutesList);
		
		cboxToH.getSelectionModel().selectFirst();
		cboxToM.getSelectionModel().selectFirst();
		
		
	}
	
	@FXML
	public void rbGuestOnAction(ActionEvent event){
		
		cboxTH.setItems(SQLConnector.fillGuestsList());
		cboxTH.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbFamilyOnAction(ActionEvent event){
		
		cboxTH.setItems(SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682")));
		cboxTH.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbStaffOnAction(ActionEvent event){
		
		cboxTH.setItems(SQLConnector.fillStaffList());
		cboxTH.getSelectionModel().selectFirst();
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

		int to = Integer.valueOf(cboxToH.getSelectionModel().getSelectedItem()) * 60 + Integer.valueOf(cboxToM.getSelectionModel().getSelectedItem());
		
		//switch (button){
		
		if (String.valueOf(rbGroup.getSelectedToggle().getUserData()).equals("family"))
			SQLConnector.addNewSpecTempDoorSchedule(new DoorSchedule(cboxTH.getSelectionModel().getSelectedItem(), "1", String.valueOf(to), ""), doorID);
		
		if (String.valueOf(rbGroup.getSelectedToggle().getUserData()).equals("staff"))
			SQLConnector.addNewSpecTempDoorSchedule(new DoorSchedule(cboxTH.getSelectionModel().getSelectedItem(), "2", String.valueOf(to), ""), doorID);
		
		if (String.valueOf(rbGroup.getSelectedToggle().getUserData()).equals("guest")){
			if (cboxTH.getSelectionModel().getSelectedItem() != null){
				
				String cardID = cboxTH.getSelectionModel().getSelectedItem();
				cardID = cardID.substring(cardID.length()-2, cardID.length());
				
				SQLConnector.addNewSpecTempDoorSchedule(new DoorSchedule(cardID, "3", String.valueOf(to), ""), doorID);
			}
		}
		
		
		
		//}

		dpg.tblTempAccess.setItems(SQLConnector.fillDoorSpecTempSecGroupTable(doorID));;
		
		((Stage) cboxToH.getScene().getWindow()).close();
		
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
