package com.rylow.cardreaderadmin;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DoorPageController implements Initializable {
	
	@FXML
	ListView<Door> listDoor;
	
	@FXML
	Button btnAddDoor, btnRemoveDoor, btnSGAdd, btnSGEdit, btnSGRemove, btnSAAdd, btnSAEdit, btnSARemove, btnTempAdd, btnTempRemove;
	
	@FXML
	Label lblDoorName, lblID;
	
	@FXML
	TableView<DoorSchedule> tblSecGroup, tblSpecialAccess, tblTempAccess;
	
	@FXML
	TableColumn<DoorSchedule, String> tColSG, tColSGFrom, tColSGTo, tColSGOn, tColSAName, tColSAFrom, tColSATo, tColSAOn, tColTempName, tColTempFrom, tColTempTo, tColTempOn;
	
	private DoorPageController dpg = this;
		
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		tColSG.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("name"));
		tColSGFrom.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("from"));
		tColSGTo.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("to"));
		tColSGOn.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("dayType"));
		
		tColSAName.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("name"));
		tColSAFrom.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("from"));
		tColSATo.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("to"));
		tColSAOn.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("dayType"));
		
		tColTempName.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("name"));
		tColTempFrom.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("from"));
		tColTempTo.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("to"));
		tColTempOn.setCellValueFactory(new PropertyValueFactory<DoorSchedule, String>("dayType"));
		
		listDoor.setItems(SQLConnector.fillDoorList());
		listDoor.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Door>() {
	                public void changed(ObservableValue<? extends Door> ov, 
	                    Door old_val, Door new_val) {

	                		showDoor(new_val);
	            }
	     });
		listDoor.getSelectionModel().selectFirst();
		
		btnSGAdd.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		                try {
			                final Stage dialog = new Stage();
			                dialog.initModality(Modality.APPLICATION_MODAL);
			                dialog.initOwner((Stage) listDoor.getScene().getWindow());
			                FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewSGDoorRulePage.fxml"));
			                //AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("addNewSGDoorRulePage.fxml"));
			                Scene dialogScene = new Scene((AnchorPane) loader.load(), 800, 300);
			                
			                addNewSGDoorPageController controller = loader.getController();
			                controller.setDoorName(lblDoorName.getText());
			                controller.setDoorID(Integer.valueOf(lblID.getText()));
			                controller.setDpg(dpg);
			                controller.setButton(1);
			                
			                dialog.setResizable(false);
			                dialog.setScene(dialogScene);
			                dialog.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		            }
		         });
		
		btnSGRemove.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {

		            	DoorSchedule doorSchedule = tblSecGroup.getSelectionModel().getSelectedItem();
		            	
		            	if (doorSchedule != null){
		            	
			            	String from = String.valueOf(Integer.valueOf(doorSchedule.getFrom().substring(0, 2)) * 60 + Integer.valueOf(doorSchedule.getFrom().substring(3,5)));
			            	String to = String.valueOf(Integer.valueOf(doorSchedule.getTo().substring(0, 2)) * 60 + Integer.valueOf(doorSchedule.getTo().substring(3,5)));
			            	
			            	SQLConnector.removeDoorSgSchedule(new DoorSchedule(doorSchedule.getName(), from, to, doorSchedule.getDayType()), Integer.valueOf(lblID.getText()));
			            	
			            	tblSecGroup.setItems(SQLConnector.fillDoorSecGroupTable(Integer.valueOf(lblID.getText())));
		            	}

		            }
		         });
		
		
		btnSAAdd.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		                try {
			                final Stage dialog = new Stage();
			                dialog.initModality(Modality.APPLICATION_MODAL);
			                dialog.initOwner((Stage) listDoor.getScene().getWindow());
			                FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewSpecPermDoorRulePage.fxml"));
			                //AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("addNewSGDoorRulePage.fxml"));
			                Scene dialogScene = new Scene((AnchorPane) loader.load(), 800, 300);
			                
			                AddNewSpecPermDoorPageController controller = loader.getController();
			                controller.setDoorName(lblDoorName.getText());
			                controller.setDoorID(Integer.valueOf(lblID.getText()));
			                controller.setDpg(dpg);
			                controller.setButton(1);
			                
			                dialog.setResizable(false);
			                dialog.setScene(dialogScene);
			                dialog.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		            }
		         });
		
		
		btnSARemove.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {

		            	DoorSchedule doorSchedule = tblSpecialAccess.getSelectionModel().getSelectedItem();
		            	
		            	if (doorSchedule != null){
		            	
			            	String from = String.valueOf(Integer.valueOf(doorSchedule.getFrom().substring(0, 2)) * 60 + Integer.valueOf(doorSchedule.getFrom().substring(3,5)));
			            	String to = String.valueOf(Integer.valueOf(doorSchedule.getTo().substring(0, 2)) * 60 + Integer.valueOf(doorSchedule.getTo().substring(3,5)));
			            	
			            	String daySequence = "";
			            	
			            	if (doorSchedule.getDayType().contains("Mon"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Tue"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Wed"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Thu"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Fri"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Sat"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	if (doorSchedule.getDayType().contains("Sun"))
			            		daySequence += "1";
			            	else
			            		daySequence += "0";
			            	
			            	SQLConnector.removeSpecPermDoorSchedule(new DoorSchedule(doorSchedule.getName(), from, to, daySequence), Integer.valueOf(lblID.getText()));
			            	
			            	tblSpecialAccess.setItems(SQLConnector.fillDoorSpecPermSecGroupTable(Integer.valueOf(lblID.getText())));
			            	tblSpecialAccess.getSelectionModel().clearSelection();
		            	}
		            	else{
		            		System.out.println("NULL");
		            	}

		            }
		         });
		
		btnTempAdd.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {
		                try {
			                final Stage dialog = new Stage();
			                dialog.initModality(Modality.APPLICATION_MODAL);
			                dialog.initOwner((Stage) listDoor.getScene().getWindow());
			                FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewSpecTempDoorRulePage.fxml"));
			                //AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("addNewSGDoorRulePage.fxml"));
			                Scene dialogScene = new Scene((AnchorPane) loader.load(), 800, 300);
			                
			                AddNewSpecTempDoorPageController controller = loader.getController();
			                controller.setDoorName(lblDoorName.getText());
			                controller.setDoorID(Integer.valueOf(lblID.getText()));
			                controller.setDpg(dpg);
			                controller.setButton(1);
			                
			                dialog.setResizable(false);
			                dialog.setScene(dialogScene);
			                dialog.show();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

		            }
		         });
		
		btnTempRemove.setOnAction(
		        new EventHandler<ActionEvent>() {
		            @Override
		            public void handle(ActionEvent event) {

		            	DoorSchedule doorSchedule = tblTempAccess.getSelectionModel().getSelectedItem();
		            	
		            	if (doorSchedule != null){
		            	
		            		int type = 0;
		            		String name = "";
		            		
		            		switch (doorSchedule.getDayType()){
		            		
		            		case "Family" : type = 1;
		            						name = doorSchedule.getName();
		            						break;
		            		case "Staff" : type = 2;
		            						name = doorSchedule.getName();
		            						break;
		            		case "Guest" : type = 3;
		            						name = doorSchedule.getName().substring(doorSchedule.getName().length()-2);
		            						break;
		            		
		            		}
		            		
			            			            	
			            	SQLConnector.removeDoorTempSchedule(new DoorSchedule(name, "", "", String.valueOf(type)), Integer.valueOf(lblID.getText()));
			            	
			            	tblTempAccess.setItems(SQLConnector.fillDoorSpecTempSecGroupTable(Integer.valueOf(lblID.getText())));
			            	tblTempAccess.getSelectionModel().clearSelection();
		            	}

		            }
		         });
		
	}
	
	
	
	private void showDoor(Door door){
		
		lblDoorName.setText(door.getName());
		lblID.setText(String.valueOf(door.getId()));
		
		tblSecGroup.setItems(SQLConnector.fillDoorSecGroupTable(door.getId()));
		tblSpecialAccess.setItems(SQLConnector.fillDoorSpecPermSecGroupTable(door.getId()));
		tblTempAccess.setItems(SQLConnector.fillDoorSpecTempSecGroupTable(door.getId()));
	}
	
	
	

}
