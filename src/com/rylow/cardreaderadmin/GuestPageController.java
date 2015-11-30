package com.rylow.cardreaderadmin;

import java.net.URL;
import java.sql.Connection;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;

public class GuestPageController implements Initializable {
	
	@FXML
	RadioButton rbGuest, rbFam, rbStaff;
	
	@FXML
	TextField txtName;
	
	@FXML
	ChoiceBox<GuestCard> cboxCard;
	
	@FXML
	ComboBox<String> cboxStaff, cboxFamily;
	
	@FXML
	Button btnAssign, btnRelease;
	
	private static Connection conn;
	
	@FXML
	final ToggleGroup rbGroup = new ToggleGroup();
	
	@FXML
	ListView<String> listTempCards;
	
	private Timer restartTimer = new Timer();

	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		conn = SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		rbGuest.setToggleGroup(rbGroup);
		rbGuest.setUserData("guest");
		rbFam.setToggleGroup(rbGroup);
		rbFam.setUserData("family");
		rbStaff.setToggleGroup(rbGroup);
		rbStaff.setUserData("staff");
		
		cboxStaff.setDisable(true);
		cboxFamily.setDisable(true);
		txtName.setDisable(true);
		
		rbGuest.setSelected(true);
		cboxStaff.setDisable(true);
		cboxFamily.setDisable(true);
		txtName.setDisable(false);
		cboxCard.setItems(SQLConnector.fillCardCbox(33));
		cboxCard.getSelectionModel().selectFirst();
		
		listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
		
