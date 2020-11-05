package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
	public static String AUTH_TOKEN = "";
	private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		BigDecimal currentBalance = null;
		currentBalance = restTemplate.exchange(API_BASE_URL + "accounts/" + currentUser.getUser().getId(),
				HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
		System.out.println("Your current account balance is: " + currentBalance);
	}

	private void viewTransferHistory() {
		// TODO View Transfer
		
		User[] allUsers = restTemplate.exchange(API_BASE_URL + "accounts/", HttpMethod.GET,
				makeAuthEntity(), User[].class).getBody();
		Transfer[] transfers = restTemplate.exchange(API_BASE_URL + "transfers/" + currentUser.getUser().getId(),
				HttpMethod.GET, makeAuthEntity(), Transfer[].class).getBody();
		int transferId = console.promptForTransfers(transfers, allUsers);
		if(transferId == 0) {
			console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
		}else if(transferId > transfers.length + 1 || transferId < 1) {
			System.out.println("Please enter a vaild transfer ID");
		}else {
			console.displayTransferDetails(transferId);
		}
		
	}

	private void viewPendingRequests() {
		// TODO View Pending
		
	}

	private void sendBucks() {
		User[] allUsers = restTemplate.exchange(API_BASE_URL + "accounts/", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
		int userId = console.promptForUsers(allUsers, "sending to");
		if(userId == 0) {
			console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
		} else if (userId != currentUser.getUser().getId() && userId < allUsers.length + 1 && userId > 0) {
			BigDecimal amount = console.promptForAmount(userId);
			BigDecimal currentUsersBalance = restTemplate.exchange(API_BASE_URL + "accounts/" + currentUser.getUser().getId(), HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
			BigDecimal sentUsersBalance = restTemplate.exchange(API_BASE_URL + "accounts/" + userId, HttpMethod.GET, makeAuthEntity(), BigDecimal.class).getBody();
			int result = amount.compareTo(currentUsersBalance);
			if (result == -1) {
				BigDecimal currentUsersNewBalance = currentUsersBalance.subtract(amount);
				BigDecimal sentUsersNewBalance = sentUsersBalance.add(amount);
				restTemplate.put(API_BASE_URL + "accounts/" + userId, sentUsersNewBalance);
				restTemplate.put(API_BASE_URL + "accounts/" + currentUser.getUser().getId(), currentUsersNewBalance);
				System.out.println("Approved. " + currentUser.getUser().getUsername() + " sent " + amount + " TE Bucks to " + allUsers[userId - 1].getUsername());
			} else {
				System.out.println("Not enough funds in your account");
			}
		} else {
			System.out.println("Invalid Option");
		}
	}

	private void requestBucks() {
		// TODO Request
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

	private HttpEntity makeAuthEntity() {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setBearerAuth(AUTH_TOKEN);
	    HttpEntity entity = new HttpEntity<>(headers);
	    return entity;
	  }
}
