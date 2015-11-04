package com.rylow.cardreaderadmin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;

import com.rylow.Staff.Staff;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StaffPageController implements Initializable {
	
	@FXML
	Button btnAddNewStaff, btnRemSelStaff, btnImg1, btnAddCard, btnRemCardSel, btnSave, btnCancel;
	
	@FXML
	ListView<String> listStaff, listCards;
	
	@FXML
	TextField txtStaffName, txtPosition, txtEmail;
	
	@FXML
	CheckBox chkExternal, chkActive;
	
	@FXML
	Label lblID;
	
	@FXML
	ImageView imgPhoto;
	
	@FXML
	Parent root;
	
	@FXML
	ChoiceBox<SecurityGroup> cboxSecGroup;
	
	private File img1Changed;
	private String currentImg1;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		listStaff.setItems(SQLConnector.fillStaffList(false));
		listStaff.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
	                public void changed(ObservableValue<? extends String> ov, 
	                    String old_val, String new_val) {
	                      	showStaff(new_val);  
		              		btnAddCard.setDisable(false);
		            		btnRemCardSel.setDisable(false); 
		            		btnAddNewStaff.setDisable(false);
		            		btnRemSelStaff.setDisable(false);
		            		img1Changed = null;
	            }
	        });
		listStaff.getSelectionModel().selectFirst();
		
	}
	
	private void showStaff(String staffName){
		
		Staff staff = SQLConnector.findStaffByName(staffName);
		
		if (staff != null){
			
			listCards.setItems(SQLConnector.findCardsByStaff(staff.getId()));
			
			lblID.setText(String.valueOf(staff.getId()));
			
			currentImg1 = staff.getPhoto();
			
			txtEmail.setText(staff.getEmail());
			txtPosition.setText(staff.getRole());
			txtStaffName.setText(staff.getName());
			
			chkExternal.setSelected(staff.getExternal());
			chkActive.setSelected(staff.getActive());
			
			cboxSecGroup.setItems(SQLConnector.fillInSecurityBox());
			
			for (int i = 0; i < cboxSecGroup.getItems().size(); i++){
				
				if (cboxSecGroup.getItems().get(i).getId() == staff.getSecurityGroup()){
					
					cboxSecGroup.getSelectionModel().select(i);
					break;
					
				}
				
			}
			
			try{
				if (!currentImg1.equals("-")){
					imgPhoto.setImage(new Image("file:\\\\172.25.0.215\\PhotosStaff\\" + staff.getPhoto()));
				}
				else{
					imgPhoto.setImage(new Image("file:\\\\172.25.0.215\\PhotosStaff\\no_photo.jpg"));
				}
			}
			catch(Exception e){
				
			}
	
		}
		
	}
	
	@FXML
	public void btnCancelOnAction(ActionEvent event){
		
		listStaff.getSelectionModel().clearSelection();
		listStaff.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void btnSaveOnAction(ActionEvent event){
		
	//// NEW STAFF /////
			if (lblID.getText().equals("")){
				
				if (txtStaffName.getText().equals("")){
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Staff Name is Empty");
					alert.setContentText("You cannot create a Staff member without a name");

					alert.showAndWait();
					
				}
				else{
					
					if(SQLConnector.doesStaffExist(txtStaffName.getText())){
						
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error Dialog");
						alert.setHeaderText("Staff name is not unique");
						alert.setContentText("Staff name you are trying to create is not unique");

						alert.showAndWait();
					}
					else{
						
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Confirmation Dialog");
						alert.setHeaderText("New Staff member " + txtStaffName.getText());
						alert.setContentText("Are you sure you want to create a new Staff member " + txtStaffName.getText() + "?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){

							Staff staff = createStaffPacket(0);
							
							if (SQLConnector.insertNewStaff(staff)){
								
								int staffID = SQLConnector.findStaffIdByName(txtStaffName.getText());
								
								if (staffID != 0){
								
									Staff staffUpd = createStaffPacket(staffID);
									
									if (SQLConnector.updateStaffProfile(staffUpd)) {
										// TODO Auto-generated catch block
										listStaff.setItems(SQLConnector.fillStaffList(false));
										listStaff.getSelectionModel().select(staff.getName());
									}
									else{
										
										listStaff.setItems(SQLConnector.fillStaffList(false));
										listStaff.getSelectionModel().select(staff.getName());
										
										Alert alert1 = new Alert(AlertType.ERROR);
										alert1.setTitle("Error Dialog");
										alert1.setHeaderText("Cannot create pictures for the new Staff member");
										alert1.setContentText("New staff member was created, but the picture cannot be assigned to the profle. Please try update pictures later.");
										alert1.showAndWait();
									}
								}
								else{
									
									listStaff.setItems(SQLConnector.fillStaffList(false));
									listStaff.getSelectionModel().select(staff.getName());
									
									Alert alert1 = new Alert(AlertType.ERROR);
									alert1.setTitle("Error Dialog");
									alert1.setHeaderText("Cannot create pictures for the new Staff member");
									alert1.setContentText("New staff member was created, but the picture cannot be assigned to the profle. Please try update pictures later.");
									alert1.showAndWait();
									
								}
								
							}
							else{
								
								Alert alert1 = new Alert(AlertType.ERROR);
								alert1.setTitle("Error Dialog");
								alert1.setHeaderText("Cannot create a new staff member");
								alert1.setContentText("Staff member creation failed. Probably SQL server failure. Please try again.");
								alert1.showAndWait();
								
							}
							
						} else {
						    // ... user chose CANCEL or closed the dialog
						}
						
						
						
						
					}
				}
				
				
				
			}
			
			//// EXISTING FAMILY UPDATING/////
			else{
				
				if (txtStaffName.getText().equals("")){
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Staff Name is Empty");
					alert.setContentText("You cannot update a Staff profile to have an empty name");

					alert.showAndWait();
				}
				else{
					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText("Update Staff member " + txtStaffName.getText());
					alert.setContentText("Are you sure you want to update Staff member " + txtStaffName.getText() + "?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){

						Staff staffUpd = createStaffPacket(Integer.valueOf(lblID.getText()));
						
						if (SQLConnector.updateStaffProfile(staffUpd)){
							
							listStaff.setItems(SQLConnector.fillStaffList(false));
							listStaff.getSelectionModel().select(staffUpd.getName());
							
						}
						else{
							
							Alert alert1 = new Alert(AlertType.ERROR);
							alert1.setTitle("Error Dialog");
							alert1.setHeaderText("Cannot create new Staff member");
							alert1.setContentText("Staff creation failed. Probably SQL server failure. Please try again.");
							alert1.showAndWait();
						}
						
					} else {
					    // ... user chose CANCEL or closed the dialog
					}
					
					
					
				}
			}
		
		
	}
	
	private Staff createStaffPacket(int id){
		
		Staff staff = new Staff();
		
		if (id != 0){				///IF THIS IS AN OLD FAMILY (ID ALREADY EXISTS)
			staff.setId(id);
			
			if (img1Changed == null){					/// NO PICTURE CHANGE
				staff.setPhoto(currentImg1);
			}
			else{										/// NEW PICTURE
				try {
					Files.copy(Paths.get(img1Changed.toString()), Paths.get("\\\\172.25.0.215\\PhotosStaff\\" + id + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
					staff.setPhoto(id + ".jpg");
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			
		}
		else{				/// NEW FAMILY
	
			
			staff.setPhoto("no_photo.jpg");

		}
		
		staff.setName(txtStaffName.getText());
		
		if (txtEmail.getText().equals(""))
			txtEmail.setText("-");
		if (txtPosition.getText().equals(""))
			txtPosition.setText("-");
		
		
		staff.setEmail(txtEmail.getText());
		staff.setRole(txtPosition.getText());
		
		staff.setExternal(chkExternal.isSelected());
		staff.setActive(chkActive.isSelected());
		
		staff.setSecurityGroup(cboxSecGroup.getSelectionModel().getSelectedItem().getId());
				
		return staff;
		
		
	}
	
	@FXML
	public void btnRemSelStaffOnAction(ActionEvent event){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Are you sure you want to remove this family?");
		alert.setContentText("Are you sure you want to remove " + listStaff.getSelectionModel().getSelectedItem() + "? This can't be undone!!!");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			
			if(SQLConnector.deleteStaffRecord(Integer.valueOf(lblID.getText()))){
				
				listStaff.setItems(SQLConnector.fillStaffList(false));
				listStaff.getSelectionModel().selectFirst();
				
			}
			else{
				
				Alert alert1 = new Alert(AlertType.ERROR);
				alert1.setTitle("Error Dialog");
				alert1.setHeaderText("Cannot delete the family");
				alert1.setContentText("Family removal process failed. Probably SQL server failure. Please try again.");
				alert1.showAndWait();
			}
			
		}
		
	}
	
	@FXML
	public void btnAddNewStaffOnAction(ActionEvent event){
		
		//listStaff.getSelectionModel().clearSelection();
		
		txtStaffName.setText("");
		txtEmail.setText("");
		txtPosition.setText("");
		lblID.setText("");
		
		btnAddCard.setDisable(true);
		btnRemCardSel.setDisable(true); 
		btnAddNewStaff.setDisable(true);
		btnRemSelStaff.setDisable(true);
		
		cboxSecGroup.getSelectionModel().selectFirst();
		chkActive.setSelected(true);
		chkExternal.setSelected(false);
		
		img1Changed = null;
		
		currentImg1 = "no_photo.jpg";

		listCards.getItems().clear();
		
		try{
			
			imgPhoto.setImage(new Image("file:\\\\172.25.0.215\\PhotosStaff\\no_photo.jpg"));
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	@FXML
	public void btnImgPhotoOnAction(ActionEvent event){
		
		File file = loadImage();
		
		if (file != null){
			imgPhoto.setImage(new Image(file.toURI().toString()));
			img1Changed = file;
		}
		
	}
	
	private File loadImage(){
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg")
            );
		File file = fileChooser.showOpenDialog((Stage) root.getScene().getWindow());
		
		return file;
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
					
					listCards.setItems(SQLConnector.findCardsByStaff(Integer.valueOf(lblID.getText())));
					
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
			    		if (SQLConnector.addCardtoStaff(scan.toUpperCase(), Integer.valueOf(lblID.getText()))){
			    			
			    			listCards.setItems(SQLConnector.findCardsByStaff(Integer.valueOf(lblID.getText())));
			    			
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
		    			
				    		if (SQLConnector.addCardtoStaff(hex, Integer.valueOf(lblID.getText()))){
				    			
				    			listCards.setItems(SQLConnector.findCardsByStaff(Integer.valueOf(lblID.getText())));
				    			
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
}
