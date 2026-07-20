package com.phetolo.PersonalFinance.exception;

public class TransactionNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionNotFoundException(String message) {
		super(message);
	}
}
