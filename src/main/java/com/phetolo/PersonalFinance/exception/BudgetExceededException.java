package com.phetolo.PersonalFinance.exception;

public class BudgetExceededException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BudgetExceededException(String message) {
		super(message);
	}
}
