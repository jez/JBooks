/**
 * @Transaction.java
 *
 *
 * @author Jacob Zimmerman
 * @version 7.00 2011/7/06
 */

package zimmerman.jacob.moneybookv7;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Represents a single transaction. Holds information about type of service provided,
 * start date of the service provided, end date of the service provided, money received
 * from the customer, and money from the customer that still has to be delivered.
 */
public class Transaction implements Comparable<Transaction>, Serializable
{
	//Used to format doubles into currency notation and dates from numbers into strings. Example: Friday 11, 2010
	NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");

	//Variables that store main info about the transaction
	private TType type;
	private String name;
	private GregorianCalendar date;
	private double amount;

	/**
	 * Creates a new transaction object.
	 *
	 * @param type the type of service provided as specified by the constant fields
	 * @param date the date the transaction occured
	 * @param amount the amount exchanged through this transaction
	 */
	public Transaction(TType type, GregorianCalendar date, double amount)
	{
		if(date == null)
			throw new IllegalArgumentException("Date cannot be null.");

		this.type = type;
		this.name = type.toString();
		this.date = date;
		this.amount = amount;
	}

	/**
	 * @return the type of service provided
	 */
	public TType getType()
	{
		return type;
	}

	/**
	 * @return the date
	 */
	public GregorianCalendar getDate()
	{
		return date;
	}

	/**
	 * @return the amount of money exchanged
	 */
	public double getAmount()
	{
		return amount;
	}

	/**
	 * Takes a string and turns it into a GregorianCalendar object for the sake of parsing data from xml files.
	 *
	 * @param date the string to be parsed
	 */
	public static GregorianCalendar parse(String date)
	{
		int month = -1;
		int dayOfMonth = -1;
		int year;
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		String[] daysOfMonth = new String[31];
		for(int i = 1; i <= 31; i++)
		{
			daysOfMonth[i-1] = " " + i + ",";
		}

		int i = 0;
		while(month == -1 && i < 12)
		{
			if(date.indexOf(months[i]) != -1)
			{
				month = i;
			}

			i++;
		}

		i = 0;
		while(dayOfMonth == -1 && i < 31)
		{
			if(date.indexOf(daysOfMonth[i]) != -1)
			{
				dayOfMonth = i+1;
			}

			i++;
		}

		year = Integer.parseInt(date.substring(date.length()-4));

		return new GregorianCalendar(year, month, dayOfMonth);
	}

	/**
	 * Compares one transaction to another for sorting. First compares dates.
	 * A sooner date returns a negative number. If the dates are the same returns which has a lower value type
	 * as determined by the order of the enum values in Enum TType. If they are the same, returns
	 * a value by comparing the amound exchanged. If all are equal, returns 0.
	 *
	 * @param transObj the Transaction object to compare to.
	 *
	 * @return an int to determining whether this Transaction is less than, equal to or greater than another object
	 */
	public int compareTo(Transaction transObj)
	{
		if(date.compareTo(transObj.getDate()) < 0)
			return -1;
		else if(date.compareTo(transObj.getDate()) > 0)
			return 1;
		else
		{
			if(type.compareTo(transObj.getType()) < 0)
				return -1;
			else if(type.compareTo(transObj.getType()) > 0)
				return 1;
			else
			{
				if(amount < transObj.getAmount())
					return -1;
				else if(amount > transObj.getAmount())
					return 1;
				else
					return 0;
			}
		}
	}

	/**
	 * Creates the xml data for this transaction object, to be written into an xml file
	 *
	 * @return the data created for this expense object
	 */
	public String getXML()
	{
		String result = "";

		result += "<transaction>\n";
		result += "\t<type>" + type.toString() + "</type>\n";
		result += "\t<date>" + sdfmt.format(date.getTime()) + "</date>\n";
		result += "\t<amount>" + nfmt.format(amount) + "</amount>\n";
		result += "</transaction>\n";

		return result;
	}

	/**
	 * @return a String representation of this Transaction
	 */
	public String toString()
	{
		String result = "";
		result += "Type:    \t" + type.toString() + "\n";
		result += "Date:\t" + sdfmt.format(date.getTime()) + "\n";
		result += "Amount:\t" + nfmt.format(amount) + "\n";

		return result;
	}
}