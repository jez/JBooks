/**
 * @MoneyBook.java
 *
 * Last updated: 2010/5/6
 * @author Jacob Zimmerman
 * @version 5.00 2010/6/9
 */

package zimmerman.jacob.moneybookv5;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

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
	private Customer currentCustomer = null;
	private int currentCustomerIndex = -1;
	
	private File customerList;
	private ArrayList<Customer> customers = new ArrayList<Customer>();
	
	public MoneyBook()
	{
		//Sets up the size, title, and visibility of the frame
		frame = new JFrame("MoneyBook");
		frame.setLayout(new GridLayout(1, 2, 10, 0));
		
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
				
				outputStream.close();
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		customerPanel = new CustomerPanel();
		customersPanel = new CustomersPanel(customerPanel);
		customerPanel.setCustomersPanel(customersPanel);
		
		//Adds the panel with the customer list and the panel that has information about a specific customer to the frame
		frame.add(customersPanel.getPanel());
		frame.add(customerPanel.getPanel());
		
		//Sets the icon image in the top left corner
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Money Book.gif")));
		frame.setLocation(100, 50);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		private JButton addButton;
		private java.awt.List list;
		private JLabel moneyLabel;
		
		//Other variables
		private NumberFormat fmt = NumberFormat.getCurrencyInstance();
		private double totalReceived = 0;
		private double totalPending = 0;
		
		/**
		 * Creates the customer panel by adding and initializing all of the GUI components
		 *
		 * @param customerPanel the customerPanel that is also in the parentFrame
		 */
		public CustomersPanel(CustomerPanel customerPanel)
		{
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
			
			try
			{
				inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(customerList)));
				
				numCustomers = inputStream.readInt();
				
				for(int i = 0; i < numCustomers; i++)
				{
					customers.add((Customer) inputStream.readObject());
					list.add(customers.get(i).getName());
				}
				
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
		
		/**
		 * Recalculates the totals
		 */
		public void refreshTotals()
		{
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
				String customerName = JOptionPane.showInputDialog(frame, "Enter customer name:", "Customer Name", JOptionPane.QUESTION_MESSAGE);
				if(customerName != null)
				{
					Customer customer = new Customer(customerName);
					customers.add(customer);
					list.add(customer.getName());
				}
					save();
					refreshTotals();
			}
		}
		
		//Used to set the customer in the customer panel when a value in the list has been clicked
		private class SetCustomer implements ItemListener
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
		private JPanel dialogPanel;
		
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
			TransactionPanel dialogPanel = new TransactionPanel(-1, null, null, -1.0, -1.0);
			int result = JOptionPane.showOptionDialog(frame, dialogPanel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Add Transaction"), null, null);
			
			if(result != JOptionPane.CANCEL_OPTION && result != JOptionPane.CLOSED_OPTION)
				currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getStartDate(), dialogPanel.getEndDate(), dialogPanel.getMoneyReceived(), dialogPanel.getMoneyPending()));
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
				TransactionPanel dialogPanel = new TransactionPanel(result.getType(), result.getStartDate(), result.getEndDate(), result.getMoneyReceived(), result.getMoneyPending());
				int returnOption = JOptionPane.showOptionDialog(frame, dialogPanel, "Edit Transaction", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("images/Edit Transaction"), null, null);
				
				if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
					currentCustomer.add(new Transaction(dialogPanel.getType(), dialogPanel.getStartDate(), dialogPanel.getEndDate(), dialogPanel.getMoneyReceived(), dialogPanel.getMoneyPending()));
				return result;
			}
			catch(NumberFormatException e)
			{
				return null;
			}
		}
		
		//Used to listener for when any of the respective buttons are pressed
		private class TransactionListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					JButton source = (JButton) event.getSource();
					if(source == addTransaction)
						addTransaction();
					else if(source == removeTransaction)
						removeTransaction();
					else if(source == editTransaction)
						editTransaction();
					
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
		private class ExportListener extends MouseAdapter
		{
			public void mouseClicked(MouseEvent event)
			{	
				//Creates the new frame and creates a new file using the current customer's name
				Notebook nb = new Notebook();
				File file = new File(currentCustomer.getName() + ".xml");
				
				//Sets the text for the file, saves the file using the file previously created, and then opens it so that the filename is correct
				String result = "";
				result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
				result += "<?xml-stylesheet type=\"text/xsl\" href=\"ZimmsYardServices.xsl\"?>\n";
				result += currentCustomer.getXML();
				nb.setText(result);
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
}