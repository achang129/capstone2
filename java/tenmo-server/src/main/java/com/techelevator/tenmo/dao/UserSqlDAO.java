package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

@Service
public class UserSqlDAO implements UserDAO {

    private static final double STARTING_BALANCE = 1000;
    private JdbcTemplate jdbcTemplate;

    public UserSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        return jdbcTemplate.queryForObject("select user_id from users where username = ?", int.class, username);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        for (User user : this.findAll()) {
            if( user.getUsername().toLowerCase().equals(username.toLowerCase())) {
                return user;
            }
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {
        boolean userCreated = false;
        boolean accountCreated = false;

        // create user
        String insertUser = "insert into users (username,password_hash) values(?,?)";
        String password_hash = new BCryptPasswordEncoder().encode(password);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "user_id";
        userCreated = jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(insertUser, new String[]{id_column});
                    ps.setString(1, username);
                    ps.setString(2,password_hash);
                    return ps;
                }
                , keyHolder) == 1;
        int newUserId = (int) keyHolder.getKeys().get(id_column);

        // create account
        String insertAccount = "insert into accounts (user_id,balance) values(?,?)";
        accountCreated = jdbcTemplate.update(insertAccount,newUserId,STARTING_BALANCE) == 1;

        return userCreated && accountCreated;
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
	
	@Override
	public boolean createTransfers(Transfer transfer) {
		String createTranfer = "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)"
								+ " VALUES(?, ?, ?, ?, ?)";
		if(transfer.getAccountFromId() != transfer.getAccountToId()) {
		return jdbcTemplate.update(createTranfer, transfer.getTypeId(), transfer.getStatusId()
										, transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount()) == 1;
		} else {
			return false;
		}
	}
	
	@Override
	public List<Transfer> getTransfers(int id) {
		List<Transfer> transfers = new ArrayList<>();
		String getTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE account_from = ? OR account_to = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getTransfers, id, id);
		
		while(result.next()) {
			Transfer transfer = mapRowToTransfer(result);
			transfers.add(transfer);
		}
		
		return transfers;
	}
	
	@Override
	public List<Transfer> getAllTransfers() {
		List<Transfer> transfers = new ArrayList<>();
		String getTransfers = "SELECT * FROM transfers";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getTransfers);
		
		while(result.next()) {
			Transfer transfer = mapRowToTransfer(result);
			transfers.add(transfer);
		}
		
		return transfers;
	}

	
	  private User mapRowToUser(SqlRowSet rs) {
	        User user = new User();
	        user.setId(rs.getLong("user_id"));
	        user.setUsername(rs.getString("username"));
	        user.setPassword(rs.getString("password_hash"));
	        user.setActivated(true);
	        user.setAuthorities("ROLE_USER");
	        return user;
	    }
	  
	  private Transfer mapRowToTransfer(SqlRowSet rs) {
		  Transfer transfer = new Transfer();
		  transfer.setTransferId(rs.getInt("transfer_id"));
		  transfer.setTypeId(rs.getInt("transfer_type_id"));
		  transfer.setStatusId(rs.getInt("transfer_status_id"));
		  transfer.setAccountFromId(rs.getInt("account_from"));
		  transfer.setAccountToId(rs.getInt("account_to"));
		  transfer.setAmount(rs.getBigDecimal("amount"));
		  return transfer;
	  }

	
	

	

	
}
