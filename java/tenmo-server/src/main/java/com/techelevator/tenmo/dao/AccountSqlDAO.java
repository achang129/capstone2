package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

@Service
public class AccountSqlDAO implements AccountDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public AccountSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public BigDecimal findBalanceById(int id) {
		BigDecimal balance = null;
		String getBalance = "SELECT balance FROM accounts WHERE user_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getBalance, id);
		if(result.next()) {
			balance = result.getBigDecimal("balance");
		}
		return balance;
	}
	
	@Override
	public boolean updateAccounts(BigDecimal amount, int id) {
		String updateBalance = "UPDATE accounts SET balance = ? WHERE user_id = ?";
		return jdbcTemplate.update(updateBalance, amount, id) == 1;
		
	}

}
