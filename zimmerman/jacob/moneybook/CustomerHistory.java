/**
 * @CustomerHistory.java
 *
 * Last updated: 2010/6/5
 * Author: Jacob Zimmerman
 * @version 1.00 2010/6/5
 */

package zimmerman.jacob.moneybook;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ListIterator;

/**
 * CustomerHistory class extends the ArrayList class so that it can keep track of a customer
 * as well as any transactions that occur between that customer.
 */
public class CustomerHistory extends ArrayList<Transaction> implements Comparable
{
	private String customer;
	private int numTrans;
	private final static String EXT = ".cst";
	private double totalReceived;
	private double totalPending;
	private NumberFormat fmt;
	
	/**
	 * Creates a new CustomerHistory object.
	 */
	public CustomerHistory(String customer)
	{
		super();
		this.customer = customer;
		numTrans = this.size();
		write();
		totalReceived = 0;
		totalPending = 0;
		fmt = NumberFormat.getCurrencyInstance();
	}
	
	/**
	 * Creates a new CustomerHistory object from a file
	 */
	public static CustomerHistory load(String customer)
	{
		File file = null;
		FileInputStream fis = null;
		DataInputStream dis = null;
		Transaction[] transactions;
		
		try
		{
			file = new File(customer + EXT);
			file.createNewFile();
			
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		try
		{
			transactions = new Transaction[dis.readInt()];
			
			for(int i = 0; i < transactions.length; i++)
			{
				transactions[i] = new Transaction(new Date(dis.readInt(), dis.readInt(), dis.readInt()), new Date(dis.readInt(), dis.readInt(), dis.readInt()), dis.readUTF(), dis.readDouble(), dis.readDouble());
			}
			
			dis.close();
			fis.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
			transactions = new Transaction[0];
		}
		
		CustomerHistory result = new CustomerHistory(customer);
		
		for(Transaction t : transactions)
		{
			result.add(t);
		}
		
		return result;
	}
	
	private void write()
	{
		File file = null;
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		
		file = new File(customer + EXT);
		
		try
		{
			fos = new FileOutputStream(file, false);
			dos = new DataOutputStream(fos);
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		try
		{
			dos.writeInt(numTrans);
			dos.flush();
			
			for(int i = 0; i < numTrans; i++)
			{
				dos.writeInt(get(i).getStartDate().getYear());
				dos.flush();
				dos.writeInt(get(i).getStartDate().getMonth());
				dos.flush();
				dos.writeInt(get(i).getStartDate().getDay());
				dos.flush();
				
				dos.writeInt(get(i).getEndDate().getYear());
				dos.flush();
				dos.writeInt(get(i).getEndDate().getMonth());
				dos.flush();
				dos.writeInt(get(i).getEndDate().getDay());
				dos.flush();
				
				dos.writeUTF(get(i).getMessage());
				dos.flush();
				
				dos.writeDouble(get(i).getMoneyReceived());
				dos.flush();
				
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
	
	public void add(int index, Transaction element)
	{
		add(element);
	}
	
	public Transaction remove(int index)
	{
		Transaction result = super.remove(--index);
		numTrans--;
		totalReceived -= result.getMoneyReceived();
		totalPending -= result.getMoneyPending();
		write();
		return result;
	}
	
	public void removeAllTransactions()
	{
		ListIterator<Transaction> transactions = listIterator();
	}
	
	public String getCustomer()
	{
		return customer;
	}
	
	public double getTotalReceived()
	{
		return totalReceived;
	}
	
	public double getTotalPending()
	{
		return totalPending;
	}
	
	public int compareTo(Object obj)
	{
		return customer.compareTo(((CustomerHistory) obj).getCustomer());
	}
	
	public String toString()
	{
		ListIterator<Transaction> transactions = listIterator();
		String result = "";
		int i = 0;
		
		result += customer + "\n";
		while(transactions.hasNext())
		{
			result += "\t" + (++i) + ":\n";
			Transaction t = transactions.next();
			result += t.toString();
			result += "\n";
		}
		
		result += "Total received: " + fmt.format(totalReceived) + "\n";
		result += "Total pending:  " + fmt.format(totalPending);
		
		return result;
	}
}
