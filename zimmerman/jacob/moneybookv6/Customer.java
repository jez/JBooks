/**
 * @Customer.java
 *
 * Last updated: 2011/5/30
 * @author Jacob Zimmerman
 * @version 6.00 2011/5/30
 */

package zimmerman.jacob.moneybookv6;

import java.io.Serializable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * The Customer class extends the ArrayList class so that it can keep track of a customer
 * as well as any transactions that occur between that customer. It also keeps track of an 
 * address, phone number, and email, as well as the total amount that this customer has 
 * paid and has yet to pay.
 */
public class Customer implements Comparable<Customer>, Serializable
{
	//Store info about the customer
	private String name;
	private String address;
	private String phoneNumber;
	private String email;
	private double totalReceived;
	private double balance;
	private ArrayList<Transaction> transactions;
	
	//Format the numbers into currency notation and dates into Strings. Example: Friday, June 11, 2010
	private NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	private SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	
	/**
	 * Creates a new Customer object.
	 *
	 * @param name the name of the new customer
	 */
	public Customer(String name)
	{
		this.name = name;
		address = "";
		phoneNumber = "";
		email = "";
		totalReceived = 0;
		balance = 0;
		
		transactions = new ArrayList<Transaction>();
	}
	
	/**
	 * Creates a new Customer object.
	 *
	 * @param name the name of the new customer
	 * @param address the address of the new customer
	 * @param phoneNumber the phone number of the new customer
	 * @param email the email of the new customer
	 */
	public Customer(String name, String address, String phoneNumber, String email)
	{
		super();
		
		this.name = name;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.email = email;
		totalReceived = 0;
		balance = 0;
		
		transactions = new ArrayList<Transaction>();
	}
	
	/**
	 * Adds a transaction to the ArrayList part of this class
	 *
	 * @param element the Transaction object to be added
	 *
	 * @return true if the element was added successfully
	 */
	public boolean add(Transaction element)
	{
		boolean result = transactions.add(element);
		Collections.sort(transactions);
		
		totalReceived = 0;
		balance = 0;
		
		for(Transaction t : transactions)
		{
			if(t.getType() == TType.PAYMENT)
				totalReceived += -(t.getAmount());
			else if(t.getType() == TType.TIP)
				totalReceived += t.getAmount();
			if(t.getType() != TType.TIP)
				balance += t.getAmount();
		}
		return result;
	}
	
	/**
	 * Remove a transaction from the ArrayList part of this class
	 *
	 * @param index the index of the transaction to be removed
	 *
	 * @return the Transaction that was removed
	 */
	public Transaction remove(int index)
	{
		Transaction result = transactions.remove(index);
		if(result.getType() == TType.PAYMENT)
			totalReceived -= -(result.getAmount());
		else if(result.getType() == TType.TIP)
			totalReceived -= result.getAmount();
		if(result.getType() != TType.TIP)
			balance -= result.getAmount();
		return result;
	}
	
	/**
	 * @return the name of this customer
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Used to change the name of this customer
	 */
	public void setName(String name)
	{
		this.name = name;
	} 
	
	/**
	 * @return the address of this customer
	 */
	public String getAddress()
	{
		return address;
	}
	
	/**
	 * Sets the address of this customer then writes the data to its file
	 *
	 * @param address the new address of the customer
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/**
	 * @return the phone number of this customer
	 */
	public String getPhoneNumber()
	{
		return phoneNumber;
	}
	
	/**
	 * Sets the phone number of this customer then writes the data to its file
	 *
	 * @param phoneNumber the new phone number of the customer
	 */
	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * @return the email of this customer
	 */
	public String getEmail()
	{
		return email;
	}
	
	/**
	 * Sets the email of this customer then writes the data to its file
	 *
	 * @param email the new email of the customer
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	/**
	 * @return the total amount received as a double value
	 */
	public double getTotalReceived()
	{
		return totalReceived;
	}
	
	/**
	 * @return the total amount pending as a double value
	 */
	public double getBalance()
	{
		return balance;
	}
	
