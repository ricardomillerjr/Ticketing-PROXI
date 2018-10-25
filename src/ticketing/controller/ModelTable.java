/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticketing.controller;

import java.sql.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author JamesUriel
 */
public class ModelTable {

	private String TicketNumber;
	private String Type;
	private String date;

	public ModelTable(String TicketNumber, String Type, String date) {
		this.TicketNumber = TicketNumber;
		this.Type = Type;
		this.date = date;
	}

	/**
	 * @return the TicketNumber
	 */
	public String getTicketNumber() {
		return TicketNumber;
	}

	/**
	 * @param TicketNumber the TicketNumber to set
	 */
	public void setTicketNumber(String TicketNumber) {
		this.TicketNumber = TicketNumber;
	}

	/**
	 * @return the Type
	 */
	public String getType() {
		return Type;
	}

	/**
	 * @param Type the Type to set
	 */
	public void setType(String Type) {
		this.Type = Type;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
}