		restartTimer.scheduleAtFixedRate(new TimerTask() {

	        public void run() {
	        	
	        	Thread.currentThread().setName("Temporary Table Refresh");
	        	Platform.runLater(new Runnable(){
	    	        @Override
	    	        public void run() {
	    	        	listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
	    	        }
	    	    });
	        	

	        }
	    }, 30000, 5000);
		
	}
	
	
	
	@FXML
	public void rbGuestOnAction(ActionEvent event){
		
		cboxStaff.setDisable(true);
		cboxFamily.setDisable(true);
		txtName.setDisable(false);
		cboxCard.setItems(SQLConnector.fillCardCbox(33));
		cboxCard.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbFamOnAction(ActionEvent event){
		
		cboxStaff.setDisable(true);
		cboxFamily.setDisable(false);
		txtName.setDisable(true);
		cboxCard.setItems(SQLConnector.fillCardCbox(31));
		cboxCard.getSelectionModel().selectFirst();
		cboxFamily.setItems(SQLConnector.fillFamiliesList(conn));
		cboxFamily.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void rbStaffOnAction(ActionEvent event){
		cboxStaff.setDisable(false);
		cboxFamily.setDisable(true);
		txtName.setDisable(true);
		cboxCard.setItems(SQLConnector.fillCardCbox(32));
		cboxCard.getSelectionModel().selectFirst();
		cboxStaff.setItems(SQLConnector.fillStaffList(true));
		cboxStaff.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void btnAssignOnAction(ActionEvent event){
		
		String selected = rbGroup.getSelectedToggle().getUserData().toString();
		
		if (selected.equals("guest")){
			
			if (txtName.getText().equals("")){
				
    			Alert alert1 = new Alert(AlertType.ERROR);
				alert1.setTitle("Error Dialog");
				alert1.setHeaderText("Guest name is empty");
				alert1.setContentText("Please fill in visitor's name.");
				alert1.showAndWait();
				
			}
			else{
				
				if (SQLConnector.isGuestCardIssued(cboxCard.getSelectionModel().getSelectedItem().getId())){
					
	    			Alert alert1 = new Alert(AlertType.CONFIRMATION);
					alert1.setTitle("Confirmation Dialog");
					alert1.setHeaderText("Guest card is already issued");
					alert1.setContentText("This card is already issued to " + SQLConnector.findGuestCardHolder(SQLConnector.findGlobalIDofGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId()))
							+ ". Do you want to reassign this card to " + txtName.getText() + "?");

					Optional<ButtonType> result = alert1.showAndWait();
					if (result.get() == ButtonType.OK){
						
						SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 3);
						SQLConnector.assignGuestGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), txtName.getText());
						listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
						txtName.setText("");
						
					}
					
				}
				else{
					
					Alert alert1 = new Alert(AlertType.CONFIRMATION);
					alert1.setTitle("Confirmation Dialog");
					alert1.setHeaderText("Guest card");
					alert1.setContentText("Are you sure you want to issue " + cboxCard.getSelectionModel().getSelectedItem().getName() + " to  " + txtName.getText() + "?");

					Optional<ButtonType> result = alert1.showAndWait();
					if (result.get() == ButtonType.OK){
						
						SQLConnector.assignGuestGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), txtName.getText());
						listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
						txtName.setText("");
					}
					
				}
				
			}
			
		}
		else{
			if (selected.equals("family")){
				
				if (SQLConnector.isGuestCardIssued(cboxCard.getSelectionModel().getSelectedItem().getId())){
					
	    			Alert alert1 = new Alert(AlertType.CONFIRMATION);
					alert1.setTitle("Confirmation Dialog");
					alert1.setHeaderText("Guest card is already issued");
					alert1.setContentText("This card is already issued to Family " + SQLConnector.findFamilyNameById(SQLConnector.findIssuedGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId()))
							+ ". Do you want to reassign this card to Family " + cboxFamily.getSelectionModel().getSelectedItem().toString() + "?");

					Optional<ButtonType> result = alert1.showAndWait();
					if (result.get() == ButtonType.OK){
						
						SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 1);
						SQLConnector.assignFamilyGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), SQLConnector.findFamilyIdByName(cboxFamily.getSelectionModel().getSelectedItem().toString()));
						listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
						
					}
					
				}
				else{
					
					Alert alert1 = new Alert(AlertType.CONFIRMATION);
					alert1.setTitle("Confirmation Dialog");
					alert1.setHeaderText("Guest card");
					alert1.setContentText("Are you sure you want to issue " + cboxCard.getSelectionModel().getSelectedItem().getName() + " to the Family " + cboxFamily.getSelectionModel().getSelectedItem().toString() + "?");

					Optional<ButtonType> result = alert1.showAndWait();
					if (result.get() == ButtonType.OK){
						
						SQLConnector.assignFamilyGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), SQLConnector.findFamilyIdByName(cboxFamily.getSelectionModel().getSelectedItem().toString()));
						listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
						
					}
					
				}
				
			}
			else{
				if(selected.equals("staff")){
					
					if (SQLConnector.isGuestCardIssued(cboxCard.getSelectionModel().getSelectedItem().getId())){
						
		    			Alert alert1 = new Alert(AlertType.CONFIRMATION);
						alert1.setTitle("Confirmation Dialog");
						alert1.setHeaderText("Guest card is already issued");
						alert1.setContentText("This card is already issued to " + SQLConnector.findStaffById(SQLConnector.findIssuedGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId()))
								+ ". Do you want to reassign this card to " + cboxStaff.getSelectionModel().getSelectedItem().toString() + "?");

						Optional<ButtonType> result = alert1.showAndWait();
						if (result.get() == ButtonType.OK){
							
							SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 2);
							SQLConnector.assignStaffGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), SQLConnector.findStaffIdByName(cboxStaff.getSelectionModel().getSelectedItem().toString()));
							listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
							
						}
						
					}
					else{
						
						Alert alert1 = new Alert(AlertType.CONFIRMATION);
						alert1.setTitle("Confirmation Dialog");
						alert1.setHeaderText("Guest card");
						alert1.setContentText("Are you sure you want to issue " + cboxCard.getSelectionModel().getSelectedItem().getName() + " to  " + cboxStaff.getSelectionModel().getSelectedItem().toString() + "?");

						Optional<ButtonType> result = alert1.showAndWait();
						if (result.get() == ButtonType.OK){
							
							SQLConnector.assignStaffGuestCard(cboxCard.getSelectionModel().getSelectedItem().getId(), SQLConnector.findStaffIdByName(cboxStaff.getSelectionModel().getSelectedItem().toString()));
							listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
							
						}
						
					}
					
					
				}
			}
		}
		
	}
	
	@FXML
	public void btnReleaseOnAction(ActionEvent event){

		Alert alert1 = new Alert(AlertType.CONFIRMATION);
		alert1.setTitle("Confirmation Dialog");
		alert1.setHeaderText("Guest card");
		alert1.setContentText("Are you sure you want to release " + cboxCard.getSelectionModel().getSelectedItem().getName() + "?");

		Optional<ButtonType> result = alert1.showAndWait();
		if (result.get() == ButtonType.OK){
		
			String selected = rbGroup.getSelectedToggle().getUserData().toString();
			
			if (selected.equals("guest")){
			
				SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 3);
				listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
			}
			else{
				
				if (selected.equals("family")){
					
					SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 1);
					listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
					
				}
				else{
					
					if (selected.equals("staff")){
						
						SQLConnector.releaseCard(cboxCard.getSelectionModel().getSelectedItem().getId(), 2);
						listTempCards.setItems(SQLConnector.currentlyInSchoolReportGuests(conn));
						
					}
					
				}
				
			}
		}
		
	}
	
	

}
