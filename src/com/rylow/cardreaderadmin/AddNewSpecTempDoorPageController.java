package com.rylow.cardreaderadmin;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AddNewSpecTempDoorPageController implements Initializable {
	
	@FXML
	Label lblDoorName;
	
	@FXML
	ChoiceBox<String> cboxToH, cboxToM;
	
	@FXML
	ComboBox<String> cboxTH;
	
	@FXML
	Button bntSave, bntCancel;
	
	@FXML
	RadioButton rbGuest, rbFamily, rbStaff, rbStudent;
	
	@FXML
	final ToggleGroup rbGroup = new ToggleGroup();
	
	private String doorName;
	
	private int doorID;
	
	private static ObservableList<String> hoursList = FXCollections.observableArrayList();
	private static ObservableList<String> minutesList = FXCollections.observableArrayList();
	
	private DoorPageController dpg;
	
	private int button = 0;
	
	private String filter = "";
	private ObservableList<String> tempList = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		rbGuest.setToggleGroup(rbGroup);
		rbGuest.setUserData("guest");
		rbFamily.setToggleGroup(rbGroup);
		rbFamily.setUserData("family");
		rbStaff.setToggleGroup(rbGroup);
		rbStaff.setUserData("staff");
		rbStudent.setToggleGroup(rbGroup);
		rbStudent.setUserData("student");
		
		rbFamily.setSelected(true);
		
		cboxTH.setItems(SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682")));
		cboxTH.getSelectionModel().selectFirst();
		
		hoursList.removeAll(hoursList);
		minutesList.removeAll(minutesList);
		
		for (int i = 1; i < 8; i++){
			
			/*if (i < 10)
				hoursList.add("0" + String.valueOf(i));
			else*/
				hoursList.add(String.valueOf(i));
		}
		
		
		cboxToH.setItems(hoursList);
		
		
		cboxToH.getSelectionModel().selectFirst();
		
		cboxTH.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode().isLetterKey())  {
		            
		        	filter += keyEvent.getText();
	
		        	ArrayList<String> tempList2 = new ArrayList<String>(cboxTH.getItems());
		        	
		        	
		        	tempList.removeAll(tempList);
		        	for (String s : tempList2){
		        		
		        		if (s.toLowerCase().contains(filter.toLowerCase())){
		        			tempList.add(s);

		        		}
		        		
		        	}
		        	
		        	cboxTH.setItems(tempList);
		        	
		        }
		        else{
		        	if (keyEvent.getCode().equals(KeyCode.BACK_SPACE))  {
		        		
		        		if (filter.length() > 0){
		        			filter = filter.substring(0, filter.length()-1);

		        			ObservableList<String> tempList2 = null;
				        	
		        			switch (String.valueOf(rbGroup.getSelectedToggle().getUserData())){
		        			
		        				case "family" : tempList2 = SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682"));
		        								break;
		        				case "guest" :  tempList2 = SQLConnector.fillGuestsList();
		        								break;
		        				case "student" : tempList2 = SQLConnector.fillStudentsList(true);
		        								 break;
		        				case "staff" : tempList2 = SQLConnector.fillStaffList(true);
		        							   break;
	        			
		        			}
				        	
				        	tempList.removeAll(tempList);
				        	for (String s : tempList2){
				        		
				        		if (s.toLowerCase().contains(filter.toLowerCase())){
				        			tempList.add(s);

				        		}
				        		
				        	}
				        	
				        	cboxTH.setItems(tempList);
		        			
		        			

			        		if (filter.length() == 0){
			        			
			        			cboxTH.setItems(tempList2);
			        			
			        		}
		        			
		        		}
		        	}
		        }
		    }
		});
		
		
	}
	
	@FXML
	public void rbGuestOnAction(ActionEvent event){
		
		filter = "";
		cboxTH.setItems(SQLConnector.fillGuestsList());
		cboxTH.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbFamilyOnAction(ActionEvent event){
		
		filter = "";
		cboxTH.setItems(SQLConnector.fillFamiliesList(SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682")));
		cboxTH.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbStaffOnAction(ActionEvent event){
		
		filter = "";
		cboxTH.setItems(SQLConnector.fillStaffList(true));
		cboxTH.getSelectionModel().selectFirst();
	}
	
	@FXML
	public void rbStudentOnAction(ActionEvent event){
		
		filter = "";
		cboxTH.setItems(SQLConnector.fillStudentsList(true));
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

		
		Date curDate = new Date();
		Calendar curCalendar = Calendar.getInstance();
		curCalendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(cboxToH.getSelectionModel().getSelectedItem()));
		
		int to = Integer.valueOf(new SimpleDateFormat("HH").format(curCalendar.getTime())) * 60 + Integer.valueOf(new SimpleDateFormat("mm").format(curCalendar.getTime()));   //cboxToH.getSelectionModel().getSelectedItem()) * 60 + Integer.valueOf(cboxToM.getSelectionModel().getSelectedItem());
		
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
		
		if (String.valueOf(rbGroup.getSelectedToggle().getUserData()).equals("student"))
			SQLConnector.addNewSpecTempDoorSchedule(new DoorSchedule(cboxTH.getSelectionModel().getSelectedItem(), "4", String.valueOf(to), ""), doorID);
		
		
		
		//}

		dpg.tblTempAccess.setItems(SQLConnector.fillDoorSpecTempSecGroupTable(doorID));
		
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
