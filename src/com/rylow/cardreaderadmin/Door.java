package com.rylow.cardreaderadmin;

public class Door {
	
	private String name;
	private int id;
	
	public Door (String name, int id){
		
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
