/**
 * @MoneyBook.java
 *
 * Last updated: 2010/8/13
 * @author Jacob Zimmerman
 * @version 2.00 2010/6/9
 */

package zimmerman.jacob.moneybookv2;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * Shows a frame that a user can use to store information about customers and 
 * their transactions. 
 */
public class MoneyBook
{
	//The frame for the application, the two main panels that make up that frame, and a variable representing the current customer
	private JFrame frame;
	private CustomersPanel customersPanel;
	private CustomerPanel customerPanel;
	private Customer currentCustomer;
	
	public MoneyBook()
	{
		//Sets up the size, title, and visibility of the frame
		frame = new JFrame("MoneyBook");
		frame.setLayout(new GridLayout(1, 2, 10, 0));
		
		//Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		
		customerPanel = new CustomerPanel();
		customersPanel = new CustomersPanel(frame, customerPanel);
		
		//Adds the panel with the customer list and the panel that has information about a specific customer to the frame
		frame.add(customersPanel.getPanel());
		frame.add(customerPanel.getPanel());
		
		//Sets the icon image in the top left corner
		frame.setIconImage(new ImageIcon("images/Money Book.gif").getImage());
		frame.setLocation(100, 50);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowFocusListener(new Refresh());
	}
	
	/**
	 * Creates and maintains a panel that has a button to add a customer to the list of 
	 * customers, a label that says all of the money earned and pending, and a list
	 * of customers
	 */
	public class CustomersPanel
	{
		//Used to pass a value to any dialog boxes
		private JFrame parentFrame;
		
		//Used to refresh the panel when a new customer is picked from the list of customers
		private CustomerPanel customerPanel;
		
		//Used to make the GUI
		private JPanel panel;
		private JButton addButton;
		private java.awt.List list;
		private JLabel moneyLabel;
		
		//Other variables
		private File customerList = new File("Customers.txt");
		private ArrayList<Customer> customers = new ArrayList<Customer>();
		private NumberFormat fmt = NumberFormat.getCurrencyInstance();
		private double totalReceived = 0;
		private double totalPending = 0;
		
		/**
		 * Creates the customer panel by adding and initializing all of the GUI components
		 *
		 * @param parentFrame the frame that this panel will be added to
		 * @param customerPanel the customerPanel that is also in the parentFrame
		 */
		public CustomersPanel(JFrame parentFrame, CustomerPanel customerPanel)
		{
			this.parentFrame = parentFrame;
			this.customerPanel = customerPanel;
			
			panel = new JPanel(new BorderLayout());
			
			addButton = new JButton("Add Customer");
			addButton.addMouseListener(new AddNewCustomer());
			panel.add(addButton, BorderLayout.PAGE_START);
			
			list = new java.awt.List();
			list.addItemListener(new SetCustomer());
			panel.add(list, BorderLayout.CENTER);
			
			moneyLabel = new JLabel("Total received: " + fmt.format(totalReceived) + " Total pending: " + fmt.format(totalPending));
			panel.add(moneyLabel, BorderLayout.PAGE_END);
		}
		
		/**
		 * Refreshes all of the customers that are in the list and recalculates the totals
		 */
		public void refreshList()
		{
			//Removes everything so nothing is duplicated
			list.removeAll();
			customers.clear();
			
			Scanner scan;
			
			try
			{
				//Adds all of the customers to the list and ArrayList, the repaints it
				scan = new Scanner(new BufferedReader(new FileReader(customerList)));
				while(scan.hasNext())
				{
					String customer = scan.nextLine();
					list.add(customer);
					customers.add(Customer.load(customer));
				}
				list.repaint();
				
				scan.close();
			}
			catch(FileNotFoundException fnfe)
			{
				fnfe.printStackTrace();
			}
			
			//Zero out these variables so that nothing is doubled up
			totalReceived = 0;
			totalPending = 0;
			
			//Go through all of the customers in the arraylist and add their individual totals together, then set the text of the label
			for(Customer c : customers)
			{
				totalReceived += c.getTotalReceived();
				totalPending += c.getTotalPending();
				moneyLabel.setText("Total received: " + fmt.format(totalReceived) + " Total pending: " + fmt.format(totalPending));
			}
		}
		
		/**
		 * @return the panel that this class maintains
		 */
		public JPanel getPanel()
		{
			return panel;
		}
		
		//Class that is the listener for the button to add a new customer
		private class AddNewCustomer extends MouseAdapter
		{
			public void mouseClicked(MouseEvent me)
			{
				//Shows a dialog box to get the name of the new customer, then creates the new customer
				String customerName = JOptionPane.showInputDialog(parentFrame, "Enter customer name:", "Customer Name", JOptionPane.QUESTION_MESSAGE);
				Customer customer = new Customer(customerName);
				
				PrintWriter output;
				
				//Adds the name of this customer to the master list of customer names in a text file
				try
				{
					output = new PrintWriter(new BufferedWriter(new FileWriter(customerList, true)));
					output.println(customerName);
					output.close();
					customers.add(customer);
				}
				catch(IOException ioe)
				{
					JOptionPane.showMessageDialog(parentFrame, "Error: Could not find file: Customers.txt", "Error", JOptionPane.ERROR_MESSAGE);
					ioe.printStackTrace();
				}
			}
		}
		
