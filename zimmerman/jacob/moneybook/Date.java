/**
 * @Date.java
 *
 * Last updated: 2010/6/5
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/5
 */

package zimmerman.jacob.moneybook;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Date implements Comparable
{
	private DecimalFormat fmt = new DecimalFormat("00");
	private int day;
	private int month;
	private int year;
	
	public Date(int day, int month, int year)
	{
		if(year < 1582)
			throw new IllegalArgumentException("This year is before the Gregorian calendar was invented.");
		
		if(month > 12)
			throw new IllegalArgumentException("Month number " + month + " is not a valid month.");
		
		if((month == 4 || month == 6 || month == 9 || month == 11) && day > 30)
			throw new IllegalArgumentException("Day number " + day + " is not valid in month " + month + ".");
		else if((month == 2 && ((year % 4 == 0 && year % 100 != 0) || (year % 4 == 0 && year % 400 == 0))) && day > 29)
			throw new IllegalArgumentException("Day number " + day + " is not valid in month " + month + " during year " + year + ".");
		else if(month == 2 && day > 28)
			throw new IllegalArgumentException("Day number " + day + " is not valid in month " + month + " during year " + year + ".");
		else if((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && day > 31)
			throw new IllegalArgumentException("Day number " + day + " is not valid in month " + month + ".");
		
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	public static Date parseDate(String dateStr)
	{
		Scanner scan = new Scanner(dateStr);
		scan.useDelimiter("/");
		
		System.out.println(dateStr);
		
		String temp = scan.next();
		int day = Integer.parseInt(temp);
		
		temp = scan.next();
		int month = Integer.parseInt(temp);
		
		temp = scan.next();
		int year = Integer.parseInt(temp);
		
		return new Date(day, month, year);
	}
	
	public int getMonth()
	{
		return month;
	}
	
	public int getDay()
	{
		return day;
	}
	
	public int getYear()
	{
		return year;
	}
	
	public boolean equals(Object obj)
	{
		Date date = (Date) obj;
		return (day == date.getDay() && month == date.getMonth() && year == date.getYear());
	}
	
	public int compareTo(Object obj)
	{
		Date dateObj = (Date) obj;
		
		if(year < dateObj.getYear())
			return -1;
		else if(year > dateObj.getYear())
			return 1;
		else
		{
			if(month < dateObj.getMonth())
				return -1;
			else if(month > dateObj.getMonth())
				return 1;
			else
			{
				if(day < dateObj.getDay())
					return -1;
				else if(day > dateObj.getDay())
					return 1;
				else
					return 0;
			}
		}
	}
	
	public String toString()
	{
		return fmt.format(month) + "/" + fmt.format(day) + "/" + year;
	}
}
