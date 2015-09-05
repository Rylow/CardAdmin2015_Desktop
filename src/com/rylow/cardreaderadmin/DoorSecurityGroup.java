package com.rylow.cardreaderadmin;

public class DoorSecurityGroup {
	
	private int id;
	private String name;
	
	public DoorSecurityGroup (int id, String name){
		
		this.setId(id);
		this.setName(name);
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return name;
	}

}
