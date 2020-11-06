package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface AccountDAO {
	
	 BigDecimal findBalanceById(int id);
	    
	 boolean updateAccounts(BigDecimal amount, int id);

}
