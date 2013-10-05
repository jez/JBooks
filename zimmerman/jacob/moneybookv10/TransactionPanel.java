/**
 * @TransactionPanel.java
 *
 * Creates the transaction panel that is used when creating a new transaction from a JOptionPane.
 * Includes options for the date, type, and amount of the transaction.
 *
 * @author Jacob Zimmerman
 * @version 10.00 25 May 2012
 */

package zimmerman.jacob.moneybookv10;

import com.horstmann.corejava.GBC;

import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.FileInputStream;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * TransactionPanel defines a class that can be passed directly as a JPanel. It used in
 * this program to get information from the user through the use of a JOptionPane. Provides
 * fields to get the type, date, amount from the transaction.
 */
class TransactionPanel extends JPanel
{
	//Panels that will hold the drop down menus for the dates
	private JPanel datePanel;

	//Labels that mark the fields
	private JLabel typeLabel;
	private JLabel dateLabel;
	private JLabel amountLabel;

	//Fields that receive input
	private JComboBox<TType> typeField;
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
	 * @param transaction the transaction to be used in order to initialize the transaction panel
	 */
	public TransactionPanel(Transaction transaction)
	{
		for(int i = 1; i <= 12; i++)
			months[i-1] = new Integer(i);
		for(int i = 1; i <= 31; i++)
			days31[i-1] = new Integer(i);
		for(int i = 0; i < 10; i++)
			years[i] = new Integer(2010 + i);

		setLayout(new GridBagLayout());
		datePanel = new JPanel(new GridLayout(1,3));

		typeLabel = new JLabel("Type:");

		dateLabel = new JLabel("Date:");

		amountLabel = new JLabel("Amount:");

		typeField = new JComboBox<TType>(TType.values());
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

		amountField = new JTextField("25.0");

		if(transaction.getType() != null)
			typeField.setSelectedItem(transaction.getType());

		if(transaction.getDate() != null)
		{
			dateMonth.setSelectedItem(new Integer(transaction.getDate().get(Calendar.MONTH) + 1));
			dateDay.setSelectedItem(new Integer(transaction.getDate().get(Calendar.DATE)));
			dateYear.setSelectedItem(new Integer(transaction.getDate().get(Calendar.YEAR)));
		}

		if(transaction.getAmount() != -1)
			amountField.setText(Double.toString(transaction.getAmount()));

		//Adds the components to the panel using the GBC helper to align them to look nice
		add(typeLabel, new GBC(0,0)
			.setFill(GBC.BOTH));
		add(typeField, new GBC(1, 0)
			.setFill(GBC.BOTH));
		add(dateLabel, new GBC(0,1)
			.setFill(GBC.BOTH));
		add(datePanel, new GBC(1,1)
			.setFill(GBC.BOTH));
		add(amountLabel, new GBC(0,2)
			.setFill(GBC.BOTH));
		add(amountField, new GBC(1,2)
			.setFill(GBC.BOTH));
	}

	/**
	 * Sets up the JPanel.
	 *
	 * @param type the type of transaction to initialize the value of the type field, or null
	 * @param date the date to initialize the value of the  date field, or null
	 * @param amount the amount of money involved in the transaction, or -1
	 */
	public TransactionPanel(TType type, GregorianCalendar date, double amount)
	{
		for(int i = 1; i <= 12; i++)
			months[i-1] = new Integer(i);
		for(int i = 1; i <= 31; i++)
			days31[i-1] = new Integer(i);
		for(int i = 0; i < 10; i++)
			years[i] = new Integer(2010 + i);

		setLayout(new GridBagLayout());
		datePanel = new JPanel(new GridLayout(1,3));

		typeLabel = new JLabel("Type:");

		dateLabel = new JLabel("Date:");

		amountLabel = new JLabel("Amount:");

		typeField = new JComboBox<TType>(TType.values());
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

		amountField = new JTextField("25.0");

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

		//Adds the components to the panel using the GBC helper to align them to look nice
		add(typeLabel, new GBC(0,0)
			.setFill(GBC.BOTH));
		add(typeField, new GBC(1, 0)
			.setFill(GBC.BOTH));
		add(dateLabel, new GBC(0,1)
			.setFill(GBC.BOTH));
		add(datePanel, new GBC(1,1)
			.setFill(GBC.BOTH));
		add(amountLabel, new GBC(0,2)
			.setFill(GBC.BOTH));
		add(amountField, new GBC(1,2)
			.setFill(GBC.BOTH));
	}

	/**
	 * Gets the type of the added transaction
	 *
	 * @return the type of transaction currently showing in the typeField
	 */
	public TType getType()
	{
		return (TType) typeField.getSelectedItem();
	}

	/**
	 * Gets the date of the added transaction
	 *
	 * @return a new GregorianCalendar object initialized with the respective fields from the start date drop down menus
	 */
	public GregorianCalendar getDate()
	{
		return new GregorianCalendar(((Integer) dateYear.getSelectedItem()).intValue(),
									 dateMonth.getSelectedIndex(),
									 dateDay.getSelectedIndex() + 1);
	}

	/**
	 * Gets the amount of the added transaction makes sure that if the transaction type is a payment that the sign of the amount is
	 * negative.
	 *
	 * @return the amount of money currently showing in the moneyReceivedField
	 */
	public double getAmount()
	{
		double result = Double.parseDouble(amountField.getText());
		if(getType() == TType.PAYMENT)
			return -Math.abs(result);
		else
			return Math.abs(result);	
	}

	/**
	 * Returns the transaction represented by this panel as determined by the three getter methods
	 *
	 * @return a new transaction object as represented by this panel
	 */
	public Transaction getTransaction()
	{
		return new Transaction(getType(), getDate(), getAmount());
	}

	//A class that is used to change the money received field's value based on what is showing in the type field
	private class SetAmount implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			TType type = (TType) ((JComboBox) event.getSource()).getSelectedItem();

			double result = 0;
			
			Properties defaults = new Properties();
			try
			{
				FileInputStream in = new FileInputStream("MoneyBookProperties/prices.properties");
				defaults.load(in);
				in.close();
			}
			catch(java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			switch(type)
			{
			case GRASS_CUT:
				result += Double.parseDouble(defaults.getProperty("GRASS_CUT", "25"));
				break;
			case PAYMENT:
				result += Double.parseDouble(defaults.getProperty("PAYMENT", "25"));
				break;
			case TRIM_EDGES:
				result += Double.parseDouble(defaults.getProperty("TRIM_EDGES", "5"));
				break;	
			case SWEEP_GRASS:
				result += Double.parseDouble(defaults.getProperty("SWEEP_GRASS", "10"));
				break;
			case WATER_PLANTS:
				result += Double.parseDouble(defaults.getProperty("WATER_PLANTS", "5"));
				break;
			case SPREAD_MULCH:
				result += Double.parseDouble(defaults.getProperty("SPREAD_MULCH", "100"));
				break;
			case SPREAD_TOPSOIL:
				result += Double.parseDouble(defaults.getProperty("SPREAD_TOPSOIL", "100"));
				break;
			case SPREAD_STONE:
				result += Double.parseDouble(defaults.getProperty("SPREAD_STONE", "100"));
				break;
			case GENERAL_LABOR:
				result += Double.parseDouble(defaults.getProperty("GENERAL_LABOR", "20"));
				break;
			case WALK_DOG:
				result += Double.parseDouble(defaults.getProperty("WALK_DOG", "5"));
				break;
			case TIP:
				result += Double.parseDouble(defaults.getProperty("TIP", "5"));
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
			//Months with 31 days
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
			//Months with 30 days
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
			//February with 28 days or 29 on a leap year
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