	/**
	 * Compares this customer to another by comparing the Strings containing the names of the two customers
	 * 
	 * @param obj the Customer object to compare this customer to
	 *
	 * @return an int value that tells whether this customer is less than (-), equal to (0), or greater than (+) another customer
	 */
	public int compareTo(Customer obj)
	{
		return name.compareTo(obj.getName());
	}
	
	public String getXML()
	{
		String result = "";
		
		result += "<customer>\n";
		result += "\t<name>" + name + "</name>\n";
		
		if(!address.equals(""))
			result += "\t<address>" + address + "</address>\n";
		else
			result += "\t<address></address>\n";
			
		if(!phoneNumber.equals(""))
			result += "\t<phoneNumber>" + phoneNumber + "</phoneNumber>\n";
		else
			result += "\t<phoneNumber></phoneNumber>\n";
			
		if(!email.equals(""))
			result += "\t<email>" + email + "</email>\n";
		else
			result += "\t<email></email>\n";
		
		String temp = "";
		Scanner scan;
		for(Transaction t : transactions)
		{
			temp = t.getXML();
			scan = new Scanner(temp);
			
			while(scan.hasNext())
			{
				result += "\t" + scan.nextLine() + "\n";
			}
		}
		
		result += "\t<totalReceived>" + nfmt.format(totalReceived) + "</totalReceived>\n";
		result += "\t<balance>" + nfmt.format(balance) + "</balance>\n";
		
		result += "</customer>\n";
		
		return result;
	}
	
	/**
	 * Returns the last time this customer had its grass cut. Returns null if no grass 
	 * has ever been cut
	 *
	 * @return a GregorianCalendar object that represents the date of the last grass cutting
	 */
	public GregorianCalendar getLastCutDate()
	{
		GregorianCalendar result = null;
		
		for(Transaction t : transactions)
		{
			if(t.getType() == TType.GRASS_CUT || t.getType() == TType.GRASS_CUT_HALF_OFF || t.getType() == TType.GRASS_CUT_WITH_WEEDWHACK)
			{
				result = t.getDate();
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the transaction ArrayList
	 *
	 * @return an ArrayList containing all of the transactions
	 */
	public ArrayList<Transaction> getTransactions()
	{
		return transactions;
	}
	
	/**
	 * Formats the transactions of this customer so that they are numbered starting at 1
	 * with the information about each transaction on separate lines and values tabbed over
	 *
	 * @return the transactions as they appear in the transaction list of the customer panel
	 */
	public String getTransactionList()
	{
		String result = "";
		int i = 0;
		
		//Loops through all of the transactions and adds their information to the String that will be returned
		for(Transaction t : transactions)
		{
			result += (++i) + ":\n";
			result += t.toString();
			result += "\n";
		}
		
		return result;
	}
	
	/**
	 * Formats all of the information about this customer in a way that can be formatted 
	 * in a word processor to look professional
	 *
	 * @return a string with the above formatting
	 */
	public String toString()
	{
		String result = "";
		int i = 0;
		
		//Adds the name, and address to the final String
		result += "Name:\t" + name + "\n";
		result += "Address:\t" + address + "\n";
		
		//If there is a legitimate phone number and/or email address, adds it to the result String, otherwise adds "Not Provided"
		if(!phoneNumber.equals(""))
			result += "Phone:\t" + phoneNumber + "\n";
		else
			result += "Phone:\tNot Provided\n";
		
		if(!email.equals(""))
			result += "Email:\t" + email + "\n";
		else
			result += "Email:\tNot Provided\n";
		
		//Adds column headings to mark the various fields of a transaciton
		result += "\n";
		result += "\tType:\tDate:\tAmount:\n";
		
		//Iterates through all of the transactions and adds their information to the result String
		for(Transaction t : transactions)
		{
			//Adds all of the transaction's information to the result String
			result += (++i) + ".\t";
			result += t.toString() + "\t";
			result += sdfmt.format(t.getDate().getTime()) + "\t";
			result += nfmt.format(t.getAmount()) + "\t";
		}
		
		//Appends the total amount received and pending to the end of the result String
		result += "\n";
		result += "Total Received:\t" + nfmt.format(totalReceived) + "\n";
		result += "Balance:\t"  + nfmt.format(balance) + "\n";
		
		return result;
	}
}
