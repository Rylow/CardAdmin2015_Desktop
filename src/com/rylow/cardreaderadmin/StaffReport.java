package com.rylow.cardreaderadmin;

import javafx.beans.property.SimpleStringProperty;

public class StaffReport {
	
	private final SimpleStringProperty name;
	private final SimpleStringProperty since;
	
	public StaffReport (String name, String since){
		
		this.name = new SimpleStringProperty(name);
		this.since = new SimpleStringProperty(since);
	}

	public String getName() {
		return name.get();
	}

	public String getSince() {
		return since.get();
	}

}
