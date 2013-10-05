/**
 * @TransactionPanel.java
 *
 * Last updated: 2010/8/22
 * @author Jacob Zimmerman
 * @version 1.00 2010/8/22
 */

package zimmerman.jacob.moneybookv3;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * TransactionPanel defines a class that can be passed directly as a JPanel. It used in
 * this program to get information from the user through the use of a JOptionPane. Provides
 * fields to get the type, start date, end date, money received, and money pending from
 * the transaction.
 */
class TransactionPanel extends JPanel
{
	//Panels that will hold the drop down menus for the dates
	private JPanel startDatePanel;
	private JPanel endDatePanel;
	
	//Labels that mark the fields
	private JLabel typeLabel;
	private JLabel startDateLabel;
	private JLabel endDateLabel;
	private JLabel moneyReceivedLabel;
	private JLabel moneyPendingLabel;
	
	//Fields that receive input
	private JComboBox typeField;
	private JComboBox startDateMonth;
	private JComboBox startDateDay;
	private JComboBox startDateYear;
	private JComboBox endDateMonth;
	private JComboBox endDateDay;
	private JComboBox endDateYear;
	private JTextField moneyReceivedField;
	private JTextField moneyPendingField;
	
	//Object[]s used to initialize the drop down menus
	String[] types = {"Grass cut", "Grass cut half off", "Grass cut with weedwhack", "Pull weeds 30 min", "Pull weeds 60 min", "Pull weeds 90 min", "Pull weeds 120 min", "Spread mulch", "Tip"};
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
	public TransactionPanel(int type, GregorianCalendar startDate, GregorianCalendar endDate, double moneyReceived, double moneyPending)
	{
		for(int i = 1; i <= 12; i++)
			months[i-1] = new Integer(i);
		for(int i = 1; i <= 31; i++)
			days31[i-1] = new Integer(i);
		for(int i = 0; i < 10; i++)
			years[i] = new Integer(2010 + i);
			
		setLayout(new GridLayout(5, 2));
		startDatePanel = new JPanel(new GridLayout(1, 3));
		endDatePanel = new JPanel(new GridLayout(1, 3));
		
		typeLabel = new JLabel("Type:");
		startDateLabel = new JLabel("Start date:");
		endDateLabel = new JLabel("End date:");
		moneyReceivedLabel = new JLabel("Money received:");
		moneyPendingLabel = new JLabel("Money pending:");
		
		typeField = new JComboBox(types);
		typeField.addActionListener(new SetMoneyReceived());
		
		//temp is initialized to hold today's date at runtime
		GregorianCalendar temp = new GregorianCalendar();
		startDateMonth = new JComboBox(months);
		startDateMonth.setSelectedItem(new Integer(temp.get(Calendar.MONTH) + 1));
		startDateMonth.addActionListener(new SetDaysInMonth());
		startDatePanel.add(startDateMonth);
		startDateDay = new JComboBox(days31);
		startDateDay.setSelectedItem(new Integer(temp.get(Calendar.DATE)));
		startDatePanel.add(startDateDay);
		startDateYear = new JComboBox(years);
		startDateYear.setSelectedItem(new Integer(temp.get(Calendar.YEAR)));
		startDateYear.addActionListener(new SetDaysInMonth());
		startDatePanel.add(startDateYear);
		
		endDateMonth = new JComboBox(months);
		endDateMonth.setSelectedItem(new Integer(temp.get(Calendar.MONTH) + 1));
		endDateMonth.addActionListener(new SetDaysInMonth());
		endDatePanel.add(endDateMonth);
		endDateDay = new JComboBox(days31);
		endDateDay.setSelectedItem(new Integer(temp.get(Calendar.DATE)));
		endDatePanel.add(endDateDay);
		endDateYear = new JComboBox(years);
		endDateYear.setSelectedItem(new Integer(temp.get(Calendar.YEAR)));
		endDateYear.addActionListener(new SetDaysInMonth());
		endDatePanel.add(endDateYear);
		
		moneyReceivedField = new JTextField("20.0");
		moneyPendingField = new JTextField("0.0");
		
		if(type != -1)
			typeField.setSelectedIndex(type);
		
		if(startDate != null)
		{
			startDateMonth.setSelectedItem(new Integer(startDate.get(Calendar.MONTH) + 1));
			startDateDay.setSelectedItem(new Integer(startDate.get(Calendar.DATE)));
			startDateYear.setSelectedItem(new Integer(startDate.get(Calendar.YEAR)));
		}
		
		if(endDate != null)
		{
			endDateMonth.setSelectedItem(new Integer(endDate.get(Calendar.MONTH) + 1));
			endDateDay.setSelectedItem(new Integer(endDate.get(Calendar.DATE)));
			endDateYear.setSelectedItem(new Integer(endDate.get(Calendar.YEAR)));
		}
		if(moneyReceived != -1)
			moneyReceivedField.setText(Double.toString(moneyReceived));
		if(moneyPending != -1)
			moneyPendingField.setText(Double.toString(moneyPending));
		
		add(typeLabel);
		add(typeField);
		add(startDateLabel);
		add(startDatePanel);
		add(endDateLabel);
		add(endDatePanel);
		add(moneyReceivedLabel);
		add(moneyReceivedField);
		add(moneyPendingLabel);
		add(moneyPendingField);
	}
	
