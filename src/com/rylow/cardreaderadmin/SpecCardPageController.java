package com.rylow.cardreaderadmin;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.rylow.SpecialCard.SpecialCard;
import com.rylow.Staff.Staff;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;


public class SpecCardPageController implements Initializable {
	
	@FXML
	ListView<SpecialCard> listProfiles;
	
	@FXML
	ListView<String> listCards;
	
	
	@FXML
	Button btnAddProfile, btnRemProfile, btnAddCard, btnRemCardSel, btnSave, btnCancel;
	
	@FXML
	ChoiceBox<SecurityGroup> cboxSecGroup;
	
	@FXML
	TextField txtProfName;
	
	@FXML
	Label lblID;
	
	ObservableList<SpecialCard> types =FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		listProfiles.setItems(SQLConnector.fillSpecCardList());
		cboxSecGroup.setItems(SQLConnector.fillInSecurityBox());
		listProfiles.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<SpecialCard>() {
	                public void changed(ObservableValue<? extends SpecialCard> ov, 
	                		SpecialCard old_val, SpecialCard new_val) {
	                      	showProfile(new_val);  
		              		btnAddCard.setDisable(false);
		            		btnRemCardSel.setDisable(false); 
		            		btnAddProfile.setDisable(false);
		            		btnRemProfile.setDisable(false);
		            		
	            }
	        });
		listProfiles.getSelectionModel().selectFirst();
		
		
		
	}
	
	private void showProfile(SpecialCard selProfile){
		
		if(selProfile != null){
			listCards.setItems(SQLConnector.findCardsBySpecialProfile(selProfile.getId()));
			
			txtProfName.setText(selProfile.getName());
			lblID.setText(String.valueOf(selProfile.getId()));
			cboxSecGroup.setItems(SQLConnector.fillInSecurityBox());
			
			for (int i = 0; i < cboxSecGroup.getItems().size(); i++){
				
				if (cboxSecGroup.getItems().get(i).getId() == selProfile.getSecurityGroup()){
					
					cboxSecGroup.getSelectionModel().select(i);
					break;
					
				}
				
			}
		
		}
	}
	
	@FXML
	public void btnCancelOnAction(ActionEvent event){
		
		listProfiles.getSelectionModel().clearSelection();
		listProfiles.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void btnSaveOnAction(ActionEvent event){
		
	//// NEW Profile /////
				if (lblID.getText().equals("")){
					
					if (txtProfName.getText().equals("")){
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Profile is Empty");
						alert.setContentText("You cannot create a profile without a name");

						alert.showAndWait();
						
					}
					else{
						
						if(SQLConnector.doesSpecialProfileExist(txtProfName.getText())){
							
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Error Dialog");
							alert.setHeaderText("Profile name is not unique");
							alert.setContentText("Profile name you are trying to create is not unique");

							alert.showAndWait();
						}
						else{
							
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Confirmation Dialog");
							alert.setHeaderText("New Profile " + txtProfName.getText());
							alert.setContentText("Are you sure you want to create a new profile " + txtProfName.getText() + "?");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){

								SpecialCard card = new SpecialCard(0, txtProfName.getText(), cboxSecGroup.getSelectionModel().getSelectedItem().getId());
								
								if (SQLConnector.insertNewSpecialProfile(card)){
									
									listProfiles.setItems(SQLConnector.fillSpecCardList());
									listProfiles.getSelectionModel().selectLast();
								}

							}						
						}
					}
					
					
					
				}
				
				//// EXISTING FAMILY UPDATING/////
				else{
					
					if (txtProfName.getText().equals("")){
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Profile Name is Empty");
						alert.setContentText("You cannot update a Profile profile to have an empty name");

						alert.showAndWait();
					}
					else{
						
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
						alert.setHeaderText("Update Profile " + txtProfName.getText());
						alert.setContentText("Are you sure you want to update Profile " + txtProfName.getText() + "?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){

							SpecialCard card = new SpecialCard(Integer.valueOf(lblID.getText()), txtProfName.getText(), cboxSecGroup.getSelectionModel().getSelectedItem().getId());
							
							if (SQLConnector.updateSpecialProfile(card)){
								
								listProfiles.setItems(SQLConnector.fillSpecCardList());
								
							}
							else{
								
								Alert alert1 = new Alert(AlertType.ERROR);
								alert1.setTitle("Error Dialog");
								alert1.setHeaderText("Cannot create new Profile");
								alert1.setContentText("Profile creation failed. Probably SQL server failure. Please try again.");
								alert1.showAndWait();
							}
							
						} else {
						    // ... user chose CANCEL or closed the dialog
						}
						
						
						
					}
				}
		
	}
	

	@FXML
	public void btnAddCardOnAction(ActionEvent event){
		
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("Add New Card");
		dialog.setHeaderText("Scan New Card");
		dialog.setContentText("Please scan your card here:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		    String scan = result.get();
		    
		    if (scan.length() == 10){
		    	
		    	try{
		    		Long.parseLong(scan, 16);
		    		if (!SQLConnector.isCardIsAlreadyAssigned(scan.toUpperCase())){
			    		if (SQLConnector.addCardtoSpecialProfile(scan.toUpperCase(), Integer.valueOf(lblID.getText()))){
			    			
			    			listCards.setItems(SQLConnector.findCardsBySpecialProfile(Integer.valueOf(lblID.getText())));
			    			
			    		}
			    		else{
			    			
			    			Alert alert1 = new Alert(AlertType.ERROR);
							alert1.setTitle("Error Dialog");
							alert1.setHeaderText("Card Creating Error");
							alert1.setContentText("SQL Server error while adding the new card. Please try again later.");
							alert1.showAndWait();
			    		}
		    		}
		    		else{
		    			
		    			Alert alert1 = new Alert(AlertType.ERROR);
						alert1.setTitle("Error Dialog");
						alert1.setHeaderText("Card Creating Error");
						alert1.setContentText("The card you are trying to assign was already assigned to someone else.");
						alert1.showAndWait();
		    			
		    		}
		    	}
		    	catch (NumberFormatException e){
		    		
		    		Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error Dialog");
					alert1.setHeaderText("Card scan error");
					alert1.setContentText("The card you scanned appears to be in hexadecimal format, but it is not. Please contact IT department.");
					alert1.showAndWait();
		    	}
		    	
		    }
		    else{
		    	if (scan.length() == 9){
		    		
		    		try{
		    			
		    			long no = Long.parseLong(scan);
		    			String hex = Long.toHexString(no);
		    			hex = "010" + hex.toUpperCase();	
		    			
		    			if (!SQLConnector.isCardIsAlreadyAssigned(hex)){
		    			
				    		if (SQLConnector.addCardtoSpecialProfile(hex, Integer.valueOf(lblID.getText()))){
				    			
				    			listCards.setItems(SQLConnector.findCardsBySpecialProfile(Integer.valueOf(lblID.getText())));
				    			
				    		}
				    		else{
				    			
				    			Alert alert1 = new Alert(AlertType.ERROR);
								alert1.setTitle("Error Dialog");
								alert1.setHeaderText("Card Creating Error");
								alert1.setContentText("SQL Server error while adding the new card. Please try again later.");
								alert1.showAndWait();
				    		}
		    			}
			    		else{
			    			
			    			Alert alert1 = new Alert(AlertType.ERROR);
							alert1.setTitle("Error Dialog");
							alert1.setHeaderText("Card Creating Error");
							alert1.setContentText("The card you are trying to assign was already assigned to someone else.");
							alert1.showAndWait();
			    			
			    		}
		    			
		    			
		    		}
			    	catch (NumberFormatException e){
			    		
			    		Alert alert1 = new Alert(AlertType.ERROR);
						alert1.setTitle("Error Dialog");
						alert1.setHeaderText("Card scan error");
						alert1.setContentText("The card you scanned appears to be in decimal format, but it is not. Please contact IT department.");
						
						alert1.showAndWait();
			    	}
		    		
		    	}
		    	else{
		    		
		    		Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error Dialog");
					alert1.setHeaderText("Card scan error");
					alert1.setContentText("Cannot recognize the format of your scanned code. Please contact IT department.");
		    		
					alert1.showAndWait();
		    	}
		    	
		    	
		    }
		}
		
	}
	
	@FXML
	public void btnRemCardSelOnAction(ActionEvent event){
		
		String cardNumber = listCards.getSelectionModel().getSelectedItem();
		
		if (cardNumber != null){
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Remove card " + cardNumber);
			alert.setContentText("Are you sure you want to remove card " + cardNumber + "?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				
				if(SQLConnector.removeCard(cardNumber)){
					
					listCards.setItems(SQLConnector.findCardsBySpecialProfile(Integer.valueOf(lblID.getText())));
					
				}
				else{
					
	    			Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error Dialog");
					alert1.setHeaderText("Card Removing Error");
					alert1.setContentText("SQL Server error while removing the card. Please try again later.");
					alert1.showAndWait();
					
				}
				
			}
			
		}
		
	}
	
	@FXML
	public void btnAddNewProfileOnAction(ActionEvent event){
		
		listProfiles.getSelectionModel().clearSelection();
		
		txtProfName.setText("");
		lblID.setText("");
		
		btnAddCard.setDisable(true);
		btnRemCardSel.setDisable(true); 
		btnAddProfile.setDisable(true);
		btnRemProfile.setDisable(true);
		
		cboxSecGroup.getSelectionModel().selectFirst();

		listCards.getItems().clear();
				
	}
	
	@FXML
	public void btnRemProfileOnAction(ActionEvent event){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Are you sure you want to remove this profile?");
		alert.setContentText("Are you sure you want to remove " + listProfiles.getSelectionModel().getSelectedItem() + "? This can't be undone!!!");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			
			if(SQLConnector.deleteProfileRecord(Integer.valueOf(lblID.getText()))){
				
				listProfiles.setItems(SQLConnector.fillSpecCardList());
				listProfiles.getSelectionModel().selectFirst();
				
			}
			else{
				
				Alert alert1 = new Alert(AlertType.ERROR);
				alert1.setTitle("Error Dialog");
				alert1.setHeaderText("Cannot delete the profile");
				alert1.setContentText("Profile removal process failed. Probably SQL server failure. Please try again.");
				alert1.showAndWait();
			}
			
		}
		
	}
	
	

}
