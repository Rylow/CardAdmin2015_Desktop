package com.rylow.cardreaderadmin;

public class SecurityGroup {
	
	private int id;
	private String name;
	
	public SecurityGroup(int id, String name){
		
		this.setId(id);
		this.name = name;
		
	}
	
	@Override
	public String toString(){
		
		return name;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