	/**
	 * @return the type of transaction currently showing in the typeField
	 */
	public int getType()
	{
		return typeField.getSelectedIndex();
	}
	
	/**
	 * @return a new GregorianCalendar object initialized with the respective fields from the start date drop down menus
	 */
	public GregorianCalendar getStartDate()
	{
		return new GregorianCalendar(((Integer) startDateYear.getSelectedItem()).intValue(), startDateMonth.getSelectedIndex(), startDateDay.getSelectedIndex() + 1);
	}
	
	/**
	 * @return a new GregorianCalendar object initialized with the respective fields from the end date drop down menus
	 */
	public GregorianCalendar getEndDate()
	{
		return new GregorianCalendar(((Integer) endDateYear.getSelectedItem()).intValue(), endDateMonth.getSelectedIndex(), endDateDay.getSelectedIndex() + 1);
	}
	
	/**
	 * @return the amount of money currently showing in the moneyReceivedField
	 */
	public double getMoneyReceived()
	{
		return Double.parseDouble(moneyReceivedField.getText());
	}
	
	/**
	 * @return the amount of money currently showing in the moneyPendingField
	 */
	public double getMoneyPending()
	{
		return Double.parseDouble(moneyPendingField.getText());
	}
	
	//A class that is used to change the money received field's value based on what is showing in the type field
	private class SetMoneyReceived implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String option = (String) ((JComboBox) event.getSource()).getSelectedItem();
			int type = Transaction.parseType(option);
			
			double result = 0;
			
			switch(type)
			{
			case Transaction.GRASS_CUT_W_WEEDWHACK:
				result += 5;
			case Transaction.GRASS_CUT:
				result += 10;
			case Transaction.GRASS_CUT_HALF_OFF:
			case Transaction.PULL_WEEDS_120_MIN:
				result += 2.5;
			case Transaction.PULL_WEEDS_90_MIN:
				result += 2.5;
			case Transaction.PULL_WEEDS_60_MIN:
				result += 2.5;
			case Transaction.PULL_WEEDS_30_MIN:
				result += 2.5;
			}
			
