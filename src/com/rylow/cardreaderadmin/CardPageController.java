package com.rylow.cardreaderadmin;

import java.net.URL;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

public class CardPageController implements Initializable {
	
	
	@FXML
	ListView<Card> listCards;
	
	@FXML
	Label lblHolder, lblType, lblID, lblNumber;
	
	@FXML
	Button btnRemCard, btnFindCard;
	

	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		listCards.setItems(SQLConnector.fillCardsList());
		listCards.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<Card>() {
	                public void changed(ObservableValue<? extends Card> ov, 
	                    Card old_val,Card new_val) {
	                		
	                		showCard(SQLConnector.findCardsByID(new_val.getId()));
	                	
	            }
	        });
		listCards.getSelectionModel().selectFirst();
		
	}
	
	private void showCard(Card card){
		
		lblNumber.setText(card.getCardNumber());
		lblID.setText(String.valueOf(card.getId()));
		
		if (card.getType() == 1){ //FAMILY CARD
			
			lblType.setText("Family");
			lblHolder.setText(SQLConnector.findFamilyNameById(card.getHolderID()));
			
		}
		else{
			
			if(card.getType() == 2){ //STAFF CARD
				
				lblType.setText("Staff");
				lblHolder.setText(SQLConnector.findStaffById(card.getHolderID()));
				
			}
			else{
				
				if(card.getType() == 3){ //STAFF CARD
					
					lblType.setText("Temporary Card");
					lblHolder.setText("Temporary Card");
					
				}
				
			}
			
			
		}
		
		
	}
	
	@FXML
	public void btnFindCardOnAction(ActionEvent event){
		
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

		    		for (Card card : listCards.getItems()){
		    			
		    			if (card.getCardNumber().equals(scan.toUpperCase())){
		    				
		    				listCards.getSelectionModel().select(card);
		    				break;
		    				
		    			}
		    			
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
		    			
			    		for (Card card : listCards.getItems()){
			    			
			    			if (card.getCardNumber().equals(hex)){
			    				
			    				listCards.getSelectionModel().select(card);
			    				break;
			    				
			    			}
			    			
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
	public void btnRemCardOnAction(ActionEvent event){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Remove card " + lblNumber.getText());
		alert.setContentText("Are you sure you want to remove card " + lblNumber.getText() + "?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			
			SQLConnector.removeCard(lblNumber.getText());
			
			//listCards.getSelectionModel().clearSelection();
			listCards.setItems(SQLConnector.fillCardsList());
			listCards.getSelectionModel().selectFirst();
			
		}
		
		
	}

}
