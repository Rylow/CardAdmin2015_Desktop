package com.rylow.cardreaderadmin;

public class GuestCard {
	
	private String name;
	private int id;
	
	public GuestCard(String name, int id){
		this.setName(name);
		this.setId(id);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString(){
		
		return name;
		
	}

}
