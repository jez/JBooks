/**
 * @MoneyBook.java
 *
 * Last updated: 2011/5/30
 * @author Jacob Zimmerman
 * @version 6.00 2011/5/30
 */

package zimmerman.jacob.moneybookv6;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.GregorianCalendar;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Shows a frame that a user can use to store information about customers and 
 * their transactions. 
 */
public class MoneyBook
{
	//The frame for the application, the two main panels that make up that frame, and a variable representing the current customer
	private JFrame frame;
	private JTabbedPane tabbedPane;
//	private JMenuBar menuBar;
//	private JMenu menu;
//	private JMenuItem menuItem;
	private JPanel customerInfoPanel;
	private JPanel expenseInfoPanel;
	private CustomersPanel customersPanel;
	private CustomerPanel customerPanel;
	private ExpenseInfoPanel expenseInfoPanelObject;
	private Customer currentCustomer = null;
	private ExpenseList expenseList;
	private int currentCustomerIndex = -1;
	
	private File customerList;
	private ArrayList<Customer> customers = new ArrayList<Customer>();
	
	public MoneyBook()
	{
		//Sets up the size, title, and visibility of the frame
		frame = new JFrame("MoneyBook");
		frame.setLayout(new BorderLayout());
		
		//Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
			
		customerList = new File("Zimm's Yard Services.mbk");
		
		if(!customerList.isFile())
		{
			ObjectOutputStream outputStream = null;
			try
			{
				customerList.createNewFile();
				
				outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(customerList)));
				outputStream.writeInt(0);
				outputStream.flush();
				
				expenseList = new ExpenseList();
				
				outputStream.writeObject(expenseList);
				outputStream.flush();
				
				outputStream.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		else
		{
			ObjectInputStream inputStream = null;
			try
			{
				inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(customerList)));
				int extra = inputStream.readInt();
				
				expenseList = (ExpenseList) inputStream.readObject();
				
				inputStream.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			catch(ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace();
			}
		}
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		expenseInfoPanelObject = new ExpenseInfoPanel();
		
		customerInfoPanel = new JPanel();
		customerInfoPanel.setLayout(new GridLayout(1, 2, 10, 0));
		expenseInfoPanel = expenseInfoPanelObject.getPanel();
		
		customerPanel = new CustomerPanel();
		customersPanel = new CustomersPanel(customerPanel);
		customerPanel.setCustomersPanel(customersPanel);
		
		tabbedPane.addChangeListener(new RefreshWindowListener());
		
		//Adds the panel with the customer list and the panel that has information about a specific customer to the customer info tab panel
		customerInfoPanel.add(customersPanel.getPanel());
		customerInfoPanel.add(customerPanel.getPanel());
		
		//Creates the tabs
		tabbedPane.addTab("Customer Info", customerInfoPanel);
		tabbedPane.addTab("Expense Info", expenseInfoPanel);
		frame.add(tabbedPane, BorderLayout.CENTER);
		
//		menuBar = new JMenuBar();
//		
//		menu = new JMenu("Customers");
//		menuItem = new JMenuItem("Add");
//		menuItem.addMouseListener(customersPanel.new AddNewCustomer());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Edit Name");
//		menuItem.addMouseListener(customersPanel.new EditCustomer());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Remove");
//		menuItem.addMouseListener(customersPanel.new RemoveCustomer());
//		menu.add(menuItem);
//		menuBar.add(menu);
//		
//		menu = new JMenu("Customer");
//		menuItem = new JMenuItem("Add Transaction");
//		menuItem.addMouseListener(customerPanel.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Edit Transaction");
//		menuItem.addMouseListener(customerPanel.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Remove Transaction");
//		menuItem.addMouseListener(customerPanel.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Export as XML");
//		menuItem.addMouseListener(customerPanel.new ExportListener());
//		menu.add(menuItem);
//		menuBar.add(menu);
//		
//		menu = new JMenu("Expenses");
//		menuItem = new JMenuItem("Add Expense");
//		menuItem.addMouseListener(expenseInfoPanelObject.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Remove Expense");
//		menuItem.addMouseListener(expenseInfoPanelObject.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Remove Expense");
//		menuItem.addMouseListener(expenseInfoPanelObject.new TransactionListener());
//		menu.add(menuItem);
//		menuItem = new JMenuItem("Export as XML");
//		menuItem.addMouseListener(expenseInfoPanelObject.new ExportListener());
//		menu.add(menuItem);
//		menuBar.add(menu);
//		
//		frame.setJMenuBar(menuBar);
		
