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
	private Scanner in;
	private final RestTemplate restTemplate = new RestTemplate();

	private Transfer[] transfers;
	private User[] users;

	public TransferService(String baseURL, InputStream input) {
		this.BASE_URL = baseURL;
		this.in = new Scanner(input);
	}

	public void viewTransferHistory(AuthenticatedUser currentUser) {
		User[] allUsers = restTemplate
				.exchange(BASE_URL + "accounts/", HttpMethod.GET, makeAuthEntity(currentUser.getToken()), User[].class)
				.getBody();
		Transfer[] transfers = restTemplate.exchange(BASE_URL + "transfers/", HttpMethod.GET,
				makeAuthEntity(currentUser.getToken()), Transfer[].class).getBody();
		int transferId = promptForTransfers(transfers, allUsers, transfers.length);
		if (transferId > 0) {
			for (Transfer transfer : transfers) {
				if (transferId == transfer.getTransferId()) {
					displayTransferDetails(transferId);
					break;
				} 
			}
		} else if (transferId == 0) {
			
		} else {
			System.out.println("Please enter a valid transfer ID");
		}
	}

	public void viewPendingTransferHistory(AuthenticatedUser currentUser) {
		int pendingTransferSelection = 0;
		List<Transfer> pendingTransfers = new ArrayList<>();
		User[] allUsers = restTemplate.exchange(BASE_URL + "accounts/", HttpMethod.GET, makeAuthEntity(currentUser.getToken()), User[].class)
				.getBody();
		Transfer[] transfers = restTemplate.exchange(BASE_URL + "transfers/",
				HttpMethod.GET, makeAuthEntity(currentUser.getToken()), Transfer[].class).getBody();
		for(Transfer pendingTransfer : transfers) {
			if(pendingTransfer.getStatusId() == 1 && currentUser.getUser().getId() != pendingTransfer.getAccountToId()) {
				pendingTransfers.add(pendingTransfer);
			}
		}
		int transferId = promptForPendingTransfers(pendingTransfers, allUsers, transfers.length);
		if (transferId > 0) {
			for (Transfer transfer : transfers) {
				if (transfer.getStatusId() == 1) {
					if (transferId == transfer.getTransferId()) {
						pendingTransferSelection = displayPendingTransferDetails(transferId);
						pendingTransferSelection += 1;
						if (pendingTransferSelection == 2) {
							BigDecimal currentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
							int sentUsersId = transfer.getAccountToId();
							BigDecimal sentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + sentUsersId, HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
							BigDecimal result = transfer.getAmount();
							if (result.compareTo(currentUsersBalance) == -1 || result.compareTo(currentUsersBalance) == 0) {
								BigDecimal currentUsersNewBalance = currentUsersBalance.subtract(result);
								BigDecimal sentUsersNewBalance = sentUsersBalance.add(result);
								restTemplate.put(BASE_URL + "accounts/" + sentUsersId, makeUserEntity(sentUsersNewBalance, currentUser.getToken()));
								restTemplate.put(BASE_URL + "accounts/" + currentUser.getUser().getId(), makeUserEntity(currentUsersNewBalance, currentUser.getToken()));
								Transfer changedTransfer = new Transfer(transfer.getTransferId(), transfer.getTypeId(), pendingTransferSelection, transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
								restTemplate.put(BASE_URL + "transfers/" + currentUser.getUser().getId(), makeTransferEntity(changedTransfer, currentUser.getToken()));
								break;
							} else {
								System.out.println("Not enough funds in your account");
							}
						} else if (pendingTransferSelection == 3) {
							System.out.println("Request was rejected");
							Transfer changedTransfer = new Transfer(transfer.getTransferId(), transfer.getTypeId(), pendingTransferSelection, transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount());
							restTemplate.put(BASE_URL + "transfers/" + currentUser.getUser().getId(), makeTransferEntity(changedTransfer, currentUser.getToken()));
							break;
						}
					}
				}
			}
		} else if (transferId == 0) {
		
		} else {
			System.out.println("Please enter a valid transfer ID");
		}
	}

	public int promptForTransfers(Transfer[] transfers, User[] users, int numberOfTransfers) {
		this.transfers = transfers;
		this.users = users;
		int transferSelection = 1;
		int counter = 0;
		String toUserName = "";
		String userName = "";
		List<String> allTransfers = new ArrayList<>();
		System.out.println("---------------------------------");
		System.out.println("Transfers");
		System.out.println("ID\tFrom\tTo \tAmount");
		System.out.println("---------------------------------");
		for (Transfer transfer : transfers) {
			for (User user : users) {
				if (counter < numberOfTransfers) {
					if (transfer.getAccountFromId() == user.getId()) {
						userName = user.getUsername();
						for (User userTo : users) {
							if (transfer.getAccountToId() == userTo.getId()) {
								toUserName = userTo.getUsername();
							}
						}
					}
				}
				allTransfers.add(transfer.getTransferId() + "\t" + userName + " \t" + toUserName + "\t$"
						+ transfer.getAmount().toString());
			}
			counter++;
		}
		counter = 1;
		for (String transfer : allTransfers) {
			if (counter % users.length == 0) {
				System.out.println(transfer);
			}
			counter++;
		}
		System.out.println("");
		System.out.print("Please enter transfer ID to view details (0 to cancel): ");
		if (in.hasNextInt()) {
			transferSelection = in.nextInt();
			in.nextLine();
		} else {
			transferSelection = 999;
		}
		return transferSelection;
	}

	public int promptForPendingTransfers(List<Transfer> pendingTransfers, User[] users, int numberOfTransfers) {
		int transferSelection = 1;
		int counter = 0;
		String userName = "";
		System.out.println("------------------------------------");
		System.out.println("Pending Transfers");
		System.out.println("ID\tTo \tAmount");
		System.out.println("------------------------------------");
		for (Transfer transfer : pendingTransfers) {
			for (User user : users) {
				if (counter < numberOfTransfers) {
					if (transfer.getAccountToId() == user.getId()) {
						userName = user.getUsername();
						System.out.println(transfer.getTransferId() + "\t" + userName + "\t" + transfer.getAmount());
					}
				}
				
			}
			counter++;
		}
		System.out.println("");
		System.out.print("Please enter transfer ID to approve/reject (0 to cancel): ");
		if (in.hasNextInt()) {
			transferSelection = in.nextInt();
			in.nextLine();
		} else {
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
		for (Transfer transfer : transfers) {
			if (transferId == transfer.getTransferId()) {
				amount = transfer.getAmount();
				if (transfer.getTypeId() == 1) {
					transferType = "Request";
				}
				if (transfer.getTypeId() == 2) {
					transferType = "Send";
				}
				if (transfer.getStatusId() == 1) {
					statusType = "Pending";
				}
				if (transfer.getStatusId() == 2) {
					statusType = "Approved";
				}
				if (transfer.getStatusId() == 3) {
					statusType = "Rejected";
				}
				for (User user : users) {
					if (transfer.getAccountFromId() == user.getId()) {
						fromUser = user.getUsername();
					} else if (transfer.getAccountToId() == user.getId()) {
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
	
	public int displayPendingTransferDetails(int transferId) {
		int pendingTransferSelection = 0;
		System.out.println("");
		System.out.println("1: Approve");
		System.out.println("2: Reject");
		System.out.println("0: Don't approve or reject");
		System.out.println("--------------------------");
		System.out.print("Please choose an option: ");
		if (in.hasNextInt()) {
			pendingTransferSelection = in.nextInt();
			in.nextLine();
		} else {
			pendingTransferSelection = 999;
		}
		return pendingTransferSelection;
	}

	private HttpEntity makeAuthEntity(String userToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(userToken);
		HttpEntity entity = new HttpEntity<>(headers);
		return entity;
	}

	private HttpEntity<Transfer> makeTransferEntity(Transfer transfer, String userToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(userToken);
		HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
		return entity;
	}
	
	private HttpEntity<BigDecimal> makeUserEntity(BigDecimal amount, String userToken){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(userToken);
		HttpEntity<BigDecimal> entity = new HttpEntity<>(amount, headers);
		return entity;
	}

}
