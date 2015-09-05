package com.rylow.cardreaderadmin;

public class Card {
	
	private int id, holderID, type;
	private String cardNumber;
	
	public Card(){
		
	}
	
	public Card(int id, String cardNumber, int holderID, int type){
		
		this.id = id;
		this.cardNumber = cardNumber;
		this.holderID = holderID;
		this.type = type;
		
	}
	
	@Override
	public String toString(){
		
		return cardNumber;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getHolderID() {
		return holderID;
	}
	public void setHolderID(int holderID) {
		this.holderID = holderID;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	

}