		//Sets the icon image in the top left corner
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Money Book.gif")));
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Sorts the customers based on the Customer.compareTo(Customer) method
	 */
	public void sortCustomers()
	{
		Collections.sort(customers);
		save();
		customersPanel.refreshList();
	} 
		
	/**
	 * Writes all of the data to the output file
	 */
	public void save()
	{
		ObjectOutputStream outputStream = null;
		int numCustomers = customers.size();
		
		try
		{
			outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(customerList)));
			
			outputStream.writeInt(numCustomers);
			outputStream.flush();
			
			outputStream.writeObject(expenseList);
			outputStream.flush();
			
			for(int i = 0; i < numCustomers; i++)
			{
				outputStream.writeObject(customers.get(i));
				outputStream.flush();
			}
			
			outputStream.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		XMLConverter.toXML(customerList.getName());
	}
	
	class ShowRecent extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			String resultN = "";
			String resultD = "";
			SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");
			
			ArrayList<NameAndDate> namesAndDates = new ArrayList<NameAndDate>();
			
			for(Customer c : customers)
			{
				NameAndDate temp = new NameAndDate(c.getName(), c.getLastCutDate());
				namesAndDates.add(temp);
			}
			
			Collections.sort(namesAndDates);
			Collections.reverse(namesAndDates);
			
			for(NameAndDate nad : namesAndDates)
			{
				resultN += nad.getName() + "\n";
				try
				{
					resultD += sdfmt.format(nad.getDate().getTime()) + "\n";
				}
				catch(NullPointerException npe)
				{
					resultD += "\n";
				}
			}
			
			JPanel temp = new JPanel(new GridLayout(1,2));
			
			JTextArea recentListN = new JTextArea(resultN);
			recentListN.setEditable(false);
			JTextArea recentListD = new JTextArea(resultD);
			recentListD.setEditable(false);
			
			temp.add(recentListN);
			temp.add(recentListD);
			
			JOptionPane.showMessageDialog(frame, temp, "Recent Grass Cuttings", JOptionPane.INFORMATION_MESSAGE);
		}
		
		private class NameAndDate implements Comparable<NameAndDate>
		{
			String name;
			GregorianCalendar date;
			
			public NameAndDate(String name, GregorianCalendar date)
			{
				this.name = name;
				this.date = date;
			}
			
			public int compareTo(NameAndDate nad)
			{
				try
				{
					return date.compareTo(nad.getDate());
				}
				catch(NullPointerException npe)
				{
					return -1;
				}
			}
			
			public GregorianCalendar getDate()
			{
				return date;
			}
			
			public String getName()
			{
				return name;
			}
		}
	}
	
	private class ShowTopGrossing extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			String result = "";
			NumberFormat fmt = NumberFormat.getCurrencyInstance();
			
			ArrayList<NameAndMoney> namesAndMonies = new ArrayList<NameAndMoney>();
			
			for(Customer c : customers)
			{
				namesAndMonies.add(new NameAndMoney(c.getName(), c.getTotalReceived() + c.getBalance()));
			}
			
			Collections.sort(namesAndMonies);
			
			for(NameAndMoney n : namesAndMonies)
			{
				result += fmt.format(n.getMoney()) + ":\t" + n.getName() + "\n";
			}
			
			JTextArea recentList = new JTextArea(result);
			recentList.setEditable(false);
			JOptionPane.showMessageDialog(frame, recentList, "Recent Grass Cuttings", JOptionPane.INFORMATION_MESSAGE);
		}
		
		private class NameAndMoney implements Comparable<NameAndMoney>
		{
			String name;
			double money;
			
			public NameAndMoney(String name, double money)
			{
				this.name = name;
				this.money = money;
			}
			
			public int compareTo(NameAndMoney nam)
			{
				return -(new Double(this.money).compareTo(new Double(nam.getMoney())));
			}
			
			public double getMoney()
			{
				return money;
			}
			
			public String getName()
			{
				return name;
			}
		}
	}
	
	private class RefreshWindowListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent ce)
		{
			if(tabbedPane.getSelectedIndex() == 0)
				customerPanel.refresh();
			else
				expenseInfoPanelObject.refresh();
		}
	}
	
	/**
	 * Creates and maintains a panel that has a button to add a customer to the list of 
	 * customers, a label that says all of the money earned and pending, and a list
	 * of customers
	 */
	public class CustomersPanel
	{
		//Used to refresh the panel when a new customer is picked from the list of customers
		private CustomerPanel customerPanel;
		
		//Used to make the GUI
		private JPanel panel;
		private JPanel buttonPanel;
		private JButton addButton;
		private JButton editButton;
		private JButton removeButton;
		private JButton recentButton;
		private JButton topGrossingButton;
		private java.awt.List list;
		private JLabel moneyLabel;
		
		//Other variables
		private NumberFormat fmt = NumberFormat.getCurrencyInstance();
		private double totalEarned = 0;
		private double totalPending = 0;
		private double totalCredit = 0;
		
		/**
		 * Creates the customer panel by adding and initializing all of the GUI components
		 *
		 * @param customerPanel the customerPanel that is also in the parentFrame
		 */
		public CustomersPanel(CustomerPanel customerPanel)
		{
			this.customerPanel = customerPanel;
			
			panel = new JPanel(new BorderLayout());
			buttonPanel = new JPanel(new GridLayout(2,3));
			
			addButton = new JButton("Add Customer");
			addButton.addMouseListener(new AddNewCustomer());
			buttonPanel.add(addButton);
			
			editButton = new JButton("Edit Customer Name");
			editButton.addMouseListener(new EditCustomer());
			buttonPanel.add(editButton);
			
			removeButton = new JButton("Remove Customer");
			removeButton.addMouseListener(new RemoveCustomer());
			buttonPanel.add(removeButton);
			
			recentButton = new JButton("Show Recent Yards Cut");
			recentButton.addMouseListener(new ShowRecent());
			buttonPanel.add(recentButton);
			
			topGrossingButton = new JButton("Show Top Grossing");
			topGrossingButton.addMouseListener(new ShowTopGrossing());
			buttonPanel.add(topGrossingButton);
			
			panel.add(buttonPanel, BorderLayout.PAGE_START);
			
			list = new java.awt.List();
			list.addItemListener(new SetCustomer());
			panel.add(list, BorderLayout.CENTER);
			
			moneyLabel = new JLabel("Total earned: " + fmt.format(totalEarned) + " Total pending: " + fmt.format(totalPending) + " Total credit: " + fmt.format(totalCredit));
			panel.add(moneyLabel, BorderLayout.PAGE_END);
			
			refreshList();
			refreshTotals();
		}
		
		/**
		 * Refreshes all of the customers that are in the list
		 */
		public void refreshList()
		{
			//Removes everything so nothing is duplicated
			list.removeAll();
			customers.clear();
			
			ObjectInputStream inputStream = null;
			int numCustomers = 0;
			
			for(int i = 0; i < 2; i++)
			{
				try
				{
					inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(customerList)));
					
					numCustomers = inputStream.readInt();
					
					//Extra data: this object is an ExpenseList object. Doesn't need to be read here
					Object extra = inputStream.readObject();
					
					for(int j = 0; j < numCustomers; j++)
					{
						customers.add((Customer) inputStream.readObject());
						list.add(customers.get(j).getName());
					}
					
					inputStream.close();
					break;
				}
//				catch(InvalidClassException ice)
//				{
//					XMLConverter.fromXML("ZimmsYardServices");
//				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
				catch(ClassNotFoundException cnfe)
				{
					cnfe.printStackTrace();
				}
			}
		}
		
		/**
		 * Recalculates the totals
		 */
		public void refreshTotals()
		{
			//Zero out these variables so that nothing is doubled up
			totalEarned = 0;
			totalPending = 0;
			totalCredit = 0;
			
			//Go through all of the customers in the arraylist and add their individual totals together, then set the text of the label
			for(Customer c : customers)
			{
				totalEarned += c.getTotalReceived();
				
				if(c.getBalance() > 0)
					totalPending += c.getBalance();
				else if(c.getBalance() < 0)
					totalCredit += -(c.getBalance());
			}
			
			totalEarned += totalPending;
			totalEarned -= totalCredit;
			moneyLabel.setText("Total earned: " + fmt.format(totalEarned) + " Total pending: " + fmt.format(totalPending) + " Total credit: " + fmt.format(totalCredit));
		}
		
		/**
		 * @return the panel that this class maintains
		 */
		public JPanel getPanel()
		{
			return panel;
		}
		
		public double getTotalEarned()
		{
			return totalEarned;
		}
		
		public double getTotalPending()
		{
			return totalPending;
		}
		
		public double getTotalCredit()
		{
			return totalCredit;
		}
		
		//Class that is the listener for the button to add a new customer
		class AddNewCustomer extends MouseAdapter
		{
			public void mouseClicked(MouseEvent me)
			{
				//Shows a dialog box to get the name of the new customer, then creates the new customer
				String customerName = JOptionPane.showInputDialog(frame, "Enter customer name:", "Add Customer", JOptionPane.QUESTION_MESSAGE);
				if(customerName != null)
				{
					Customer customer = new Customer(customerName);
					customers.add(customer);
					list.add(customer.getName());
				}
				
				sortCustomers();
				save();
				refreshTotals();
			}
		}
		
		//Class that is the listener for the button that edits a customer's name
		class EditCustomer extends MouseAdapter
		{
			public void mouseClicked(MouseEvent me)
			{
				try
				{				
					String customerName = JOptionPane.showInputDialog(frame, "Enter " + currentCustomer.getName() + "'s new name:", "Edit Name", JOptionPane.QUESTION_MESSAGE);
					if(!customerName.equals(""))
						currentCustomer.setName(customerName);
				}
				catch(NullPointerException npe)
				{
					JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				sortCustomers();
				save();
				refreshTotals();
			}
		}
		
		//Class that is the listener for the button that removes a customer
		class RemoveCustomer extends MouseAdapter
		{
			public void mouseClicked(MouseEvent me)
			{
				String customerName = currentCustomer.getName();
				boolean result = customers.remove(currentCustomer);
				currentCustomer = null;
				
				sortCustomers();
				save();
				refreshTotals();
								
				if(result)
					JOptionPane.showMessageDialog(frame, "'" + customerName + "' was removed successfully.", "Remove Customer", JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(frame, "'" + customerName + "' could not be removed.", "Remove Customer", JOptionPane.ERROR_MESSAGE);
			}
		}
		
		//Used to set the customer in the customer panel when a value in the list has been clicked
		class SetCustomer implements ItemListener
		{
			public void itemStateChanged(ItemEvent ie)
			{
				currentCustomerIndex = list.getSelectedIndex();
				currentCustomer = customers.get(currentCustomerIndex);
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
		
		private CustomersPanel customersPanel;
		
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
		private String ext = new String(".cst3");
		
		/**
		 * Creates the panel and initializes all of the GUI components that are in it, as well as add all of the listeners to various objects
		 */
		public CustomerPanel()
		{
			fmt = NumberFormat.getCurrencyInstance();
			
			//Inititalize the panel with transaction buttons and add the buttons, then add listeners to the buttons
			buttonPanel = new JPanel(new GridLayout(2, 2));
			
			addTransaction = new JButton("Add Transaction", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Add Transaction.gif"))));
			addTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(addTransaction);
			
			removeTransaction = new JButton("Remove Transaction", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Remove Transaction.gif"))));
			removeTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(removeTransaction);
			
			editTransaction = new JButton("Edit Transaction", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Edit Transaction.gif"))));
			editTransaction.addMouseListener(new TransactionListener());
			buttonPanel.add(editTransaction);
			
			exportCustomer = new JButton("Export as XML", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Export.gif"))));
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
			
			totalsLabel = new JLabel("Total received: $0.00 Balance: $0.00");
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
				
				totalsLabel.setText("Total received: " + fmt.format(currentCustomer.getTotalReceived()) + " Total balance: " + fmt.format(currentCustomer.getBalance()));
				
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
		
		public void setCustomersPanel(CustomersPanel customersPanel)
		{
			this.customersPanel = customersPanel;
		}
		
		/**
		 * Adds a transaction by showing a dialog box to the user
		 */
		public void addTransaction()
		{
			//Create the panel that has various fields and labels to gain information from the user
			TransactionPanel dialogPanel = new TransactionPanel(null, null, -1.0);
			int result = JOptionPane.showOptionDialog(frame, dialogPanel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Add Transaction"), null, null);
			
			if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)
				currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getDate(), dialogPanel.getAmount()));
			
			if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION && dialogPanel.getType() != TType.PAYMENT && dialogPanel.getType() != TType.TIP)
			{
				result = JOptionPane.showConfirmDialog(frame, "Paid?", "Paid?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(result == JOptionPane.YES_OPTION)
					currentCustomer.add(new Transaction(TType.PAYMENT, dialogPanel.getDate(), -(dialogPanel.getAmount())));
			}
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
				Transaction result = currentCustomer.remove(--index);
				return result;
			}
			catch(NumberFormatException e)
			{
				return null;
			}
		}
		
		/**
		 * Edits a transaction by showing two dialog boxes to the user, one that asks 
		 * which one to edit, and another that asks the user to edit that transaction
		 *
		 * @return the transaction before editing
		 */
		public Transaction editTransaction()
		{
			try
			{
				//See remove transaction
				int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the entry number you want to edit: ", "Edit Parameters", JOptionPane.QUESTION_MESSAGE));
				Transaction result = currentCustomer.remove(--index);
				
				//See add transaction
				TransactionPanel dialogPanel = new TransactionPanel(result.getType(), result.getDate(), result.getAmount());
				int returnOption = JOptionPane.showOptionDialog(frame, dialogPanel, "Edit Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Edit Transaction"), null, null);
				
				if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
					currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getDate(), dialogPanel.getAmount()));
				return result;
			}
			catch(NumberFormatException e)
			{
				return null;
			}
		}
		
		//Used to listener for when any of the respective buttons are pressed
		class TransactionListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					AbstractButton source = (AbstractButton) event.getSource();
					if(source.getText().equals("Add Transaction"))
						addTransaction();
					else if(source.getText().equals("Edit Transaction"))
						editTransaction();
					else if(source.getText().equals("Remove Transaction"))
						removeTransaction();
					
					transactionList.setText(currentCustomer.getTransactionList());
					
					customers.set(currentCustomerIndex, currentCustomer);
					save();
					refresh();
					customersPanel.refreshTotals();
				}
				catch(NullPointerException npe)
				{
					JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		//Used to open Notebook with a String representation of this customer
		class ExportListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					//Creates the new frame and creates a new file using the current customer's name
					File newFolder = new File("XML Printouts\\");
					newFolder.mkdir();
					File file = new File("XML Printouts\\" + currentCustomer.getName() + ".xml");
					Notebook nb = new Notebook();
					
					//Sets the text for the file, saves the file using the file previously created, and then opens it so that the filename is correct
					String result = "";
					result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
					result += "<?xml-stylesheet type=\"text/xsl\" href=\"ZimmsYardServices.xsl\"?>\n";
					result += currentCustomer.getXML();
					nb.setText(result);
					nb.saveAs(file);
					nb.exit();
					
					try
					{ 
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getPath()); 
					}
					catch (IOException e)
					{ 
						e.printStackTrace(); 
					}
				}
				catch(NullPointerException npe)
				{
					for(Customer c : customers)
					{
						File newFolder = new File("XML Printouts\\");
						newFolder.mkdir();
						File file = new File("XML Printouts\\" + c.getName() + ".xml");
						Notebook nb = new Notebook();
						
						//Sets the text for the file, saves the file using the file previously created, and then opens it so that the filename is correct
						String result = "";
						result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
						result += "<?xml-stylesheet type=\"text/xsl\" href=\"ZimmsYardServices.xsl\"?>\n";
						result += c.getXML();
						nb.setText(result);
						nb.saveAs(file);
						nb.exit();
						
						try
						{ 
							Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file.getPath()); 
						}
						catch (IOException e)
						{ 
							e.printStackTrace(); 
						}	
					}
				}
			}
		}
		
		//Used to save the address, phone number, and email to the current customer by listening for enter to be released
		private class SaveInfo extends KeyAdapter
		{
			public void keyReleased(KeyEvent e)
			{				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					try
					{
						if(!addressField.getText().equals(""))
							currentCustomer.setAddress(addressField.getText());
						
						if(!phoneField.getText().equals("0"))
							currentCustomer.setPhoneNumber(phoneField.getText());
						
						if(!emailField.getText().equals(""))
							currentCustomer.setEmail(emailField.getText());
					}
					catch(NullPointerException npe)
					{
						JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					customers.set(currentCustomerIndex, currentCustomer);
					save();
				}
			}
		}
	}
	
	public class ExpenseInfoPanel
	{
		private JPanel mainPanel;
		private JPanel buttonPanel;
		
		//Buttons to add, remove, and edit expense transactions as well as create a text representation of all the information about this customer
		private JButton addExpense;
		private JButton removeExpense;
		private JButton editExpense;
		private JButton exportTransactionList;
		
		//Label that shows the totals
		private JLabel totalLabel;
		
		//The text area where the transactions are are the scroll pane that allows it to scroll
		private JTextArea infoTextArea;
		private JScrollPane scrollPane;
		
		//Used to format doubles into money notation
		private NumberFormat fmt;
		
		public ExpenseInfoPanel()
		{
			fmt = NumberFormat.getCurrencyInstance();
			
			//Initialize the main panel
			mainPanel = new JPanel(new BorderLayout());
			
			//Inititalize the panel with transaction buttons and add the buttons, then add listeners to the buttons
			buttonPanel = new JPanel(new GridLayout(1, 4));
			
			addExpense = new JButton("Add Expense", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Add Transaction.gif"))));
			addExpense.addMouseListener(new TransactionListener());
			buttonPanel.add(addExpense);
			
			removeExpense = new JButton("Remove Expense", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Remove Transaction.gif"))));
			removeExpense.addMouseListener(new TransactionListener());
			buttonPanel.add(removeExpense);
			
			editExpense = new JButton("Edit Expense", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Edit Transaction.gif"))));
			editExpense.addMouseListener(new TransactionListener());
			buttonPanel.add(editExpense);
			
			exportTransactionList = new JButton("Export as XML", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Export.gif"))));
			exportTransactionList.addMouseListener(new ExportListener());
			buttonPanel.add(exportTransactionList);
			
			mainPanel.add(buttonPanel, BorderLayout.PAGE_START);
			
			infoTextArea = new JTextArea(expenseList.toString());
			infoTextArea.setEditable(false);
			scrollPane = new JScrollPane(infoTextArea);
			mainPanel.add(scrollPane, BorderLayout.CENTER);
			
			totalLabel = new JLabel("Total Expended: " + fmt.format(expenseList.getTotalExpended()));
			mainPanel.add(totalLabel, BorderLayout.PAGE_END);
		}
		
		public void refresh()
		{
			try
			{
				frame.setTitle("Expenses - MoneyBook");
				
				totalLabel.setText("Total expended: " + fmt.format(expenseList.getTotalExpended()));
				
				infoTextArea.setText(expenseList.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		/**
		 * @return the panel represented by this class
		 */
		public JPanel getPanel()
		{
			return mainPanel;
		}
		
		public void refreshTextArea()
		{
			ObjectInputStream inputStream = null;
			
			try
			{
				inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(customerList)));
				
				//Extra data: this int is the number of customers. Doesn't need to be read here
				int extra = inputStream.readInt();
				
				expenseList = (ExpenseList) inputStream.readObject();
				
				inputStream.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			catch(ClassNotFoundException cnfe)
			{
				cnfe.printStackTrace();
			}
			
			refresh();
		}
		
		/**
		 * Adds an expense by showing a dialog box to the user
		 */
		public void addExpense()
		{
			//Create the panel that has various fields and labels to gain information from the user
			ExpensePanel dialogPanel = new ExpensePanel(null, null, -1.0);
			int result = JOptionPane.showOptionDialog(frame, dialogPanel, "Add Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Add Transaction"), null, null);
			
			if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)
				expenseList.add(new Expense(dialogPanel.getType(), dialogPanel.getDate(), dialogPanel.getAmount()));
			
			refresh();
		}
		
		/**
		 * Removes a transaction asking the user to enter an index number in a dialog box
		 */
		public Expense removeExpense()
		{
			try
			{
				String indexStr = JOptionPane.showInputDialog(frame, "Enter the entry number you want to remove:", "Remove Expense", JOptionPane.QUESTION_MESSAGE);
				int index = Integer.parseInt(indexStr);
				Expense result = expenseList.remove(--index);
				
				refresh();
				return result;
			}
			catch(NumberFormatException e)
			{
				refresh();
				return null;
			}
		}
		
		/**
		 * Edits a transaction by showing two dialog boxes to the user, one that asks 
		 * which one to edit, and another that asks the user to edit that transaction
		 *
		 * @return the transaction before editing
		 */
		public Expense editExpense()
		{
			try
			{
				//See remove transaction
				int index = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the entry number you want to edit: ", "Edit Expense", JOptionPane.QUESTION_MESSAGE));
				Expense result = expenseList.remove(--index);
				
				//See add transaction
				ExpensePanel dialogPanel = new ExpensePanel(result.getType(), result.getDate(), result.getAmount());
				int returnOption = JOptionPane.showOptionDialog(frame, dialogPanel, "Edit Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Edit Transaction"), null, null);
				
				if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
					expenseList.add(new Expense(dialogPanel.getType(), dialogPanel.getDate(), dialogPanel.getAmount()));
				
				refresh();
				return result;
			}
			catch(NumberFormatException e)
			{
				refresh();
				return null;
			}
		}
		
		//Used to listener for when any of the respective buttons are pressed
		class TransactionListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					AbstractButton source = (AbstractButton) event.getSource();
					if(source.getText().equals("Add Expense"))
						addExpense();
					else if(source.getText().equals("Remove Expense"))
						removeExpense();
					else if(source.getText().equals("Edit Expense"))
						editExpense();
					
					infoTextArea.setText(expenseList.toString());
					
					save();
					refresh();
					refreshTextArea();
				}
				catch(NullPointerException npe)
				{
					JOptionPane.showMessageDialog(frame, "Error: expense list not found.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		//Used to open Notebook with a String representation of this customer
		class ExportListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{	
				//Creates the new frame and creates a new file using the current customer's name
				Notebook nb = new Notebook();
				File file = new File("Expense List.xml");
				
				//Sets the text for the file, saves the file using the file previously created, and then opens it so that the filename is correct
				String result = "";
				result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
				result += expenseList.getXML();
				nb.setText(result);
				nb.saveAs(file);
				nb.open(file);
			}
		}
	} 
}