/**
 * @Customer.java
 *
 * Last updated: 2010/8/13
 * Author: Jacob Zimmerman
 * @version 2.00 2010/6/5
 */

package zimmerman.jacob.moneybookv2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.ListIterator;

/**
 * The Customer class extends the ArrayList class so that it can keep track of a customer
 * as well as any transactions that occur between that customer. It also keeps track of an 
 * address, phone number, and email, as well as the total amount that this customer has 
 * paid and has yet to pay.
 */
public class Customer extends ArrayList<Transaction> implements Comparable
{
	//Store info about the customer
	private String name;
	private String address;
	private String phoneNumber;
	private String email;
	private double totalReceived;
	private double totalPending;
	
	//Format the numbers into currency notation and dates into Strings. Example: Friday, June 11, 2010
	private NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	private SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	
	//Other variables
	private int numTrans;
	private final static String EXT = ".cst2";
	
	/**
	 * Creates a new Customer object.
	 *
	 * @param name the name of the new customer
	 */
	public Customer(String name)
	{
		super();
		
		this.name = name;
		address = "";
		phoneNumber = "";
		email = "";
		totalReceived = 0;
		totalPending = 0;
		
		numTrans = this.size();
		write();
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
		totalPending = 0;
		
		numTrans = this.size();
		write();
	}
	
