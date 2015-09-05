package com.rylow.cardreaderadmin;

import java.sql.Timestamp;


public class StaffAttendanceRecord {

	private Timestamp time;
	private String terminal;
	
	public StaffAttendanceRecord (String terminal, Timestamp time){
		
		this.setTime(time);
		this.setTerminal(terminal);
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	
	public String getTerminal() {
		return terminal;
	}

	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
	
	
}
