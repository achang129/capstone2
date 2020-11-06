package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.LoginDTO;
import com.techelevator.tenmo.model.RegisterUserDTO;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAlreadyExistsException;
import com.techelevator.tenmo.security.jwt.JWTFilter;
import com.techelevator.tenmo.security.jwt.TokenProvider;

@RestController
public class AccountController {

	private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private AccountDAO accountDAO;
	private UserDAO userDAO;

	public AccountController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, AccountDAO accountDAO, UserDAO userDAO) {
		this.tokenProvider = tokenProvider;
    	this.authenticationManagerBuilder = authenticationManagerBuilder;
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

	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.PUT)
	public void updateAccountBalance(@RequestBody BigDecimal balance, @PathVariable int id) {
		accountDAO.updateAccounts(balance, id);

	}

}
