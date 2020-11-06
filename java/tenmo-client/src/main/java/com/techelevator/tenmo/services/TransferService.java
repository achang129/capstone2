package com.techelevator.tenmo.services;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.Transfer;

public class TransferService {
	
	private final String BASE_URL;
	
	public static String AUTH_TOKEN = "";
	private AuthenticatedUser currentUser;
	private Scanner in;
	private final RestTemplate restTemplate = new RestTemplate();
	
	private Transfer[] transfers;
	private User[] users;
	
	public TransferService(String baseURL, AuthenticatedUser currentUser, InputStream input) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
		this.in = new Scanner(input);
	}
	
	public TransferService(String baseURL, InputStream input) {
		this.BASE_URL = baseURL;
		this.in = new Scanner(input);
	}
	
	public void viewTransferHistory(AuthenticatedUser currentUser) {
		User[] allUsers = restTemplate.exchange(BASE_URL + "accounts/", HttpMethod.GET,
				makeAuthEntity(currentUser.getToken()), User[].class).getBody();
		Transfer[] transfers = restTemplate.exchange(BASE_URL + "transfers/",
				HttpMethod.GET, makeAuthEntity(currentUser.getToken()), Transfer[].class).getBody();
		int transferId = promptForTransfers(transfers, allUsers, transfers.length);
		if(transferId  != 0) {
			for(Transfer transfer : transfers) {
				if (transferId == transfer.getTransferId()) {
					displayTransferDetails(transferId);
					break;
				} 
			}	
		} else {	// TODO Catch Invalid IDs
			System.out.println("Please enter a valid transfer ID");
		}
	}
	
	public int promptForTransfers(Transfer[] transfers, User[] users, int numberOfTransfers) {
		this.transfers = transfers;
		this.users = users;
		int transferSelection = 1;
		int counter = 0;
		String typeIdName = "";
		String userName = "";
		List<String> allTransfers = new ArrayList<>();
		System.out.println("------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID\tFrom/To \tAmount");
		System.out.println("------------------------------------");
		for(Transfer transfer : transfers) {
				for(User user : users) {
					if(counter < numberOfTransfers) {
						if(transfer.getAccountFromId() == user.getId()) {
							userName = user.getUsername();
							typeIdName = "From: ";
						}else if(transfer.getAccountToId() == user.getId()) {
							userName = user.getUsername();
							typeIdName = "To: ";
						}	
					}
					allTransfers.add(transfer.getTransferId() + "\t" + typeIdName + userName + " \t$ " + transfer.getAmount().toString());
				}
				counter++;
		}
		counter = 1;
		for (String transfer : allTransfers) {
			if (counter % 2 == 1) {
				System.out.println(transfer);
			}
			counter++;
		}
		System.out.println("");
		System.out.print("Please enter transfer ID to view details (0 to cancel): ");
		if (in.hasNextInt()) {
			transferSelection = in.nextInt();
			in.nextLine();
		}else {
			transferSelection = 999;
		}
		return transferSelection;
	}
	
	public void displayTransferDetails(int transferId) {
		String fromUser = "";
		String toUser = "";
		String transferType = "";
		String statusType = "";
		BigDecimal amount = null;
		System.out.println("-------------------");
		System.out.println("Transfer Details");
		System.out.println("-------------------");
		System.out.println("Id: " + transferId);
		for(Transfer transfer : transfers) {
			if (transferId == transfer.getTransferId()) {
			amount = transfer.getAmount();
			if(transfer.getTypeId() == 1) {
				transferType = "Request";
			}if(transfer.getTypeId() == 2) {
				transferType = "Send";
			}if(transfer.getStatusId() == 1) {
				statusType = "Pending";
			}if(transfer.getStatusId() == 2) {
				statusType = "Approved";
			}if(transfer.getStatusId() == 3) {
				statusType = "Rejected";
			}
			for(User user : users) {
				if(transfer.getAccountFromId() == user.getId()) {
					fromUser = user.getUsername();
				}else if(transfer.getAccountFromId() != user.getId()) {
					toUser = user.getUsername();
				}
			}
			}
		}
		System.out.println("From: " + fromUser);
		System.out.println("To: " + toUser);
		System.out.println("Type: " + transferType);
		System.out.println("Status: " + statusType);
		System.out.println("Amount: " + amount);
		
	}
	
	private HttpEntity makeAuthEntity(String userToken) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(userToken);
	    HttpEntity entity = new HttpEntity<>(headers);
	    return entity;
	  }
	
	private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String userToken){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(userToken);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}

}
