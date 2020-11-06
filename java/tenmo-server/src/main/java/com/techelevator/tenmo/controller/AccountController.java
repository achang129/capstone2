package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

	private AccountDAO accountDAO;
	private UserDAO userDAO;

	public AccountController(AccountDAO accountDAO, UserDAO userDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
	}

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public List<User> listUsers() {
		return userDAO.findAll();
	}

	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
	public BigDecimal getAccountBalance(@PathVariable int id) {
		return accountDAO.findBalanceById(id);
	}

	@PreAuthorize("permitAll")
	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.PUT)
	public void updateAccountBalance(@RequestBody BigDecimal balance, @PathVariable int id) {
		accountDAO.updateAccounts(balance, id);

	}

}
