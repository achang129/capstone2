package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
    
    BigDecimal findBalanceById(int id);
    
    boolean updateAccounts(BigDecimal amount, int id);
    
    boolean createTransfers(Transfer transfer);
    
    List<Transfer> getTransfers(int id);
    
}
