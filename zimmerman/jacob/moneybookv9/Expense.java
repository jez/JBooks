/**
 * @Expense.java
 *
 * Defines a particular expense, with a specified type, date, and amount
 *
 * @author Jacob Zimmerman
 * @version 9.00 3 Apr 2012
 */

package zimmerman.jacob.moneybookv9;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Represents a single expense. Holds information about type of service provided,
 * start date of the service provided, end date of the service provided, money received
 * from the customer, and money from the customer that still has to be delivered.
 */
public class Expense implements Comparable<Expense>
{
	//Used to format doubles into currency notation and dates from numbers into strings. Example: Friday 11, 2010
	NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");

	//Variables that store main info about the transaction
	private EType type;
	private String name;
	private GregorianCalendar date;
	private double amount;

	/**
	 * Creates a new expense object.
	 *
	 * @param type the type of service provided as specified by the constant fields
	 * @param date the start date of the service
	 * @param amount the amount of money associated with the service provided
	 */
	public Expense(EType type, GregorianCalendar date, double amount)
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
	public EType getType()
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
	 * @return the amount of money this transaction exchanged
	 */
	public double getAmount()
	{
		return amount;
	}

	/**
	 * Compares one expense to another for sorting. First compares dates.
	 * A sooner date returns a negative number. If both are equal, returns which has a
	 * lower value type determined by the compareTo method of the enum type. If they are
	 * equals returns a value by the amount variable. If all of these fields are equal, returns 0.
	 *
	 * @param expenseObj the expense object to compare to.
	 *
	 * @return an int to determining whether this Transaction is less than, equal to or greater than another object
	 */
	public int compareTo(Expense expenseObj)
	{
		//Compare dates (chronologically)
		if(date.compareTo(expenseObj.getDate()) < 0)
			return -1;
		else if(date.compareTo(expenseObj.getDate()) > 0)
			return 1;
		//Compare types (as defined in the enum EType
		else
		{
			if(type.compareTo(expenseObj.getType()) < 0)
				return -1;
			else if(type.compareTo(expenseObj.getType()) > 0)
				return 1;
			//Compare amounts (bigger first)
			else
			{
				if(amount < expenseObj.getAmount())
					return -1;
				else if(amount > expenseObj.getAmount())
					return 1;
				else
					return 0;
			}
		}
	}

	/**
	 * Creates the xml data for this expense object, to be written into an xml file
	 *
	 * @return the data created for this expense object
	 */
	public String getXML()
	{
		String result = "";

		result += "<expense>\n";
		result += "\t<type>" + type.toString() + "</type>\n";
		result += "\t<date>" + sdfmt.format(date.getTime()) + "</date>\n";
		result += "\t<amount>" + nfmt.format(amount) + "</amount>\n";
		result += "</expense>\n";

		return result;
	}

	/**
	 * @return a String representation of this Expense
	 */
	public String toString()
	{
		String result = "";
		result += "Type:    \t" + type.toString() + "\n";
		result += "Date:\t" + sdfmt.format(date.getTime()) + "\n";
		result += "Amount:\t" + nfmt.format(amount);

		return result;
	}
}