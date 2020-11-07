package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	boolean createTransfers(Transfer transfer);

	List<Transfer> getPendingTransfers(int id);

	List<Transfer> getAllTransfers();
	
	boolean updateTransferStatus(Transfer changedTransfer);
	
	

}
