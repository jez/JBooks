/**
 * @Transaction.java
 *
 * Last updated: 2010/8/13
 * @author Jacob Zimmerman
 * @version 2.00 2010/6/5
 */

package zimmerman.jacob.moneybookv2;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Represents a single transaction. Holds information about type of service provided, 
 * start date of the service provided, end date of the service provided, money received
 * from the customer, and money from the customer that still has to be delivered.
 */
public class Transaction implements Comparable
{
	//Used to format doubles into currency notation and dates from numbers into strings. Example: Friday 11, 2010
	NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	
	//Variables that store main info about the transaction
	private int type;
	private String typeStr;
	private GregorianCalendar startDate;
	private GregorianCalendar endDate;
	private double moneyReceived;
	private double moneyPending;
	
	//Constant fields that represent the types of transactions as integers
	
	/**
	 * Cut the grass without weedwhacking or for summer-long customers
	 */
	public static final int GRASS_CUT = 0;
	/**
	 * Cut the grass for Dad
	 */
	public static final int GRASS_CUT_HALF_OFF = 1;
	/**
	 * Cut the grass with weedwhacking
	 */
	public static final int GRASS_CUT_W_WEEDWHACK = 2;
	public static final int PULL_WEEDS_30_MIN = 3;
	public static final int PULL_WEEDS_60_MIN = 4;
	public static final int PULL_WEEDS_90_MIN = 5;
	public static final int PULL_WEEDS_120_MIN = 6;
	public static final int SPREAD_MULCH = 7;
	public static final int TIP = 8;
	
	/**
	 * Creates a new transaction object.
	 *
	 * @param type the type of service provided as specified by the constant fields
	 * @param startDate the start date of the service
	 * @param endDate the end date of the service
	 * @param moneyReceived the amount of money received from a customer regarding the service provided
	 * @param moneyPending the amount of money the customer still has to pay
	 */
	public Transaction(int type, GregorianCalendar startDate, GregorianCalendar endDate, double moneyReceived, double moneyPending)
	{
		if(startDate == null)
			throw new IllegalArgumentException("Start date cannot be null.");
		
		this.type = type;
		switch(this.type)
		{
		case GRASS_CUT:
			typeStr = "Grass cut";
			break;
		case GRASS_CUT_HALF_OFF:
			typeStr = "Grass cut half off";
			break;
		case GRASS_CUT_W_WEEDWHACK:
			typeStr = "Grass cut with weedwhack";
			break;
		case PULL_WEEDS_30_MIN:
			typeStr = "Pull weeds 30 min";
			break;
		case PULL_WEEDS_60_MIN:
			typeStr = "Pull weeds 60 min";
			break;
		case PULL_WEEDS_90_MIN:
			typeStr = "Pull weeds 90 min";
			break;
		case PULL_WEEDS_120_MIN:
			typeStr = "Pull weeds 120 min";
			break;
		case SPREAD_MULCH:
			typeStr = "Spread mulch";
			break;
		case TIP:
			typeStr = "Tip";
		}
		this.startDate = startDate;
		if(endDate.equals(startDate))
			this.endDate = null;
		else
			this.endDate = endDate;
		this.moneyReceived = moneyReceived;
		this.moneyPending = moneyPending;
	}
	
	/**
	 * Used to change a String into an int value representing a type of service provided
	 *
	 * @param option the service provided as a string
	 *
	 * @return an int representing the type of service provided
	 */
	public static int parseType(String option)
	{
		if(option.equals("Grass cut"))
			return GRASS_CUT;
		else if(option.equals("Grass cut half off"))
			return GRASS_CUT_HALF_OFF;
		else if(option.equals("Grass cut with weedwhack"))
			return GRASS_CUT_W_WEEDWHACK;
		else if(option.equals("Pull weeds 30 min"))
			return PULL_WEEDS_30_MIN;
		else if(option.equals("Pull weeds 60 min"))
			return PULL_WEEDS_60_MIN;
		else if(option.equals("Pull weeds 90 min"))
			return PULL_WEEDS_90_MIN;
		else if(option.equals("Pull weeds 120 min"))
			return PULL_WEEDS_120_MIN;
		else if(option.equals("Spread mulch"))
			return SPREAD_MULCH;
		else if(option.equals("Tip"))
			return TIP;
		else
			return -1;
	}
	
	/**
	 * @return the type of service provided
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * @return the type of service provided as a String
	 */
	public String getTypeString()
	{
		return typeStr;
	}
	
	/**
	 * @return the start date
	 */
	public GregorianCalendar getStartDate()
	{
		return startDate;
	}
	
	
	/**
	 * @return the end date, unless the end date is the same as the start date, in which case it returns the start date
	 */
	public GregorianCalendar getEndDate()
	{
		if(endDate == null)
			return startDate;
		else
			return endDate;
	}
	
	/**
	 * @return the amount of money received
	 */
	public double getMoneyReceived()
	{
		return moneyReceived;
	}
	
	/**
	 * @return the amount of money pending
	 */
	public double getMoneyPending()
	{
		return moneyPending;
	}
	
	/**
	 * Compares one transaction to another for sorting. First compares start dates.
	 * A sooner start date returns a negative number. If the start dates are the same, end
	 * dates are compared. If both end dates are not null and not the same, returns the
	 * sooner of the two. Otherwise, returns a lower value if this transaction has an end 
	 * date and the other doesn't. If both are null, returns which has a lower value  type 
	 * as determined by the value of the int field that represents that type. if they are 
	 * equals returns a value by comparing first money received, then money pending. If
	 * all of these fields are equal, returns 0.
	 *
	 * @param obj the object to compare to.
	 *
	 * @return an int to determining whether this Transaction is less than, equal to or greater than another object
	 */
	public int compareTo(Object obj)
	{
		Transaction transObj = (Transaction) obj;
		
		if(startDate.compareTo(transObj.getStartDate()) < 0)
			return -1;
		else if(startDate.compareTo(transObj.getStartDate()) > 0)
			return 1;
		else
		{
			if(endDate != null && !transObj.getEndDate().equals(transObj.getStartDate()) && endDate.compareTo(transObj.getEndDate()) != 0)
				return endDate.compareTo(transObj.getEndDate());
			else if(endDate != null && transObj.getEndDate().equals(transObj.getStartDate()))
				return -1;
			else if(endDate == null && !transObj.getEndDate().equals(transObj.getStartDate()))
				return 1;
			else
			{
				if(type < transObj.getType())
					return -1;
				else if(type > transObj.getType())
					return 1;
				else
				{
					if(moneyReceived < transObj.getMoneyReceived())
						return -1;
					else if(moneyReceived > transObj.getMoneyReceived())
						return 1;
					else
						return 0;
				}
			}
		}
	}
	
	/**
	 * @return a String representation of this Transaction
	 */
	public String toString()
	{
		String result = "";
		result += "Type:    \t"   + typeStr + "\n";
		result += "Start Date:\t" + sdfmt.format(startDate.getTime()) + "\n";
		try
		{
		result += "End Date:\t"   + sdfmt.format(endDate.getTime()) + "\n";
		}
		catch(NullPointerException npe)
		{
		result += "End Date:\t\n";
		}
		result += "Received:\t"   + nfmt.format(moneyReceived) + "\n";
		result += "Pending:\t"    + nfmt.format(moneyPending) + "\n";
		
		return result;
	}
}