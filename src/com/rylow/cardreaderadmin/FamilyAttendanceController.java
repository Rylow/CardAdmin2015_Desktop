package com.rylow.cardreaderadmin;

import java.net.URL;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class FamilyAttendanceController implements Initializable {
	
	@FXML
	ListView<String> listFamily;
	
	@FXML
	TreeView<String> treeAttendance;
	
	Connection conn;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		conn = SQLConnector.dbConnect("jdbc:sqlserver://172.25.0.215\\SQLEXPRESS;databaseName=CardReader","CrdReader","SbdLswTOr*682");
		
		listFamily.setItems(SQLConnector.fillFamiliesList(conn));
		listFamily.getSelectionModel().selectedItemProperty().addListener(
	            new ChangeListener<String>() {
	                public void changed(ObservableValue<? extends String> ov, 
	                    String old_val, String new_val) {

	                		showAttendance(new_val);
	            }
	        });
		
	}
	
	private void showAttendance (String familyName){

		ObservableList<StaffAttendanceRecord> items =SQLConnector.getFamilyAttendanceReport(familyName);
		Boolean foundYear = false;
		Boolean foundMonth = false;
		Boolean foundDay = false;
		
		TreeItem<String> rootNode = 
		        new TreeItem<String>(familyName + " Visits Data");
		
		SimpleDateFormat yearSDF = new SimpleDateFormat("YYYY");
		SimpleDateFormat monthSDF = new SimpleDateFormat("MMMM");
		SimpleDateFormat daySDF = new SimpleDateFormat("d - EEEE");
		SimpleDateFormat recordSDF = new SimpleDateFormat("HH:mm:ss");
		
		for (StaffAttendanceRecord record : items){
			
			
			
            for (TreeItem<String> yearNode : rootNode.getChildren()) {
            	
                if (yearNode.getValue().contentEquals(yearSDF.format(record.getTime()))){ ///YEAR NODE EXIST
                	
                    
                	for (TreeItem<String> monthNode : yearNode.getChildren()){
                		
                		if (monthNode.getValue().contentEquals(monthSDF.format(record.getTime()))){ //// YEAR NODE EXIST, MONTH NODE EXIST
                			
                			
                			for (TreeItem<String> dayNode : monthNode.getChildren()){
                				
                				if (dayNode.getValue().contentEquals(daySDF.format(record.getTime()))){ //// YEAR NODE EXIST, MONTH NODE EXIST, DAY NODE EXIST
                					
                					TreeItem<String> recordNode = new TreeItem<String>(
                                    		recordSDF.format(record.getTime()) + " - " + record.getTerminal());
                					
                					dayNode.getChildren().add(recordNode);
                					foundDay = true;
                					break;
                				}
                				
                			}
                			
                			if (!foundDay){
                				
                				TreeItem<String> dayNode = new TreeItem<String>(
                                		daySDF.format(record.getTime()));
                                
                                TreeItem<String> recordNode = new TreeItem<String>(
                                		recordSDF.format(record.getTime()) + " - " + record.getTerminal());
                                
                                dayNode.getChildren().add(recordNode);
                                monthNode.getChildren().add(dayNode);
                				
                			}
                			
                			foundDay = false;
                			
                			foundMonth = true;
                			break;
                		}
                		
                		
                	}
                	
                	if (!foundMonth){																//// YEAR NODE EXIST, MONTH NODE DOESNT EXIST
                		
                		TreeItem<String> monthNode = new TreeItem<String>(
                        		monthSDF.format(record.getTime()));
                		
                		TreeItem<String> dayNode = new TreeItem<String>(
                        		daySDF.format(record.getTime()));
                        
                        TreeItem<String> recordNode = new TreeItem<String>(
                        		recordSDF.format(record.getTime()) + " - " + record.getTerminal());
                        
                        dayNode.getChildren().add(recordNode);
                        monthNode.getChildren().add(dayNode);
                		yearNode.getChildren().add(monthNode);
                		
                	}
                	
                	foundMonth = false;
                	
                    foundYear = true;
                    break;
                }
            }
            if (!foundYear) {																/// NO YEAR NODE - CREATE ONE
                

            		
            	TreeItem<String> yearNode = new TreeItem<String>(
                		yearSDF.format(record.getTime()));
                
                TreeItem<String> monthNode = new TreeItem<String>(
                		monthSDF.format(record.getTime()));
                
                TreeItem<String> dayNode = new TreeItem<String>(
                		daySDF.format(record.getTime()));
                
                TreeItem<String> recordNode = new TreeItem<String>(
                		recordSDF.format(record.getTime()) + " - " + record.getTerminal());
                
                dayNode.getChildren().add(recordNode);
                
                
                monthNode.getChildren().add(dayNode);
                yearNode.getChildren().add(monthNode);
                rootNode.getChildren().add(yearNode);

            	

            }
			
            foundYear = false;
            
		}
		
		treeAttendance.setRoot(rootNode);
	}
	

}