	/**
	 * Creates a new Customer object from a file
	 *
	 * @param name the name of the customer to load
	 *
	 * @return an existing Customer object that has previously been stored in a file
	 */
	public static Customer load(String name)
	{
		//Objects that read the data
		File file = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		DataInputStream dis = null;
		
		//Variables that are used to store data in temporarily between the time the data is read to when it is added to a Customer object
		String tempAddress = "";
		String tempPhoneNumber = "";
		String tempEmail = "";
		Transaction[] transactions = new Transaction[0];;
		
		try
		{
			//Initialize the data readers
			file = new File(name + EXT);
			file.createNewFile();
			
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		try
		{
			//First variable in the file says how many transactions there are
			transactions = new Transaction[dis.readInt()];
			
			//Read information about address, phoneNumber, and email
			tempAddress = dis.readUTF();
			tempPhoneNumber = dis.readUTF();
			tempEmail = dis.readUTF();
			
			//Take all information about the transactions and put it into indivitual Transaction objects
			for(int i = 0; i < transactions.length; i++)
			{
				transactions[i] = new Transaction(dis.readInt(), new GregorianCalendar(dis.readInt(), dis.readInt(), dis.readInt()), new GregorianCalendar(dis.readInt(), dis.readInt(), dis.readInt()), dis.readDouble(), dis.readDouble());
			}
			
			dis.close();
			bis.close();
			fis.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		//Create a new customer object with name, address, phoneNumber, and email
		Customer result = new Customer(name, tempAddress, tempPhoneNumber, tempEmail);
		
		//Add all transactions
		for(Transaction t : transactions)
		{
			result.add(t);
		}
		
		return result;
	}
	
	/**
	 * Write all of the data for this Customer object so that it can be retrieved later. 
	 * This method should not need to be called from a method outside of this class.
	 */
	private void write()
	{
		//Objects used to write the data
		File file = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		DataOutputStream dos = null;
		
		//Either an existing or a new file to write the data to. All previous data will be overriden
		file = new File(name + EXT);
		
		try
		{
			fos = new FileOutputStream(file, false);
			bos = new BufferedOutputStream(fos);
			dos = new DataOutputStream(bos);
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		try
		{
			//Write the number of transactions first so that it can be used in the load method
			dos.writeInt(numTrans);
			dos.flush();
			
			//Write the address, phone number, and email for the customer
			dos.writeUTF(address);
			dos.flush();
			dos.writeUTF(phoneNumber);
			dos.flush();
			dos.writeUTF(email);
			dos.flush();
			
			//Write each transaction one by one
			for(int i = 0; i < numTrans; i++)
			{
				//Write the type of transaction
				dos.writeInt(get(i).getType());
				dos.flush();
				
				//Write the start date of the transaction
				dos.writeInt(get(i).getStartDate().get(GregorianCalendar.YEAR));
				dos.flush();
				dos.writeInt(get(i).getStartDate().get(GregorianCalendar.MONTH));
				dos.flush();
				dos.writeInt(get(i).getStartDate().get(GregorianCalendar.DAY_OF_MONTH));
				dos.flush();
				
				//Write the end date of the transaction
				dos.writeInt(get(i).getEndDate().get(GregorianCalendar.YEAR));
				dos.flush();
				dos.writeInt(get(i).getEndDate().get(GregorianCalendar.MONTH));
				dos.flush();
				dos.writeInt(get(i).getEndDate().get(GregorianCalendar.DAY_OF_MONTH));
				dos.flush();
				
				//Write the amount of money received from the transaction
				dos.writeDouble(get(i).getMoneyReceived());
				dos.flush();
				
				//Write the amount of money pending for the transaction
				dos.writeDouble(get(i).getMoneyPending());
				dos.flush();
			}
			
			dos.close();
			fos.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
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
		boolean result = super.add(element);
		Collections.sort(this);
		numTrans++;
		
		totalReceived = 0;
		totalPending = 0;
		
		for(int i = 0; i < numTrans; i++)
		{
			totalReceived += get(i).getMoneyReceived();
			totalPending += get(i).getMoneyPending();
		}
		write();
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
		Transaction result = super.remove(index);
		numTrans--;
		totalReceived -= result.getMoneyReceived();
		totalPending -= result.getMoneyPending();
		write();
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
		write();
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
		write();
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
		write();
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
	public double getTotalPending()
	{
		return totalPending;
	}
	
	/**
	 * Compares this customer to another by comparing the Strings containing the names of the two customers
	 * 
	 * @param obj the object to compare this customer to
	 *
	 * @return an int value that tells whether this customer is less than (-), equal to (0), or greater than (+) another customer
	 */
	public int compareTo(Object obj)
	{
		return name.compareTo(((Customer) obj).getName());
	}
	
	/**
	 * Formats the transactions of this customer so that they are numbered starting at 1
	 * with the information about each transaction on separate lines and values tabbed over
	 *
	 * @return the transactions as they appear in the transaction list of the customer panel
	 */
	public String getTransactionList()
	{
		//Uses a list iterator to iterate through all of the transactions with a while loop
		ListIterator<Transaction> transactions = listIterator();
		String result = "";
		int i = 0;
		
		//Loops through all of the transactions and adds their information to the String that will be returned
		while(transactions.hasNext())
		{
			result += (++i) + ":\n";
			Transaction t = transactions.next();
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
		//Uses a list iterator to iterate through all of the transactions with a while loop
		ListIterator<Transaction> transactions = listIterator();
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
		result += "\tType:\tStart Date:\tEnd Date:\tMoney received:\tMoney pending:\n";
		
		//Iterates through all of the transactions and adds their information to the result String
		while(transactions.hasNext())
		{
			//The next transaction
			Transaction t = transactions.next();
			
			//Adds all of the transaction's information to the result String
			result += (++i) + ".\t";
			result += t.getTypeString() + "\t";
			result += sdfmt.format(t.getStartDate().getTime()) + "\t";
			result += sdfmt.format(t.getEndDate().getTime()) + "\t";
			result += nfmt.format(t.getMoneyReceived()) + "\t";
			result += nfmt.format(t.getMoneyPending()) + "\t\n";
		}
		
		//Appends the total amount received and pending to the end of the result String
		result += "\n";
		result += "Total Received:\t" + nfmt.format(totalReceived) + "\n";
		result += "Total Pending:\t"  + nfmt.format(totalPending) + "\n";
		
		return result;
	}
}
