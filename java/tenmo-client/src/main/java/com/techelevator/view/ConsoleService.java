package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;
	private Transfer[] transfers;
	private User[] users;
	private AuthenticatedUser currentUser;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": ");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println("\n*** " + userInput + " is not valid ***\n");
			}
		} while(result == null);
		return result;
	}
	
	public int promptForUsers(User[] users, String action) {
		int menuSelection = 1;
		System.out.println("------------------------------------");
		System.out.println("Users");
		System.out.println("ID\tName");
		System.out.println("------------------------------------");
		for (User user : users) {
			System.out.println(user.getId() + "\t" + user.getUsername());
		}
		System.out.println("");
		System.out.print("Enter ID of user you are" + action + " (0 to cancel): ");
		if (in.hasNextInt()) {
			menuSelection = in.nextInt();
			in.nextLine();
		}else {
			menuSelection = 999;
		}
		return menuSelection;
	}
	
	public BigDecimal promptForAmount(int id) {
		BigDecimal amount = null;
		System.out.print("Enter Amount: ");
		String userInput = in.nextLine();
		try {
			double result = Double.valueOf(userInput);
			amount = BigDecimal.valueOf(result);
		} catch(Exception ex) {
			System.out.println("Something went wrong.");
		}
		return amount;
		
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
		System.out.println("From: " + fromUser);
		System.out.println("To: " + toUser);
		System.out.println("Type: " + transferType);
		System.out.println("Status: " + statusType);
		System.out.println("Amount: " + amount);
		
	}
}
