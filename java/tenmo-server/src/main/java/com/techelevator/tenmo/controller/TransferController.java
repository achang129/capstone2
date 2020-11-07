package com.techelevator.tenmo.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

	private TransferDAO transferDAO;

	public TransferController(TransferDAO transferDAO) {
		this.transferDAO = transferDAO;
	}

	@RequestMapping(path = "/transfers/", method = RequestMethod.GET)
	public List<Transfer> list() {
		return transferDAO.getAllTransfers();
	}

	@RequestMapping(value = "/transfers/", method = RequestMethod.POST)
	public void createTranfsers(@Valid @RequestBody Transfer transfer) {
		transferDAO.createTransfers(transfer);
	}

	@RequestMapping(value = "/transfers/{id}", method = RequestMethod.GET)
	public void getTransfers(@PathVariable int id) {
		transferDAO.getPendingTransfers(id);
	}
	
	@RequestMapping(value = "/transfers/{id}", method = RequestMethod.PUT)
	public void updateTransferStatus(@Valid @RequestBody Transfer changedTransfer, @PathVariable int id) {
		transferDAO.updateTransferStatus(changedTransfer);
	}

}
