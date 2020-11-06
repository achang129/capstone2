package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.techelevator.tenmo.model.Transfer;

@Service
public class TransferSqlDAO implements TransferDAO {

	private JdbcTemplate jdbcTemplate;

	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public boolean createTransfers(Transfer transfer) {
		String createTranfer = "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount)"
				+ " VALUES(?, ?, ?, ?, ?)";
		if (transfer.getAccountFromId() != transfer.getAccountToId()) {
			return jdbcTemplate.update(createTranfer, transfer.getTypeId(), transfer.getStatusId(),
					transfer.getAccountFromId(), transfer.getAccountToId(), transfer.getAmount()) == 1;
		} else {
			return false;
		}
	}

	@Override
	public List<Transfer> getTransfers(int id) {
		List<Transfer> transfers = new ArrayList<>();
		String getTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfers WHERE account_from = ? OR account_to = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getTransfers, id, id);

		while (result.next()) {
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

		while (result.next()) {
			Transfer transfer = mapRowToTransfer(result);
			transfers.add(transfer);
		}

		return transfers;
	}
	
	@Override
	public List<Transfer> getFromTransfers() {
		List<Transfer> transfers = new ArrayList<>();
		String getTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to,u.user_id, u.username "
				+ "FROM transfers t INNER JOIN accounts a ON t.account_from=a.account_id  INNER JOIN users u ON a.user_id = u.user_id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getTransfers);

		while (result.next()) {
			Transfer transfer = mapRowToTransfer(result);
			transfers.add(transfer);
		}

		return transfers;
	}
	
	@Override
	public List<Transfer> getToTransfers() {
		List<Transfer> transfers = new ArrayList<>();
		String getTransfers = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, z.user_id, z.username "
				+ "FROM transfers t INNER JOIN accounts b ON t.account_to = b.account_id INNER JOIN users z ON b.user_id = z.user_id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(getTransfers);

		while (result.next()) {
			Transfer transfer = mapRowToTransfer(result);
			transfers.add(transfer);
		}

		return transfers;
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
