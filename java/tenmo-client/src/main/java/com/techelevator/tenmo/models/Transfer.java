package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {
	
	private int transferId;
	private int typeId;
	private int statusId;
	private int accountFromId;
	private int accountToId;
	private BigDecimal amount;
	
	public Transfer(int transferId, int typeId, int statusId, int accountFromId, int accountToId, BigDecimal amount) {
		this.transferId = transferId;
		this.typeId = typeId;
		this.statusId = statusId;
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.amount = amount;
	}
	

	public Transfer(int typeId, int statusId, int accountFromId, int accountToId, BigDecimal amount) {
		this.typeId = typeId;
		this.statusId = statusId;
		this.accountFromId = accountFromId;
		this.accountToId = accountToId;
		this.amount = amount;
	}

	
	public Transfer() {
		
	}
	
	public int getTransferId() {
		return transferId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public int getAccountFromId() {
		return accountFromId;
	}
	public void setAccountFromId(int accountFromId) {
		this.accountFromId = accountFromId;
	}
	public int getAccountToId() {
		return accountToId;
	}
	public void setAccountToId(int accountToId) {
		this.accountToId = accountToId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	
	

}
