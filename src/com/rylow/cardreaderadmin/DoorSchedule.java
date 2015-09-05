package com.rylow.cardreaderadmin;

import javafx.beans.property.SimpleStringProperty;

public class DoorSchedule {
	
	private final SimpleStringProperty name;
	private final SimpleStringProperty from;
	private final SimpleStringProperty to;
	private final SimpleStringProperty dayType;
	
	public DoorSchedule(String name, String from, String to, String dayType){
		
		this.name = new SimpleStringProperty(name);
		this.from = new SimpleStringProperty(from);
		this.to = new SimpleStringProperty(to);
		this.dayType = new SimpleStringProperty(dayType);
		
	}
	
	public String getName() {
		return name.get();
	}

	public String getFrom() {
		return from.get();
	}
	
	public String getTo() {
		return to.get();
	}
	
	public String getDayType() {
		return dayType.get();
	}

}