		//Used to set the customer in the customer panel when a value in the list has been clicked
		private class SetCustomer implements ItemListener
		{
			public void itemStateChanged(ItemEvent ie)
			{
				String customer = list.getSelectedItem();
				currentCustomer = Customer.load(customer);
				customerPanel.refresh();
			}
		}
	}
	
	/**
	 * Creates and maintains a panel that has various buttons and fields to add and 
	 * edit information about the current customer. Also shows all of the transactions
	 * for this customer and the total amount of money received and pending from only 
	 * this customer
	 */
	public class CustomerPanel
	{
		//Various panels to contain buttons, labels and fields so everything displays correctly
		private JPanel mainPanel;
		private JPanel infoPanel;
		private JPanel buttonPanel;
		private JPanel labelPanel;
		private JPanel textFieldPanel;
		private JPanel dialogPanel;
		
		//Buttons to add, remove, and edit transactions as well as create a text representation of all the information about this customer
		private JButton addTransaction;
		private JButton removeTransaction;
		private JButton editTransaction;
		private JButton exportCustomer;
		
		//Labels that say "Address: ", "Phone Number: ", and "Email: "
		private JLabel addressLabel;
		private JLabel phoneLabel;
		private JLabel emailLabel;
		
		//Fields that take information about the respective names
		private JTextField addressField;
		private JTextField phoneField;
		private JTextField emailField;
		
		//Label that shows the totals
		private JLabel totalsLabel;
		
		//The text area where the transactions are are the scroll pane that allows it to scroll
		private JTextArea transactionList;
		private JScrollPane scrollPane;
		
		//Used to format doubles into money notation
		private NumberFormat fmt;
		
		//String objects used to name the file and save the text
		private String entry;
		private String temp;
		private String filename;
		private String filenameWithExt;
		private String ext = new String(".cst2");
		
		/**
		 * Creates the panel and initializes all of the GUI components that are in it, as well as add all of the listeners to various objects
		 */
		public CustomerPanel()
		{
			fmt = NumberFormat.getCurrencyInstance();
			
			//Inititalize the panel with transaction buttons and add the buttons, then add listeners to the buttons
			buttonPanel = new JPanel(new GridLayout(2, 2));
			
			addTransaction = new JButton("Add Transaction", new ImageIcon("images/Add Transaction.gif"));
			addTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(addTransaction);
			
			removeTransaction = new JButton("Remove Transaction", new ImageIcon("images/Remove Transaction.gif"));
			removeTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(removeTransaction);
			
			editTransaction = new JButton("Edit Transaction", new ImageIcon("images/Edit Transaction.gif"));
			editTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(editTransaction);
			
			exportCustomer = new JButton("Export to Notebook", new ImageIcon("images/Export.gif"));
			exportCustomer.addMouseListener(new ExportListener());
			buttonPanel.add(exportCustomer);
			
			//Initialize the panel with the labels and add the labels
			labelPanel = new JPanel(new GridLayout(3, 1));
			
			addressLabel = new JLabel("Address:");
			labelPanel.add(addressLabel);
			
			phoneLabel = new JLabel("Phone:");
			labelPanel.add(phoneLabel);
			
			emailLabel = new JLabel("Email:");
			labelPanel.add(emailLabel);
			
			//Initialize the panel with the text fields and add the text fields, then add listeners to the fields
			textFieldPanel = new JPanel(new GridLayout(3, 1));
			
			addressField = new JTextField();
			addressField.addKeyListener(new SaveInfo());
			textFieldPanel.add(addressField);
			
			phoneField = new JTextField();
			phoneField.addKeyListener(new SaveInfo());
			textFieldPanel.add(phoneField);
			
			emailField = new JTextField();
			emailField.addKeyListener(new SaveInfo());
			textFieldPanel.add(emailField);
			
			//Initialize the panel to contain all of the above
			infoPanel = new JPanel(new BorderLayout());
			infoPanel.add(buttonPanel, BorderLayout.PAGE_START);
			infoPanel.add(labelPanel, BorderLayout.LINE_START);
			infoPanel.add(textFieldPanel, BorderLayout.CENTER);
			
			totalsLabel = new JLabel("Total received: $0.00 Total pending: $0.00");
			infoPanel.add(totalsLabel, BorderLayout.PAGE_END);
			
			//Initialize the main panel
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(infoPanel, BorderLayout.PAGE_START);
			
			transactionList = new JTextArea();
			transactionList.setEditable(false);
			scrollPane = new JScrollPane(transactionList);
			mainPanel.add(scrollPane, BorderLayout.CENTER);
		}
		
