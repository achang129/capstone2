package com.techelevator.view;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;

import com.techelevator.tenmo.models.User;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

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
		System.out.println("------------------------------");
		System.out.println("Users");
		System.out.println("ID      Name");
		System.out.println("------------------------------");
		for (User user : users) {
			System.out.println(user.getId() + "       " + user.getUsername());
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
}