			moneyReceivedField.setText(Double.toString(result));
		}
	}
	
	//Sets the number of options for days in the month based on the currently showing month and year for both the start and end dates
	private class SetDaysInMonth implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			int startMonth = ((Integer) startDateMonth.getSelectedItem()).intValue();
			int endMonth = ((Integer) endDateMonth.getSelectedItem()).intValue();
			
			//Adds or removes numbers in the start date day field
			switch(startMonth)
			{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if(startDateDay.getItemCount() == 28)
				{
					startDateDay.addItem(new Integer(29));
					startDateDay.addItem(new Integer(30));
					startDateDay.addItem(new Integer(31));
				}
				else if(startDateDay.getItemCount() == 29)
				{
					startDateDay.addItem(new Integer(30));
					startDateDay.addItem(new Integer(31));
				}
				else if(startDateDay.getItemCount() == 30)
				{
					startDateDay.addItem(new Integer(31));
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				if(startDateDay.getItemCount() == 28)
				{
					startDateDay.addItem(new Integer(29));
					startDateDay.addItem(new Integer(30));
				}
				else if(startDateDay.getItemCount() == 29)
				{
					startDateDay.addItem(new Integer(30));
				}
				else if(startDateDay.getItemCount() == 31)
				{
					startDateDay.removeItemAt(30);
				}
				break;
			case 2:
				GregorianCalendar calendar = new GregorianCalendar();
				int currentYear = ((Integer) startDateYear.getSelectedItem()).intValue();
				
				if(startDateDay.getItemCount() == 28 && calendar.isLeapYear(currentYear))
				{
					startDateDay.addItem(new Integer(29));
				}
				if(startDateDay.getItemCount() == 30 && !calendar.isLeapYear(currentYear))
				{
					startDateDay.removeItemAt(29);
					startDateDay.removeItemAt(28);
				}
				else if(startDateDay.getItemCount() == 30 && calendar.isLeapYear(currentYear))
				{
					startDateDay.removeItemAt(29);
				}
				else if(startDateDay.getItemCount() == 31 && !calendar.isLeapYear(currentYear))
				{
					startDateDay.removeItemAt(30);
					startDateDay.removeItemAt(29);
					startDateDay.removeItemAt(28);
				}
				else if(startDateDay.getItemCount() == 31 && calendar.isLeapYear(currentYear))
				{
					startDateDay.removeItemAt(30);
					startDateDay.removeItemAt(29);
				}
				break;
			}
			
			//Adds or removes numbers in the end date day field
			switch(endMonth)
			{
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				if(endDateDay.getItemCount() == 28)
				{
					endDateDay.addItem(new Integer(29));
					endDateDay.addItem(new Integer(30));
					endDateDay.addItem(new Integer(31));
				}
				else if(endDateDay.getItemCount() == 30)
				{
					endDateDay.addItem(new Integer(31));
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				if(endDateDay.getItemCount() == 28)
				{
					endDateDay.addItem(new Integer(29));
					endDateDay.addItem(new Integer(30));
				}
				else if(endDateDay.getItemCount() == 31)
				{
					endDateDay.removeItemAt(30);
				}
				break;
			case 2:
				GregorianCalendar calendar = new GregorianCalendar();
				int currentYear = ((Integer) endDateYear.getSelectedItem()).intValue();
				
				if(endDateDay.getItemCount() == 28 && calendar.isLeapYear(currentYear))
				{
					endDateDay.addItem(new Integer(29));
				}
				else if(endDateDay.getItemCount() == 30 && !calendar.isLeapYear(currentYear))
				{
					endDateDay.removeItemAt(29);
					endDateDay.removeItemAt(28);
				}
				else if(endDateDay.getItemCount() == 30 && calendar.isLeapYear(currentYear))
				{
					endDateDay.removeItemAt(29);
				}
				else if(endDateDay.getItemCount() == 31 && !calendar.isLeapYear(currentYear))
				{
					endDateDay.removeItemAt(30);
					endDateDay.removeItemAt(29);
					endDateDay.removeItemAt(28);
				}
				else if(endDateDay.getItemCount() == 31 && calendar.isLeapYear(currentYear))
				{
					endDateDay.removeItemAt(30);
					endDateDay.removeItemAt(29);
				}
				break;
			}
		}
	}
}