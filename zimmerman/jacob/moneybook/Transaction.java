/**
 * @Transaction.java
 *
 * Last updated: 2010/6/5
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/5
 */

package zimmerman.jacob.moneybook;

import java.text.NumberFormat;

public class Transaction implements Comparable
{
	NumberFormat fmt = NumberFormat.getCurrencyInstance();
	private Date startDate;
	private Date endDate;
	private String message;
	private double moneyReceived;
	private double moneyPending;
	
	public Transaction(Date startDate, Date endDate, String message, double moneyReceived, double moneyPending)
	{
		if(startDate == null)
			throw new IllegalArgumentException("Start date cannot be null.");
		
		this.startDate = startDate;
		if(endDate.equals(startDate))
			this.endDate = null;
		else
			this.endDate = endDate;
		this.message = message;
		this.moneyReceived = moneyReceived;
		this.moneyPending = moneyPending;
	}
	
	public Date getStartDate()
	{
		return startDate;
	}
	
	public Date getEndDate()
	{
		if(endDate == null)
			return startDate;
		else
			return endDate;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public double getMoneyReceived()
	{
		return moneyReceived;
	}
	
	public double getMoneyPending()
	{
		return moneyPending;
	}
	
	public int compareTo(Object obj)
	{
		Transaction transObj = (Transaction) obj;
		
		if(startDate.compareTo(transObj.getStartDate()) < 0)
			return -1;
		else if(startDate.compareTo(transObj.getStartDate()) > 0)
			return 1;
		else
		{
			if(endDate != null && transObj.getEndDate() != null && endDate.compareTo(transObj.getEndDate()) != 0)
				return endDate.compareTo(transObj.getEndDate());
			else if(endDate == null && !transObj.getEndDate().equals(transObj.getStartDate()))
				return -1;
			else if(endDate != null && transObj.getEndDate().equals(transObj.getStartDate()))
				return 1;
			else if(message.compareTo(transObj.getMessage()) != 0)
				return message.compareTo(transObj.getMessage());
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
	
	public String toString()
	{
		String result = "";
		result += "\tStart Date: " + startDate + "\n";
		result += "\tEnd Date:   " + endDate + "\n";
		result += "\tMessage:    " + message + "\n";
		result += "\tReceived:   " + fmt.format(moneyReceived) + "\n";
		result += "\tPending:    " + fmt.format(moneyPending) + "\n";
		
		return result;
	}
}