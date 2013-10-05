/**
 * @ExpenseList.java
 *
 *
 * @author Jacob Zimmerman
 * @version 10.00 25 May 2012
 */

package zimmerman.jacob.moneybookv10;

import java.io.Serializable;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class ExpenseList implements Serializable
{
	private double totalExpended;
	private ArrayList<Expense> expenses;

	//Format the numbers into currency notation and dates into Strings. Example: Friday, June 11, 2010
	private NumberFormat nfmt = NumberFormat.getCurrencyInstance();
	private SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");

	public ExpenseList()
	{
		totalExpended = 0;
		expenses = new ArrayList<Expense>();
	}

	/**
	 * Adds a expense to the ArrayList part of this class
	 *
	 * @param element the Expense object to be added
	 *
	 * @return true if the element was added successfully
	 */
	public boolean add(Expense element)
	{
		boolean result = expenses.add(element);
		Collections.sort(expenses);

		totalExpended = 0;

		for(Expense t : expenses)
		{
			totalExpended += t.getAmount();
		}

		return result;
	}

	/**
	 * Remove an expense from the ArrayList part of this class
	 *
	 * @param index the index of the expense to be removed
	 *
	 * @return the Expense that was removed
	 */
	public Expense remove(int index)
	{
		Expense result = expenses.remove(index);

		totalExpended -= result.getAmount();

		return result;
	}

	/**
	 * Remove an expense from the ArrayList part of this class
	 *
	 * @param expense the expense to be removed
	 *
	 * @return the success of the removal as a boolean
	 */
	public boolean remove(Expense expense)
	{
		boolean result = expenses.remove(expense);

		totalExpended -= expense.getAmount();

		return result;
	}

	/**
	 * Get an expense from the ArrayList part of this class
	 *
	 * @param index the index of the expense to return
	 *
	 * @return a refrence to the expense at <code>index</code>
	 */
	public Expense get(int index)
	{
		return expenses.get(index);
	}

	/**
	 * Get the ArrayList that this class uses
	 *
	 * @return a reference to the expenses ArrayList
	 */
	public ArrayList<Expense> getExpenses()
	{
		return expenses;
	}

	/**
	 * Return the total expended
	 * @return the total amount expended as a double
	 */
	public double getTotalExpended()
	{
		return totalExpended;
	}

	public String getXML()
	{
		String result = "";

		result += "<expenseList>\n";

		String temp = "";
		Scanner scan;
		for(Expense t : expenses)
		{
			temp = t.getXML();
			scan = new Scanner(temp);

			while(scan.hasNext())
			{
				result += "\t" + scan.nextLine() + "\n";
			}
		}

		result += "\t<totalExpended>" + nfmt.format(totalExpended) + "</totalExpended>\n";

		result += "</expenseList>\n";

		return result;
	}

	/**
	 * Formats the expenses of this expenseList so that they are numbered starting at 1
	 * with the information about each expense on separate lines and values tabbed over
	 *
	 * @return the expenses numbered by descending dates
	 */
	public String toString()
	{
		String result = "";
		int i = 0;

		//Loops through all of the expenses and adds their information to the String that will be returned
		for(Expense t : expenses)
		{
			result += (++i) + ":\n";
			result += t.toString();
			result += "\n";
		}

		return result;
	}
}