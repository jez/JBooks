/**
 * @ExpensePanel.java
 *
 *
 * @author Jacob Zimmerman
 * @version 7.00 2011/7/06
 */

package zimmerman.jacob.moneybookv7;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * ExpensePanel defines a class that can be passed directly as a JPanel. It used in
 * this program to get information from the user through the use of a JOptionPane. Provides
 * fields to get the type, date, amount from the expense transaction.
 */
class ExpensePanel extends JPanel
{
	//Panels that will hold the drop down menus for the dates
	private JPanel datePanel;

	//Labels that mark the fields
	private JLabel typeLabel;
	private JLabel dateLabel;
	private JLabel amountLabel;

	//Fields that receive input
	private JComboBox<EType> typeField;
	private JComboBox<Integer> dateMonth;
	private JComboBox<Integer> dateDay;
	private JComboBox<Integer> dateYear;
	private JTextField amountField;

	//Object[]s used to initialize the drop down menus
	Integer[] months = new Integer[12];
	Integer[] days31 = new Integer[31];
	Integer[] years = new Integer[10];

	/**
	 * Sets up the JPanel.
	 *
	 * @param type the type of transaction to initialize the value of the type field, or -1
	 * @param startDate the startDate to initialize the value of the start date field, or null
	 * @param endDate the endDate to initialize the value of the end date field, or null
	 * @param moneyReceived the amount of money recieved from the transaction, or -1
	 * @param moneyPending the amount of money pending for the transaction, or -1
	 */
	public ExpensePanel(EType type, GregorianCalendar date, double amount)
	{
		for(int i = 1; i <= 12; i++)
			months[i-1] = new Integer(i);
		for(int i = 1; i <= 31; i++)
			days31[i-1] = new Integer(i);
		for(int i = 0; i < 10; i++)
			years[i] = new Integer(2010 + i);

		setLayout(new GridLayout(3, 2));
		datePanel = new JPanel(new GridLayout(1, 3));

		typeLabel = new JLabel("Type:");
		dateLabel = new JLabel("Date:");
		amountLabel = new JLabel("Amount:");

		typeField = new JComboBox<EType>(EType.values());
		typeField.addActionListener(new SetAmount());

		//temp is initialized to hold today's date at runtime
		GregorianCalendar temp = new GregorianCalendar();
		dateMonth = new JComboBox<Integer>(months);
		dateMonth.setSelectedItem(new Integer(temp.get(Calendar.MONTH) + 1));
		dateMonth.addActionListener(new SetDaysInMonth());
		datePanel.add(dateMonth);
		dateDay = new JComboBox<Integer>(days31);
		dateDay.setSelectedItem(new Integer(temp.get(Calendar.DATE)));
		datePanel.add(dateDay);
		dateYear = new JComboBox<Integer>(years);
		dateYear.setSelectedItem(new Integer(temp.get(Calendar.YEAR)));
		dateYear.addActionListener(new SetDaysInMonth());
		datePanel.add(dateYear);

		amountField = new JTextField("40");

		if(type != null)
			typeField.setSelectedItem(type);

		if(date != null)
		{
			dateMonth.setSelectedItem(new Integer(date.get(Calendar.MONTH) + 1));
			dateDay.setSelectedItem(new Integer(date.get(Calendar.DATE)));
			dateYear.setSelectedItem(new Integer(date.get(Calendar.YEAR)));
		}

		if(amount != -1)
			amountField.setText(Double.toString(amount));

		add(typeLabel);
		add(typeField);
		add(dateLabel);
		add(datePanel);
		add(amountLabel);
		add(amountField);
	}

	/**
	 * @return the type of transaction currently showing in the typeField
	 */
	public EType getType()
	{
		return (EType) typeField.getSelectedItem();
	}

	/**
	 * @return a new GregorianCalendar object initialized with the respective fields from the start date drop down menus
	 */
	public GregorianCalendar getDate()
	{
		return new GregorianCalendar(((Integer) dateYear.getSelectedItem()).intValue(), dateMonth.getSelectedIndex(), dateDay.getSelectedIndex() + 1);
	}

	/**
	 * @return the amount of money currently showing in the moneyReceivedField
	 */
	public double getAmount()
	{
		return Double.parseDouble(amountField.getText());
	}

	//A class that is used to change the money received field's value based on what is showing in the type field
	private class SetAmount implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			EType type = (EType) ((JComboBox) event.getSource()).getSelectedItem();

			double result = 0;

			switch(type)
			{
			case GAS:
				result += 40;
				break;
			}

			amountField.setText(Double.toString(result));
		}
	}

	//Sets the number of options for days in the month based on the currently showing month and year for both the start and end dates
	private class SetDaysInMonth implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			int month = ((Integer) dateMonth.getSelectedItem()).intValue();

			//Adds or removes numbers in the start date day field
			switch(month)
			{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if(dateDay.getItemCount() == 28)
				{
					dateDay.addItem(new Integer(29));
					dateDay.addItem(new Integer(30));
					dateDay.addItem(new Integer(31));
				}
				else if(dateDay.getItemCount() == 29)
				{
					dateDay.addItem(new Integer(30));
					dateDay.addItem(new Integer(31));
				}
				else if(dateDay.getItemCount() == 30)
				{
					dateDay.addItem(new Integer(31));
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				if(dateDay.getItemCount() == 28)
				{
					dateDay.addItem(new Integer(29));
					dateDay.addItem(new Integer(30));
				}
				else if(dateDay.getItemCount() == 29)
				{
					dateDay.addItem(new Integer(30));
				}
				else if(dateDay.getItemCount() == 31)
				{
					dateDay.removeItemAt(30);
				}
				break;
			case 2:
				GregorianCalendar calendar = new GregorianCalendar();
				int currentYear = ((Integer) dateYear.getSelectedItem()).intValue();

				if(dateDay.getItemCount() == 28 && calendar.isLeapYear(currentYear))
				{
					dateDay.addItem(new Integer(29));
				}
				if(dateDay.getItemCount() == 30 && !calendar.isLeapYear(currentYear))
				{
					dateDay.removeItemAt(29);
					dateDay.removeItemAt(28);
				}
				else if(dateDay.getItemCount() == 30 && calendar.isLeapYear(currentYear))
				{
					dateDay.removeItemAt(29);
				}
				else if(dateDay.getItemCount() == 31 && !calendar.isLeapYear(currentYear))
				{
					dateDay.removeItemAt(30);
					dateDay.removeItemAt(29);
					dateDay.removeItemAt(28);
				}
				else if(dateDay.getItemCount() == 31 && calendar.isLeapYear(currentYear))
				{
					dateDay.removeItemAt(30);
					dateDay.removeItemAt(29);
				}
				break;
			}
		}
	}
}