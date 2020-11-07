package com.techelevator.tenmo.services;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Scanner;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.services.AccountService;

@RestController
public class AccountService {
	
	private final String BASE_URL;
	
	public static String AUTH_TOKEN = "";
	private AuthenticatedUser currentUser;
	private Scanner in;
	private final RestTemplate restTemplate = new RestTemplate();
	
	public AccountService(String baseURL, AuthenticatedUser currentUser, InputStream input) {
		this.BASE_URL = baseURL;
		this.currentUser = currentUser;
		this.in = new Scanner(input);
	}
	
	public AccountService(String baseURL, InputStream input) {
		this.BASE_URL = baseURL;
		this.in = new Scanner(input);
	}
	
	public BigDecimal getBalance(AuthenticatedUser currentUser) {
		BigDecimal currentBalance = null;
		currentBalance = restTemplate.exchange(BASE_URL + "accounts/" + currentUser.getUser().getId(),
				HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
		return currentBalance;
	}
	
	public void sendBucks(AuthenticatedUser currentUser) {
		User[] allUsers = restTemplate.exchange(BASE_URL + "accounts/", HttpMethod.GET, makeAuthEntity(currentUser.getToken()), User[].class).getBody();
		int userId = promptForUsers(allUsers, " sending to");
		if(userId == 0) {
			//console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
		} else if (userId != currentUser.getUser().getId() && userId < allUsers.length + 1 && userId > 0) {
			BigDecimal amount = promptForAmount(userId);
			BigDecimal currentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
			BigDecimal sentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
			int result = amount.compareTo(currentUsersBalance);
			if (result == -1) {
				BigDecimal currentUsersNewBalance = currentUsersBalance.subtract(amount);
				BigDecimal sentUsersNewBalance = sentUsersBalance.add(amount);
				restTemplate.put(BASE_URL + "accounts/" + userId, makeUserEntity(sentUsersNewBalance, currentUser.getToken()));
				restTemplate.put(BASE_URL + "accounts/" + currentUser.getUser().getId(), makeUserEntity(currentUsersNewBalance, currentUser.getToken()));
				System.out.println("Approved. " + currentUser.getUser().getUsername() + " sent " + amount + " TE Bucks to " + allUsers[userId - 1].getUsername());
				//String amountString = amount.toString();
				//double amountDouble = Double.valueOf(amountString);
				Transfer newTransfer = new Transfer(2, 2, currentUser.getUser().getId(), userId, amount);
				//Transfer[] transfers = new Transfer[] {newTransfer};
				restTemplate.postForObject(BASE_URL + "transfers/", makeTransferEntity(newTransfer, currentUser.getToken()), Transfer.class);
			} else {
				System.out.println("Not enough funds in your account");
			}
		} else {
			System.out.println("Invalid Option");
		}
	}
	
	
	public void requestBuck(AuthenticatedUser currentUser) {
		User[] allUsers = restTemplate.exchange(BASE_URL + "accounts/", HttpMethod.GET, makeAuthEntity(currentUser.getToken()), User[].class).getBody();
		int userId = promptForUsers(allUsers, " requesting from");
		if(userId == 0) {
		}else if (userId != currentUser.getUser().getId() && userId < allUsers.length + 1 && userId > 0) {
			BigDecimal amount = promptForAmount(userId);
			BigDecimal currentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
			BigDecimal sentUsersBalance = restTemplate.exchange(BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(currentUser.getToken()), BigDecimal.class).getBody();
			Transfer newTransfer = new Transfer(1, 1, userId, currentUser.getUser().getId(), amount);
			restTemplate.postForObject(BASE_URL + "transfers/", makeTransferEntity(newTransfer, currentUser.getToken()), Transfer.class);
			System.out.println("Approved. " + currentUser.getUser().getUsername() + " requested " + amount + " TE Bucks from " + allUsers[userId - 1].getUsername());

			}
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
	
	private HttpEntity<BigDecimal> makeUserEntity(BigDecimal amount, String userToken){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(userToken);
		HttpEntity<BigDecimal> entity = new HttpEntity<>(amount, headers);
		return entity;
	}

}
