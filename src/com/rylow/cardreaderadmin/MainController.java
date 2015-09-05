package com.rylow.cardreaderadmin;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.rylow.Family.Family;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable{
	
	//REPORTS
	@FXML
	TableView<FamilyReport> tblGuests, tblFamily, tblStudents;
	@FXML
	TableView<StaffReport> tblStaff;
	@FXML
	TableColumn<FamilyReport, String> clmFamilyName;
	@FXML
	TableColumn<FamilyReport, String>clmFamilySince;
	@FXML
	TableColumn<StaffReport, String> clmStaffName;
	@FXML
	TableColumn<StaffReport, String>clmStaffSince;
	@FXML
	TableColumn<FamilyReport, String> clmGuestName;
	@FXML
	TableColumn<FamilyReport, String>clmGuestSince;
	@FXML
	TableColumn<FamilyReport, String> clmStudentName;
	@FXML
	TableColumn<FamilyReport, String>clmStudentSince;
	@FXML
	Button btnGenerateReport;
	@FXML
	CheckBox chkActive;
	@FXML
	ChoiceBox<SecurityGroup> cboxSecGroup;
	
	//FAMILIES
	@FXML
	Button btnImg1, btnImg2, btnImg3, btnImg4, btnAddCard, btnRemCardSel, btnRemCardScan, btnAddNewFamily, btnRemSelFam, btnSave, btnCancel, btnFireDrill;
	@FXML
	TextField txtFamilyName, txtNameHolder1, txtNameHolder2, txtNameHolder3, txtNameHolder4, txtDescrHolder1, txtDescrHolder2, txtDescrHolder3, txtDescrHolder4;
	@FXML
	ListView<String> listFamilies, listCards;
	@FXML
	ImageView imgHolder1, imgHolder2, imgHolder3, imgHolder4, imglogo;
	@FXML
	Label lblID;
	
	@FXML
	Parent root;
	
	private static Connection conn;
	
	
	private static ObservableList<FamilyReport> familyList = FXCollections.observableArrayList();
	private static ObservableList<StaffReport> staffList = FXCollections.observableArrayList();
	private static ObservableList<FamilyReport> guestsList = FXCollections.observableArrayList();
	private static ObservableList<FamilyReport> studentsList = FXCollections.observableArrayList();
	
	private File img1Changed, img2Changed, img3Changed, img4Changed;
	String currentImg1, currentImg2, currentImg3, currentImg4;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
		conn = SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		imglogo.setImage(new Image(Main.class.getResourceAsStream("calogo.jpg")));
		
		clmFamilyName.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("name"));
		clmFamilySince.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("since"));
		clmStaffName.setCellValueFactory(new PropertyValueFactory<StaffReport, String>("name"));
		clmStaffSince.setCellValueFactory(new PropertyValueFactory<StaffReport, String>("since"));
		
		clmGuestName.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("name"));
		clmGuestSince.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("since"));
		
		clmStudentName.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("name"));
		clmStudentSince.setCellValueFactory(new PropertyValueFactory<FamilyReport, String>("since"));
		
		tblFamily.setItems(generateReportFamily());
		tblStaff.setItems(generateReportStaff());
		tblGuests.setItems(generateReportGuests());
		tblStudents.setItems(generateReportStudents());
		
		//FAMILIES LIST
		listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
		listFamilies.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
	                public void changed(ObservableValue<? extends String> ov, 
	                    String old_val, String new_val) {
	                      showFamily(new_val);  
	              		btnAddCard.setDisable(false);
	            		btnRemCardSel.setDisable(false); 
	            		btnRemCardScan.setDisable(false); 
	            		btnAddNewFamily.setDisable(false);
	            		btnRemSelFam.setDisable(false);
	            		img1Changed = null;
	            		img2Changed = null;
	            		img3Changed = null;
	            		img4Changed = null;
	            }
	        });
		listFamilies.getSelectionModel().selectFirst();
	}

	
	@FXML
	public void btnGenerateReportOnAction(ActionEvent event){
		
		generateReport();
		
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
					
					listCards.setItems(SQLConnector.findCardsByFamily(conn, Integer.valueOf(lblID.getText())));
					
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
			    		if (SQLConnector.addCardtoFamily(scan.toUpperCase(), Integer.valueOf(lblID.getText()))){
			    			
			    			listCards.setItems(SQLConnector.findCardsByFamily(conn, Integer.valueOf(lblID.getText())));
			    			
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
		    			
				    		if (SQLConnector.addCardtoFamily(hex, Integer.valueOf(lblID.getText()))){
				    			
				    			listCards.setItems(SQLConnector.findCardsByFamily(conn, Integer.valueOf(lblID.getText())));
				    			
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
	public void btnImg1OnAction(ActionEvent event){
		
		File file = loadImage();
		
		if (file != null){
			imgHolder1.setImage(new Image(file.toURI().toString()));
			img1Changed = file;
			
		}
	}
	
	@FXML
	public void btnImg2OnAction(ActionEvent event){
		
		File file = loadImage();
		
		if (file != null){
			imgHolder2.setImage(new Image(file.toURI().toString()));
			img2Changed = file;
		}
	}
	
	@FXML
	public void btnImg3OnAction(ActionEvent event){
		
		File file = loadImage();
		
		if (file != null){
			imgHolder3.setImage(new Image(file.toURI().toString()));
			img3Changed = file;
		}
	}
	
	@FXML
	public void btnImg4OnAction(ActionEvent event){
		
		File file = loadImage();
		
		if (file != null){
			imgHolder4.setImage(new Image(file.toURI().toString()));
			img4Changed = file;
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
	
	public static void generateReport(){
		
		familyList.removeAll(familyList);
	    Platform.runLater(new Runnable(){
	        @Override
	        public void run() {
	        	generateReportFamily();
	        }
	    });
	    
		staffList.removeAll(staffList);
	    Platform.runLater(new Runnable(){
	        @Override
	        public void run() {
	        	generateReportStaff();
	        }
	    });
	    
		guestsList.removeAll(guestsList);
	    Platform.runLater(new Runnable(){
	        @Override
	        public void run() {
	        	generateReportGuests();
	        }
	    });
	    
		studentsList.removeAll(studentsList);
	    Platform.runLater(new Runnable(){
	        @Override
	        public void run() {
	        	generateReportStudents();
	        }
	    });
		
	}
	
	private static ObservableList<FamilyReport> generateReportFamily(){
		
		long diffSeconds;
		long diffMinutes;
		long diffHours;
		
		
		
		TreeMap<Long, String> familyInSchool = SQLConnector.currentlyInSchoolReportFamily(conn);
		
		if (!familyInSchool.isEmpty()){
			
			
			
			for (long timeDiff : familyInSchool.keySet()){
				
				diffSeconds = timeDiff / 1000 % 60;
				diffMinutes = timeDiff / (60 * 1000) % 60;
				diffHours = timeDiff / (60 * 60 * 1000) % 24 + 1;
				
				String since = "";
				
				if (diffHours > 9)
					since = since + diffHours + ":";
				else
					since = since + "0" + diffHours + ":";
				
				if (diffMinutes > 9)
					since = since + diffMinutes + ":";
				else
					since = since + "0" + diffMinutes + ":";
				
				if (diffSeconds > 9)
					since = since + diffSeconds ;
				else
					since = since + "0" + diffSeconds;
				
				familyList.add(new FamilyReport(familyInSchool.get(timeDiff),since));
				
				
			}

		}
		else{
			familyList.add(new FamilyReport("Nobody should be in the school at the moment",""));
		}
		
		return familyList;
	}
	
	private static ObservableList<StaffReport> generateReportStaff(){
		
		long diffSeconds;
		long diffMinutes;
		long diffHours;
		
		
		
		TreeMap<Long, String> staffInSchool = SQLConnector.currentlyInSchoolReportStaff(conn);
		
		if (!staffInSchool.isEmpty()){
			
			
			
			for (long timeDiff : staffInSchool.keySet()){
				
				diffSeconds = timeDiff / 1000 % 60;
				diffMinutes = timeDiff / (60 * 1000) % 60;
				diffHours = timeDiff / (60 * 60 * 1000) % 24 + 1;
				
				String since = "";
				
				if (diffHours > 9)
					since = since + diffHours + ":";
				else
					since = since + "0" + diffHours + ":";
				
				if (diffMinutes > 9)
					since = since + diffMinutes + ":";
				else
					since = since + "0" + diffMinutes + ":";
				
				if (diffSeconds > 9)
					since = since + diffSeconds ;
				else
					since = since + "0" + diffSeconds;
				
				staffList.add(new StaffReport(staffInSchool.get(timeDiff),since));
			}

		}
		else{
			staffList.add(new StaffReport("Nobody should be in the school at the moment",""));
		}
		
		return staffList;
	}
	
	private static ObservableList<FamilyReport> generateReportGuests(){
		
		long diffSeconds;
		long diffMinutes;
		long diffHours;
		
		
		
		TreeMap<Long, String> guestsInSchool = SQLConnector.currentlyInSchoolReportOnlyGuests();
		
		if (!guestsInSchool.isEmpty()){
			
			
			
			for (long timeDiff : guestsInSchool.keySet()){
				
				diffSeconds = timeDiff / 1000 % 60;
				diffMinutes = timeDiff / (60 * 1000) % 60;
				diffHours = timeDiff / (60 * 60 * 1000) % 24 + 1;
				
				String since = "";
				
				if (diffHours > 9)
					since = since + diffHours + ":";
				else
					since = since + "0" + diffHours + ":";
				
				if (diffMinutes > 9)
					since = since + diffMinutes + ":";
				else
					since = since + "0" + diffMinutes + ":";
				
				if (diffSeconds > 9)
					since = since + diffSeconds ;
				else
					since = since + "0" + diffSeconds;
				
				guestsList.add(new FamilyReport(guestsInSchool.get(timeDiff),since));
				
				
			}

		}
		else{
			guestsList.add(new FamilyReport("Nobody should be in the school at the moment",""));
		}
		
		return guestsList;
	}
	
	private static ObservableList<FamilyReport> generateReportStudents(){
		
		long diffSeconds;
		long diffMinutes;
		long diffHours;
		
		
		
		TreeMap<Long, String> guestsInSchool = SQLConnector.currentlyInSchoolReportStudents(conn);
		
		if (!guestsInSchool.isEmpty()){
			
			
			
			for (long timeDiff : guestsInSchool.keySet()){
				
				diffSeconds = timeDiff / 1000 % 60;
				diffMinutes = timeDiff / (60 * 1000) % 60;
				diffHours = timeDiff / (60 * 60 * 1000) % 24 + 1;
				
				String since = "";
				
				if (diffHours > 9)
					since = since + diffHours + ":";
				else
					since = since + "0" + diffHours + ":";
				
				if (diffMinutes > 9)
					since = since + diffMinutes + ":";
				else
					since = since + "0" + diffMinutes + ":";
				
				if (diffSeconds > 9)
					since = since + diffSeconds ;
				else
					since = since + "0" + diffSeconds;
				
				studentsList.add(new FamilyReport(guestsInSchool.get(timeDiff),since));
				
				
			}

		}
		else{
			studentsList.add(new FamilyReport("Nobody should be in the school at the moment",""));
		}
		
		return studentsList;
	}
	
	private void showFamily (String familyName){
		
		Family family = SQLConnector.findFamilyByName(conn, familyName);
		
		if (family != null){
			
			txtFamilyName.setText(family.getFamilyName());
			txtNameHolder1.setText(family.getHolder1Name());
			txtNameHolder2.setText(family.getHolder2Name());
			txtNameHolder3.setText(family.getHolder3Name());
			txtNameHolder4.setText(family.getHolder4Name());
			txtDescrHolder1.setText(family.getHolder1Status());
			txtDescrHolder2.setText(family.getHolder2Status());
			txtDescrHolder3.setText(family.getHolder3Status());
			txtDescrHolder4.setText(family.getHolder4Status());
			
			listCards.setItems(SQLConnector.findCardsByFamily(conn, family.getFamilyID()));
			
			lblID.setText(String.valueOf(family.getFamilyID()));
			
			cboxSecGroup.setItems(SQLConnector.fillInSecurityBox());
			
			chkActive.setSelected(family.getActive());
			
			for (int i = 0; i < cboxSecGroup.getItems().size(); i++){
				
				if (cboxSecGroup.getItems().get(i).getId() == family.getSecurityGroup()){
					
					cboxSecGroup.getSelectionModel().select(i);
					break;
					
				}
				
			}
			
			currentImg1 = family.getHolder1Picture();
			
			try{
				if (!currentImg1.equals("-")){
					imgHolder1.setImage(new Image("file:\\\\172.25.0.215\\Photos\\" + family.getHolder1Picture()));
				}
				else{
					imgHolder1.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
				}
			}
			catch(Exception e){
				
			}
			
			currentImg2 = family.getHolder2Picture();
			try{
				if (!currentImg2.equals("-")){
					imgHolder2.setImage(new Image("file:\\\\172.25.0.215\\Photos\\" + family.getHolder2Picture()));
				}
				else{
					imgHolder2.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
				}
			}
			catch(Exception e){
				
			}
			
			currentImg3 = family.getHolder3Picture();
			try{
				if (!currentImg3.equals("-")){
					imgHolder3.setImage(new Image("file:\\\\172.25.0.215\\Photos\\" + family.getHolder3Picture()));
				}
				else{
					imgHolder3.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
				}
			}
			catch(Exception e){
				
			}
			
			currentImg4 = family.getHolder4Picture();
			try{
				if (!currentImg4.equals("-")){
					imgHolder4.setImage(new Image("file:\\\\172.25.0.215\\Photos\\" + family.getHolder4Picture()));
				}
				else{
					imgHolder4.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
				}
			}
			catch(Exception e){
				
			}
	
		}
		
	}
	
	
	@FXML
	public void btnAddNewFamilyOnAction(ActionEvent event){
		
		listFamilies.getSelectionModel().clearSelection();
		
		txtFamilyName.setText("");
		txtNameHolder1.setText("");
		txtNameHolder2.setText("");
		txtNameHolder3.setText("");
		txtNameHolder4.setText("");
		txtDescrHolder1.setText("");
		txtDescrHolder2.setText("");
		txtDescrHolder3.setText("");
		txtDescrHolder4.setText("");
		lblID.setText("");
		
		btnAddCard.setDisable(true);
		btnRemCardSel.setDisable(true); 
		btnRemCardScan.setDisable(true); 
		btnAddNewFamily.setDisable(true);
		btnRemSelFam.setDisable(true);
		
		img1Changed = null;
		img2Changed = null;
		img3Changed = null;
		img4Changed = null;
		
		currentImg1 = "no_photo.jpg";
		currentImg2 = "no_photo.jpg";
		currentImg3 = "no_photo.jpg";
		currentImg4 = "no_photo.jpg";
		
		listCards.getItems().clear();
		
		try{
			
			imgHolder1.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
		}
		catch(Exception e){
			
		}
		try{
			imgHolder2.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
		}
		catch(Exception e){
			
		}
		try{
			imgHolder3.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
		}
		catch(Exception e){
			
		}
		try{
			imgHolder4.setImage(new Image("file:\\\\172.25.0.215\\Photos\\no_photo.jpg"));
		}
		catch(Exception e){
			
		}
		
		
	}
	
	@FXML
	public void btnRemSelFamOnAction(ActionEvent event){
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Are you sure you want to remove this family?");
		alert.setContentText("Are you sure you want to remove Family " + listFamilies.getSelectionModel().getSelectedItem() + "? This can't be undone!!!");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			
			if(SQLConnector.deleteFamilyRecord(Integer.valueOf(lblID.getText()))){
				
				listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
				listFamilies.getSelectionModel().selectFirst();
				
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
	public void btnCancelOnAction(ActionEvent event){
		
		listFamilies.getSelectionModel().clearSelection();
		listFamilies.getSelectionModel().selectFirst();
		
	}
	
	@FXML
	public void btnSaveOnAction(ActionEvent event){
		
		
		//// NEW FAMILY /////
		if (lblID.getText().equals("")){
			
			if (txtFamilyName.getText().equals("")){
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Family Name is Empty");
				alert.setContentText("You cannot create a Family without a Family name");

				alert.showAndWait();
				
			}
			else{
				
				if(SQLConnector.doesFamilyExist(txtFamilyName.getText())){
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error Dialog");
					alert.setHeaderText("Family name is not unique");
					alert.setContentText("The family name you are trying to create is not unique");

					alert.showAndWait();
				}
				else{
					
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Confirmation Dialog");
					alert.setHeaderText("New Family " + txtFamilyName.getText());
					alert.setContentText("Are you sure you want to create a new Family " + txtFamilyName.getText() + "?");

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){

						Family family = createFamilyPacket(0);
						
						if (SQLConnector.insertNewFamily(family)){
							
							int famID = SQLConnector.findFamilyIdByName(txtFamilyName.getText());
							
							if (famID != 0){
							
								Family familyUpd = createFamilyPacket(famID);
								
								if (SQLConnector.updateFamilyProfile(familyUpd)) {
								
									listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
									listFamilies.getSelectionModel().select(family.getFamilyName());
								}
								else{
									
									listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
									listFamilies.getSelectionModel().select(family.getFamilyName());
									
									Alert alert1 = new Alert(AlertType.ERROR);
									alert1.setTitle("Error Dialog");
									alert1.setHeaderText("Cannot create pictures for the new family");
									alert1.setContentText("New family was created, but the pictures cannot be assigned to the card holders. Please try update pictures later.");
									alert1.showAndWait();
								}
							}
							else{
								
								listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
								listFamilies.getSelectionModel().select(family.getFamilyName());
								
								Alert alert1 = new Alert(AlertType.ERROR);
								alert1.setTitle("Error Dialog");
								alert1.setHeaderText("Cannot create pictures for the new family");
								alert1.setContentText("New family was created, but the pictures cannot be assigned to the card holders. Please try update pictures later.");
								alert1.showAndWait();
								
							}
							
						}
						else{
							
							Alert alert1 = new Alert(AlertType.ERROR);
							alert1.setTitle("Error Dialog");
							alert1.setHeaderText("Cannot create a new family");
							alert1.setContentText("Family creation failed. Probably SQL server failure. Please try again.");
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
			
			if (txtFamilyName.getText().equals("")){
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Family Name is Empty");
				alert.setContentText("You cannot update a Family profile to have an empty family name");

				alert.showAndWait();
			}
			else{
				
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setHeaderText("Update Family " + txtFamilyName.getText());
				alert.setContentText("Are you sure you want to update Family " + txtFamilyName.getText() + "?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK){

					Family familyUpd = createFamilyPacket(Integer.valueOf(lblID.getText()));
					
					if (SQLConnector.updateFamilyProfile(familyUpd)){
						
						listFamilies.setItems(SQLConnector.fillFamiliesList(conn));
						listFamilies.getSelectionModel().select(familyUpd.getFamilyName());
						
					}
					else{
						
						Alert alert1 = new Alert(AlertType.ERROR);
						alert1.setTitle("Error Dialog");
						alert1.setHeaderText("Cannot create a new family");
						alert1.setContentText("Family creation failed. Probably SQL server failure. Please try again.");
						alert1.showAndWait();
					}
					
				} else {
				    // ... user chose CANCEL or closed the dialog
				}
				
				
				
			}
		}
	}
	
	private Family createFamilyPacket(int id){
		
		Family family = new Family();
		
		if (id != 0){				///IF THIS IS AN OLD FAMILY (ID ALREADY EXISTS)
			family.setFamilyID(id);
			
			if (img1Changed == null){					/// NO PICTURE CHANGE
				family.setHolder1Picture(currentImg1);
			}
			else{										/// NEW PICTURE
				try {
					Files.copy(Paths.get(img1Changed.toString()), Paths.get("\\\\172.25.0.215\\Photos\\" + id + "-1.jpg"), StandardCopyOption.REPLACE_EXISTING);
					family.setHolder1Picture(id + "-1.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (img2Changed == null){
				family.setHolder2Picture(currentImg2);
			}
			else{
				try {
					Files.copy(Paths.get(img2Changed.toString()), Paths.get("\\\\172.25.0.215\\Photos\\" + id + "-2.jpg"), StandardCopyOption.REPLACE_EXISTING);
					family.setHolder2Picture(id + "-2.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
			if (img3Changed == null){
				family.setHolder3Picture(currentImg3);
			}
			else{
				try {
					Files.copy(Paths.get(img3Changed.toString()), Paths.get("\\\\172.25.0.215\\Photos\\" + id + "-3.jpg"), StandardCopyOption.REPLACE_EXISTING);
					family.setHolder3Picture(id + "-3.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (img4Changed == null){
				family.setHolder4Picture(currentImg4);
			}
			else{
				try {
					Files.copy(Paths.get(img4Changed.toString()), Paths.get("\\\\172.25.0.215\\Photos\\" + id + "-4.jpg"), StandardCopyOption.REPLACE_EXISTING);
					family.setHolder4Picture(id + "-4.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}

			
		}
		else{				/// NEW FAMILY
	
			
			family.setHolder1Picture("no_photo.jpg");
			family.setHolder2Picture("no_photo.jpg");
			family.setHolder3Picture("no_photo.jpg");
			family.setHolder4Picture("no_photo.jpg");

		}
		
		family.setFamilyName(txtFamilyName.getText());
		
		if (txtNameHolder1.getText().equals(""))
			txtNameHolder1.setText("-");
		if (txtNameHolder2.getText().equals(""))
			txtNameHolder2.setText("-");
		if (txtNameHolder3.getText().equals(""))
			txtNameHolder3.setText("-");
		if (txtNameHolder4.getText().equals(""))
			txtNameHolder4.setText("-");
		
		if (txtDescrHolder1.getText().equals(""))
			txtDescrHolder1.setText("-");
		if (txtDescrHolder2.getText().equals(""))
			txtDescrHolder2.setText("-");
		if (txtDescrHolder3.getText().equals(""))
			txtDescrHolder3.setText("-");
		if (txtDescrHolder4.getText().equals(""))
			txtDescrHolder4.setText("-");
		
		family.setHolder1Name(txtNameHolder1.getText());
		family.setHolder2Name(txtNameHolder2.getText());
		family.setHolder3Name(txtNameHolder3.getText());
		family.setHolder4Name(txtNameHolder4.getText());
		
		family.setHolder1Status(txtDescrHolder1.getText());
		family.setHolder2Status(txtDescrHolder2.getText());
		family.setHolder3Status(txtDescrHolder3.getText());
		family.setHolder4Status(txtDescrHolder4.getText());
		
		family.setActive(chkActive.isSelected());
		
		family.setSecurityGroup(cboxSecGroup.getSelectionModel().getSelectedItem().getId());
		

		
		return family;
		
		
	}
	
	
	
	@FXML
	public void btnFireDrillOnAction(ActionEvent event){
	
		
		PDDocument document = new PDDocument();
		PDPage page;
		

		PDFont font = PDType1Font.TIMES_BOLD;

		// Start a new content stream which will "hold" the to be created content
		try {
			
			staffList.removeAll(staffList);
			ObservableList<StaffReport> staffinSchool = generateReportStaff();
			
			SimpleDateFormat sdf = new SimpleDateFormat("E dd-MM-YYYY HH:mm");
			Date curDate = new Date();
			
			int count = staffinSchool.size() / 60;
			
			for (int i = 0; i <= count; i++){
				
				page = new PDPage();
				document.addPage( page );
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				
				contentStream.beginText();
				
				if (i == 0){
					
					contentStream.setFont( font, 14 );

					contentStream.moveTextPositionByAmount( 100, 750 );
					contentStream.drawString(sdf.format(curDate));
					
					
					contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 14 + " TL\n");
					contentStream.appendRawCommands("T*\n");
					
					contentStream.drawString("Currently in School: Staff");
					contentStream.appendRawCommands("T*\n");
					contentStream.appendRawCommands("T*\n");
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 100, 750 );
				}
				
				contentStream.setFont( font, 10 );
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < staffinSchool.size())); j++){
					
					contentStream.drawString(staffinSchool.get(j).getName());
					contentStream.appendRawCommands("T*\n");
					
					
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();
				
				contentStream = new PDPageContentStream(document, page, true, true);
				
				contentStream.beginText();;
				contentStream.setFont( font, 10 );
				
				if (i == 0){
					
					contentStream.moveTextPositionByAmount( 300, 702 );
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 300, 750 );
					
				}
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < staffinSchool.size())); j++){
					
					contentStream.drawString(staffinSchool.get(j).getSince());
					contentStream.appendRawCommands("T*\n");
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();

			}
			
			////////////////// FAMILY PART //////////////////////////////////////////
			
			
			familyList.removeAll(familyList);
			ObservableList<FamilyReport> familyinSchool = generateReportFamily();
			
			count = familyinSchool.size() / 60;
			
			for (int i = 0; i <= count; i++){
				
				page = new PDPage();
				document.addPage( page );
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				
				contentStream.beginText();
				
				if (i == 0){
					
					contentStream.setFont( font, 14 );

					contentStream.moveTextPositionByAmount( 100, 750 );
					contentStream.drawString(sdf.format(curDate));
					
					
					contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 14 + " TL\n");
					contentStream.appendRawCommands("T*\n");
					
					contentStream.drawString("Currently in School: Family");
					contentStream.appendRawCommands("T*\n");
					contentStream.appendRawCommands("T*\n");
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 100, 750 );
				}
				
				contentStream.setFont( font, 10 );
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < familyinSchool.size())); j++){
					
					contentStream.drawString(familyinSchool.get(j).getName());
					contentStream.appendRawCommands("T*\n");
					
					
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();
				
				contentStream = new PDPageContentStream(document, page, true, true);
				
				contentStream.beginText();;
				contentStream.setFont( font, 10 );
				
				if (i == 0){
					
					contentStream.moveTextPositionByAmount( 300, 702 );
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 300, 750 );
					
				}
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < familyinSchool.size())); j++){
					
					contentStream.drawString(familyinSchool.get(j).getSince());
					contentStream.appendRawCommands("T*\n");
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();

			}
			
			////////////////////////////////// GUEST ///////////////////////////////////////////////////
			
			guestsList.removeAll(guestsList);
			ObservableList<FamilyReport> guestsinSchool = generateReportGuests();
			
			count = guestsinSchool.size() / 60;
			
			for (int i = 0; i <= count; i++){
				
				page = new PDPage();
				document.addPage( page );
				PDPageContentStream contentStream = new PDPageContentStream(document, page);
				
				contentStream.beginText();
				
				if (i == 0){
					
					contentStream.setFont( font, 14 );

					contentStream.moveTextPositionByAmount( 100, 750 );
					contentStream.drawString(sdf.format(curDate));
					
					
					contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 14 + " TL\n");
					contentStream.appendRawCommands("T*\n");
					
					contentStream.drawString("Currently in School: Guests");
					contentStream.appendRawCommands("T*\n");
					contentStream.appendRawCommands("T*\n");
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 100, 750 );
				}
				
				contentStream.setFont( font, 10 );
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < guestsinSchool.size())); j++){
					
					contentStream.drawString(guestsinSchool.get(j).getName());
					contentStream.appendRawCommands("T*\n");
					
					
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();
				
				contentStream = new PDPageContentStream(document, page, true, true);
				
				contentStream.beginText();;
				contentStream.setFont( font, 10 );
				
				if (i == 0){
					
					contentStream.moveTextPositionByAmount( 300, 702 );
					
				}
				else{
					
					contentStream.moveTextPositionByAmount( 300, 750 );
					
				}
				contentStream.appendRawCommands(font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * 10 + " TL\n");
				
				for (int j = 60*i; ((j < 60*i + 60) && (j < guestsinSchool.size())); j++){
					
					contentStream.drawString(guestsinSchool.get(j).getSince());
					contentStream.appendRawCommands("T*\n");
				}
				
				contentStream.endText();

				// Make sure that the content stream is closed:
				contentStream.close();

			}
			
			String userHomeFolder = System.getProperty("user.home", "Desktop");
			userHomeFolder += "\\Desktop";
			SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY-MM-dd");
			String fileName = sdf2.format(curDate) + ".pdf";
			File exportFile = new File(userHomeFolder, fileName);
			
			document.save(exportFile);
			document.print();
			document.close();
			
			java.awt.Desktop.getDesktop().open(new File(userHomeFolder + "\\" + fileName));
			
		} catch (IOException | COSVisitorException | PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
