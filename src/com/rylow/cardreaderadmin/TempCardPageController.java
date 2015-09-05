package com.rylow.cardreaderadmin;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;

public class TempCardPageController implements Initializable {
	
	@FXML
	ListView<Card> listProfiles;
	
	@FXML
	Label lblAssCard, lblProfileName;
	
	@FXML
	Button btnAddProfile, btnChangeCard, btnSave, btnCancel;
	
	@FXML
	ChoiceBox<String> cboxProfileType;
	
	ObservableList<String> types =FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		listProfiles.setItems(SQLConnector.fillProfilesList());
		listProfiles.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Card>() {
	                public void changed(ObservableValue<? extends Card> ov, 
	                    Card old_val,Card new_val) {
	                		
	                		showProfile(SQLConnector.findProfileByID(new_val.getId()));
	                	
	            }
	        });
		listProfiles.getSelectionModel().selectFirst();
		
		types.clear();
		types.add("Temporary Family");
		types.add("Temporary Staff");
		types.add("Guest");
		
		cboxProfileType.setItems(types);
		
	}
	
	private void showProfile(Card profile){
		
		lblProfileName.setText(profile.getCardNumber());
		lblAssCard.setText(SQLConnector.findCardsByID(SQLConnector.findGlobalIDofGuestCard(profile.getId())).getCardNumber());
		cboxProfileType.getSelectionModel().select(profile.getHolderID() - 31);
		
	}
	
	@FXML
	public void btnCancelOnAction(ActionEvent event){
		
		listProfiles.getSelectionModel().clearSelection();
		listProfiles.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void btnSaveOnAction(ActionEvent event){
		
		if (lblAssCard.getText().equals("Not Found")){
			
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Card is not assigned");
			alert.setContentText("You cannot save a profile without a card assigned to it");

			alert.showAndWait();
			
		}
		else{
			
			if (lblProfileName.equals("")){ //NEW PROFILE
				
				Card profile = new Card();
				
				profile.setHolderID(cboxProfileType.getSelectionModel().getSelectedIndex() + 31);
				profile.setCardNumber("Temporary Card #");
				profile.setType(0);
				
				SQLConnector.insertNewProfile(profile);
				
				listProfiles.setItems(SQLConnector.fillProfilesList());
				listProfiles.getSelectionModel().selectFirst();
			}
			
			
		}
		
	}
	
	/*@FXML
	public void btnAddProfileOnAction(ActionEvent event){
		
		listProfiles.getSelectionModel().clearSelection();
		
		lblAssCard.equals("AAAAAAAA")
		
	}*/
	
	

}