		/**
		 * Sets the title of the frame to reflect the current customer's name, and add display all of the customer's information if applicable
		 */
		public void refresh()
		{
			try
			{
				frame.setTitle(currentCustomer.getName() + " - MoneyBook");
				
				addressField.setText(currentCustomer.getAddress());
				phoneField.setText(currentCustomer.getPhoneNumber());
				emailField.setText(currentCustomer.getEmail());
				
				totalsLabel.setText("Total received: " + fmt.format(currentCustomer.getTotalReceived()) + " Total pending: " + fmt.format(currentCustomer.getTotalPending()));
				
				transactionList.setText(currentCustomer.getTransactionList());
			}
			catch(NullPointerException npe)
			{
			}
		}
		
		/**
		 * @return the panel represented by this class
		 */
		public JPanel getPanel()
		{
			return mainPanel;
		}
		
		/**
		 * Adds a transaction by showing a dialog box to the user
		 */
		public void addTransaction()
		{
			//Create the panel that has various fields and labels to gain information from the user
			TransactionPanel dialogPanel = new TransactionPanel(-1, null, null, -1.0, -1.0);
			int result = JOptionPane.showOptionDialog(frame, dialogPanel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Add Transaction"), null, null);
			
			if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)
				currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getStartDate(), dialogPanel.getEndDate(), dialogPanel.getMoneyReceived(), dialogPanel.getMoneyPending()));
		}
		
		/**
		 * Adds a transaction from an existing Transaction object
		 *
		 * @param transaction the transaction to be added
		 */
		public void addTransaction(Transaction transaction)
		{
			currentCustomer.add(transaction);
		}
		
		/**
		 * Removes a transaction asking the user to enter an index number in a dialog box
		 */
		public Transaction removeTransaction()
		{
			try
			{
				String indexStr = JOptionPane.showInputDialog(frame, "Enter the entry number you want to remove:", "Remove Parameters", JOptionPane.QUESTION_MESSAGE);
				int index = Integer.parseInt(indexStr);
				return currentCustomer.remove(--index);
			}
			catch(NumberFormatException e)
			{
				return null;
			}
		}
		
		/**
		 * Removes a transaction with an existing index
		 * 
		 * @param index the index to be removed
		 */
		public Transaction removeTransaction(int index)
		{
			return currentCustomer.remove(index);
		}
		
		/**
		 * Edits a transaction by showing two dialog boxes to the user, one that asks 
		 * which one to edit, and another that asks the user to edit that transaction
		 */
		public void editTransaction()
		{
			try
			{
				//See remove transaction
				int option = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the entry number you want to edit: ", "Edit Parameters", JOptionPane.QUESTION_MESSAGE));
				Transaction temp = removeTransaction(--option);
				
				//See add transaction
				TransactionPanel dialogPanel = new TransactionPanel(temp.getType(), temp.getStartDate(), temp.getEndDate(), temp.getMoneyReceived(), temp.getMoneyPending());
				int result = JOptionPane.showOptionDialog(frame, dialogPanel, "Edit Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Edit Transaction"), null, null);
				
				if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)
					currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getStartDate(), dialogPanel.getEndDate(), dialogPanel.getMoneyReceived(), dialogPanel.getMoneyPending()));
			}
			catch(NumberFormatException e){}
		}
		
		//Used to listener for when any of the respective buttons are pressed
		private class TransactionListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				JButton source = (JButton) event.getSource();
				if(source == addTransaction)
					addTransaction();
				else if(source == removeTransaction)
					removeTransaction();
				else if(source == editTransaction)
					editTransaction();
				
				transactionList.setText(currentCustomer.getTransactionList());
			}
		}
		
		//Used to open Notebook with a String representation of this customer
		private class ExportListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				//Creates the new frame and creates a new file using the current customer's name
				Notebook nb = new Notebook();
				File file = new File(currentCustomer.getName() + ".txt");
				
				//Sets the text for the file, saves the file using the file previously created, and then opens it so that the filename is correct
				nb.setText(currentCustomer.toString());
				nb.saveAs(file);
				nb.open(file);
			}
		}
		
		//Used to save the address, phone number, and email to the current customer by listening for enter to be released
		private class SaveInfo extends KeyAdapter
		{
			public void keyReleased(KeyEvent e)
			{				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					if(!addressField.getText().equals(""))
						currentCustomer.setAddress(addressField.getText());
					
					if(!phoneField.getText().equals("0"))
						currentCustomer.setPhoneNumber(phoneField.getText());
					
					if(!emailField.getText().equals(""))
						currentCustomer.setEmail(emailField.getText());
				}
			}
		}
	}
	
	//Refreshes both panels whenever the frame gains focus
	public class Refresh extends WindowAdapter
	{
		public void windowGainedFocus(WindowEvent event)
		{
			customersPanel.refreshList();
			customerPanel.refresh();
		}
	}
}

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