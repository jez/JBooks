/**
 * @MoneyBook.java
 *
 *
 * @author Jacob Zimmerman
 * @version 9.00 3 Apr 2012
 */

package zimmerman.jacob.moneybookv9;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.SplashScreen;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.charset.Charset;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UIManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.horstmann.corejava.GBC;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

public class MoneyBook
{
	//Variables associated with the overall display
	private JFrame frame;
	private JTabbedPane tabbedPane;
	private ExpenseInfoPanel expenseInfoPanel;
	private JPanel customerInfoPanel; //used to combine the two customer info panels into one for the tabbed pane

	//Variables associated with creating the customers panel
	private JPanel customersPanel;

	private JButton welcomeButton;
	private JButton showTopGrossingButton;
	private JButton showRecentYardsCutButton;
	private JButton addCustomer;
	private JButton removeCustomer;
	private JButton editCustomer;

	private JLabel totalsLabel;
	private double totalEarned = 0;
	private double totalPending = 0;
	private double totalCredit = 0;

	private JList<String> customersJList;
	private DefaultListModel<String> customersListModel;
	private JScrollPane customersScrollPane;

	//Variables associated with creating the customer panel
	private JPanel customerPanel;

	private JButton addTransaction;
	private JButton editTransaction;
	private JButton removeTransaction;
	private JButton exportCustomerXML;

	private JLabel addressLabel;
	private JTextField addressField;
	private JLabel phoneLabel;
	private JTextField phoneField;
	private JLabel emailLabel;
	private JTextField emailField;
	private JButton saveCustomerInfo;

	private JLabel ccTotalsLabel;

	private JList<String> customerJList;
	private DefaultListModel<String> customerListModel;
	private JScrollPane customerScrollPane;


	//Contains the current user information
	private Path file;
	private Path temp;
	private Path XMLPrintoutsFolder;
	private ExpenseList expenseList;
	private ArrayList<Customer> customers;
	private Expense currentExpense;
	private Customer currentCustomer;
	private Transaction currentTransaction;


	//Other variables
	private boolean isFirstWelcome = true;
	private NumberFormat fmt = NumberFormat.getCurrencyInstance();
	private SimpleDateFormat sdfmt = new SimpleDateFormat("EEEE, MMMM d, yyyy");
	private GUIExceptionHandler handler = new GUIExceptionHandler();
	private JFileChooser chooser = new JFileChooser(".");


	public MoneyBook()
	{
		//Set up the frame
		frame = new JFrame("Money Book v9");
		frame.setLayout(new BorderLayout());

		//Set the file filter for the chooser to xml files
		chooser.setFileFilter(new XMLFileFilter());

		try
		{
			Thread.setDefaultUncaughtExceptionHandler(handler);
		}
		catch(SecurityException se)
		{
			//Will fail in an applet
		}

		//Display the splash screen (Splash screen set up in the manifest file)
		final SplashScreen splash = SplashScreen.getSplashScreen();
		if(splash == null)
		{
			handler.handle(new Exception("getSplashScreen() returned null."));
			System.exit(0);
		}
		Graphics2D g = splash.createGraphics();
		if(g == null)
		{
			handler.handle(new Exception("splash.createGraphics() returned null."));
		}
		splash.update();
		try
		{
			Thread.sleep(2500);
		}
		catch(InterruptedException ie)
		{
			handler.handle(ie);
		}
		splash.close();

		//Set the look and feel to Nimbus
		try
		{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}

			chooser.updateUI();
		}
		catch(Exception e){}

		//Set up the tabbed pane
		tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

		//Initialize the panels
		expenseInfoPanel = new ExpenseInfoPanel();

		initializeCustomersPanel();
		initializeCustomerPanel();

		customerInfoPanel = new JPanel(new GridLayout(1, 2)); //two columns, one for the customers panel
		customerInfoPanel.add(customersPanel);                //and one for the customer panel, used to add
		customerInfoPanel.add(customerPanel);		          //them to the tabbed pane

		tabbedPane.add("Customer Info", customerInfoPanel);
		tabbedPane.add("Expense Info", expenseInfoPanel);
		tabbedPane.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent event)
			{
				if(tabbedPane.getSelectedIndex() == 0)
				{
					refreshCustomersPanel();
					refreshCustomerPanel();

					if(currentCustomer != null)
						frame.setTitle(currentCustomer.getName() + " - " + file.getFileName().toString() + " - Money Book v9");
					else
						frame.setTitle(file.getFileName().toString() + " - Money Book v9");
				}
				else
				{
					expenseInfoPanel.refresh();
					frame.setTitle("Expenses - " + file.getFileName().toString() + " - Money Book v9");
				}
			}
		});
		frame.add(tabbedPane, BorderLayout.CENTER);

		//Set up the folder where the xml printouts will be stored
		try
		{
			XMLPrintoutsFolder = Paths.get(".\\XML Printouts\\");
			Files.createDirectory(XMLPrintoutsFolder);
		}
		catch(FileAlreadyExistsException faee){}
		catch(IOException ioe){handler.handle(ioe);}

		//Sets the icon image in the top left corner
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Money Book.png")));

		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Show the welcome screen, which allows the user to either open a file or create a new one
		showWelcomeScreen();
		isFirstWelcome = false;
	}

	/**
	 * Shows various dialog boxes that constitute the welcome screen
	 */
	public void showWelcomeScreen()
	{
		//Initialize the rest of the variables, mainly those dealing with the user's information by displaying a welcome screen
		int returnOption = JOptionPane.showOptionDialog(frame, "Do you want to open a file or start a new one?",
			"Welcome", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				new String[] {"Open", "New", "Cancel"}, null);

		//User chose to open a file
		if(returnOption == JOptionPane.YES_OPTION)
		{
			//Show a dialog that lets the user choose where and what to save the file as
			returnOption = chooser.showOpenDialog(frame);

			//Check to see if "Open" or "Cancel" was pressed
			if(returnOption == JFileChooser.APPROVE_OPTION)
			{
				//"Open" pressed, check to see if filename contains extension
				file = chooser.getSelectedFile().toPath();
				String filename = file.getFileName().toString();
				if(filename.length() >= 4 && !filename.substring(filename.length()-4).equals(".xml"))
				{
					//Missing extension, append extension, open file
					filename.concat(".xml");
					file = Paths.get(file.subpath(0, file.getNameCount()-2).toString(), filename);
				}

				//Initialize the temp directory, insurance against corrupting the real file
				temp = Paths.get(chooser.getCurrentDirectory().toString(), "temp.xml");

				open();
				currentCustomer = null;
				refreshCustomersPanel();
				refreshCustomerPanel();

				//Update totals
				//Zero out these variables so that nothing is doubled up
				totalEarned = 0;
				totalPending = 0;
				totalCredit = 0;

				//Go through all of the customers in the arraylist and add their individual totals together,
				//then set the text of the label
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

				totalsLabel.setText("Total earned: " + fmt.format(totalEarned) + " . . . Total Pending: " + fmt.format(totalPending) +
					" . . . Total Credit: " + fmt.format(totalCredit));
			}
			else
			{
				//"Cancel" pressed, check to see if we should exit
				if(isFirstWelcome)
					System.exit(0);
			}
		}
		else if(returnOption == JOptionPane.NO_OPTION)
		{
			//Show a dialog that lets the user choose where and what to save the file as
			returnOption = chooser.showDialog(frame, "Create");

			//Check to see if "Create" or "Cancel" was pressed
			if(returnOption == JFileChooser.APPROVE_OPTION)
			{
				//"Save" pressed, check to see if filename contains extension
				file = chooser.getSelectedFile().toPath();
				String filename = file.getFileName().toString();
				if(filename.length() >= 4 && filename.substring(filename.length()-4).equals(".xml"))
				{
					try
					{
						//Found extension, overwrite existing and create new file
						Files.deleteIfExists(file);
						Files.createFile(file);
					}catch(IOException ioe){handler.handle(ioe);}
				}
				else
				{
					//Missing extension, append extension, overwrite existing and create new file
					file = Paths.get(file.toString() + ".xml");
					try
					{
						Files.deleteIfExists(file);
						Files.createFile(file);
					}catch(IOException ioe){handler.handle(ioe);}
				}

				//Create temp target directory, insurance against IOException during save method corrupting the actual file
				temp = Paths.get(chooser.getCurrentDirectory().toString(), "temp.xml");

				//Initialize the variables that store information
				expenseList = new ExpenseList();
				customers = new ArrayList<Customer>();
				currentExpense = null;
				currentCustomer = null;
				currentTransaction = null;
				totalEarned = 0.0;
				totalPending = 0.0;
				totalCredit = 0.0;

				//Save the file for the first time by writing out the information to the text file
				save();
				refreshCustomersPanel();
				refreshCustomerPanel();
				totalsLabel.setText("Total earned: " + fmt.format(totalEarned) + " . . . Total Pending: " + fmt.format(totalPending) +
					" . . . Total Credit: " + fmt.format(totalCredit));
			}
			else
			{
				//Cancel pressed, no file selected **chooser.getSelectedFile() will return null**, set default name to "Untitled.xml"
				if(isFirstWelcome)
					System.exit(0);
			}
		}
		else
		{
			if(isFirstWelcome)
				System.exit(0);
		}

		frame.setTitle(file.getFileName().toString() + " - Money Book v9");
	}

	/**
	 * Saves the user data by first writing it to a temp file then copying it into the real file
	 */
	public void save()
	{
		//Create the root element string by removing all non word characters from the filename element of the current file's path
		String rootElementName = "";
		String filename = file.getFileName().toString();
		rootElementName = filename.substring(filename.length()-4);
		rootElementName = rootElementName.replaceAll("[\\W]", "");

		//Define some variables associated with moving around the data
		String result = "";      //Create an empty string to store all of the raw string data
		String tempString = "";  //Create an empty string to serve as a buffer between the XML and the result
		Scanner scan;			 //Used to write data from tempString to result

		//Headings of the xml file with root element
		result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		result += "<" + rootElementName + ">\n";

		//Write out the expense list
		tempString = expenseList.getXML();
		scan = new Scanner(tempString);
		while(scan.hasNext())
		{
			result += "\t" + scan.nextLine() + "\n";
		}

		//Write out the customers one by one
		for(Customer c : customers)
		{
			tempString = c.getXML();
			scan = new Scanner(tempString);

			while(scan.hasNext())
			{
				result += "\t" + scan.nextLine() + "\n";
			}
		}

		//Root element footer
		result += "</" + rootElementName + ">";

		//Create a new print writer and start writing data to the temp file location
		try
		{
			Files.deleteIfExists(temp);
			Files.createFile(temp);
		}catch(IOException ioe){handler.handle(ioe);}
		try(PrintWriter writer = new PrintWriter(Files.newBufferedWriter(temp, Charset.forName("UTF-8"))))
		{
			scan = new Scanner(result);
			while(scan.hasNext())
			{
				writer.println(scan.nextLine());
			}
		}
		catch(IOException ioe){handler.handle(ioe);}

		try
		{
			//Copy the temp file to the actual file (protects against corrupting the actual file
			Files.copy(temp, file, REPLACE_EXISTING);
		}
		catch(IOException ioe){handler.handle(ioe);}

		//If there were no exceptions, delete the temp file
		//If there were IOExcpetions, this part of the method will not execute
		try
		{
			Files.deleteIfExists(temp);
		}catch(IOException ioe){handler.handle(ioe);}
	}

	public void open()
	{
		expenseList = new ExpenseList();
		customers = new ArrayList<Customer>();

		//Temporary variables that are used to access all of the data
		String tempName = "";
		String tempAddress = "";
		String tempPhone = "";
		String tempEmail = "";

		TType tempType = null;
		EType tempTransType = null;
		GregorianCalendar tempDate = null;
		double tempAmount = 0;

		Transaction tempTransaction = null;
		Expense tempExpense = null;

		Customer tempCustomer = null;

		DocumentBuilderFactory dbf = null;
		DocumentBuilder db = null;
		Document doc = null;

		try
		{
			//Initialize the variables that will be used to read the raw xml data
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			doc = db.parse(file.toUri().toString());
		}
		catch(ParserConfigurationException pce){handler.handle(pce);}
		catch(SAXException saxe){handler.handle(saxe);}
		catch(IOException ioe){handler.handle(ioe);}

		doc.getDocumentElement().normalize();

		//First elements are of type "expense", get all the expenses and add them to the expense list
		NodeList nodeExpenseList = doc.getElementsByTagName("expense");

		for(int i = 0; i < nodeExpenseList.getLength(); i++)
		{
			//Get expenses one by one
			Node firstExpenseNode = nodeExpenseList.item(i);

			if(firstExpenseNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element firstExpenseElement = (Element) firstExpenseNode;

				NodeList typeElementList = firstExpenseElement.getElementsByTagName("type");
				Element typeElement = (Element) typeElementList.item(0);
				NodeList type = typeElement.getChildNodes();
				tempTransType = EType.parse(((Node) type.item(0)).getNodeValue());

				NodeList dateElementList = firstExpenseElement.getElementsByTagName("date");
				Element dateElement = (Element) dateElementList.item(0);
				NodeList date = dateElement.getChildNodes();
				tempDate = Transaction.parse(((Node) date.item(0)).getNodeValue());

				NodeList amountElementList = firstExpenseElement.getElementsByTagName("amount");
				Element amountElement = (Element) amountElementList.item(0);
				NodeList amount = amountElement.getChildNodes();
				tempAmount = fmt.parse(((Node) amount.item(0)).getNodeValue(), new java.text.ParsePosition(0)).doubleValue();
			}

			tempExpense = new Expense(tempTransType, tempDate, tempAmount);
			expenseList.add(tempExpense);
		}

		NodeList nodeCustomerList = doc.getElementsByTagName("customer");

		for(int i = 0; i < nodeCustomerList.getLength(); i++)
		{
			Node firstCustomerNode = nodeCustomerList.item(i);

			if(firstCustomerNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element firstCustomerElement = (Element) firstCustomerNode;

				NodeList nameElementList = firstCustomerElement.getElementsByTagName("name");
				Element nameElement = (Element) nameElementList.item(0);
				NodeList name = nameElement.getChildNodes();
				tempName = ((Node) name.item(0)).getNodeValue();

				NodeList addressElementList = firstCustomerElement.getElementsByTagName("address");
				Element addressElement = (Element) addressElementList.item(0);
				NodeList address = addressElement.getChildNodes();
				try
				{
					tempAddress = ((Node) address.item(0)).getNodeValue();
				}
				catch(NullPointerException npe)
				{
					tempAddress = "";
				}

				NodeList phoneNumberElementList = firstCustomerElement.getElementsByTagName("phoneNumber");
				Element phoneNumberElement = (Element) phoneNumberElementList.item(0);
				NodeList phoneNumber = phoneNumberElement.getChildNodes();
				try
				{
					tempPhone = ((Node) phoneNumber.item(0)).getNodeValue();
				}
				catch(NullPointerException npe)
				{
					tempPhone = "";
				}

				NodeList emailElementList = firstCustomerElement.getElementsByTagName("email");
				Element emailElement = (Element) emailElementList.item(0);
				NodeList email = emailElement.getChildNodes();
				try
				{
					tempEmail = ((Node) email.item(0)).getNodeValue();
				}
				catch(NullPointerException npe)
				{
					tempEmail = "";
				}

				tempCustomer = new Customer(tempName, tempAddress, tempPhone, tempEmail);

				NodeList nodeTransactionList = firstCustomerElement.getElementsByTagName("transaction");

				for(int j = 0; j < nodeTransactionList.getLength(); j++)
				{
					Node firstTransactionNode = nodeTransactionList.item(j);

					if (firstTransactionNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element firstTransactionElement = (Element) firstTransactionNode;

						NodeList typeElementList = firstTransactionElement.getElementsByTagName("type");
						Element typeElement = (Element) typeElementList.item(0);
						NodeList type = typeElement.getChildNodes();
						tempType = TType.parse(((Node) type.item(0)).getNodeValue());

						NodeList dateElementList = firstTransactionElement.getElementsByTagName("date");
						Element dateElement = (Element) dateElementList.item(0);
						NodeList date = dateElement.getChildNodes();
						tempDate = Transaction.parse(((Node) date.item(0)).getNodeValue());

						NodeList amountElementList = firstTransactionElement.getElementsByTagName("amount");
						Element amountElement = (Element) amountElementList.item(0);
						NodeList amount = amountElement.getChildNodes();
						tempAmount = fmt.parse(((Node) amount.item(0)).getNodeValue(), new java.text.ParsePosition(0)).doubleValue();
					}

					tempTransaction = new Transaction(tempType, tempDate, tempAmount);
					tempCustomer.add(tempTransaction);
				}
			}
			customers.add(tempCustomer);
		}
	}

	public void initializeCustomersPanel()
	{
		//Set up the panel with a GridBagLayout
		customersPanel = new JPanel(new BorderLayout());

		//Initialise the 6 top buttons
		welcomeButton = new JButton("Welcome Dialog");
		welcomeButton.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent me)
			{
				showWelcomeScreen();
			}
		});

		showTopGrossingButton = new JButton("Show Top Grossing");
		showTopGrossingButton.addMouseListener(new ShowTopGrossing());

		showRecentYardsCutButton = new JButton("Show Recent Yards Cut");
		showRecentYardsCutButton.addMouseListener(new ShowRecent());

		addCustomer = new JButton("Add Customer");
		addCustomer.addMouseListener(new CustomerListener());

		editCustomer = new JButton("Edit Customer's Name");
		editCustomer.addMouseListener(new CustomerListener());

		removeCustomer = new JButton("Remove Customer");
		removeCustomer.addMouseListener(new CustomerListener());

		//Set up the totals label
		totalsLabel = new JLabel("Total Earned: " + fmt.format(totalEarned) + " . . . Total Pending: " + fmt.format(totalPending)
			+ " . . . Total Credit: " + fmt.format(totalCredit));

		//Set up the main customers list and make it scrollable
		customersListModel = new DefaultListModel<String>();
		customersJList = new JList<String>(customersListModel);
		customersJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		customersJList.setLayoutOrientation(JList.VERTICAL);
		customersJList.setVisibleRowCount(1);
		customersJList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse)
			{
				try
				{
					currentCustomer = customers.get(customersJList.getSelectedIndex());

					refreshCustomerPanel();
					frame.setTitle(currentCustomer.getName() + " - " + file.getFileName().toString() + " - Money Book v9");
				}catch(ArrayIndexOutOfBoundsException aioobe){}
			}
		});
		customersScrollPane = new JScrollPane(customersJList);

		JPanel tempCustomersPanel = new JPanel(new GridBagLayout());
		tempCustomersPanel.add(welcomeButton, new GBC(0, 0)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.FIRST_LINE_START)
			.setWeight(0.5, 0));
		tempCustomersPanel.add(showTopGrossingButton, new GBC(1, 0)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.PAGE_START)
			.setWeight(0.5, 0));
		tempCustomersPanel.add(showRecentYardsCutButton, new GBC(2, 0)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.FIRST_LINE_END)
			.setWeight(0.5, 0));
		tempCustomersPanel.add(addCustomer, new GBC(0, 1)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.LINE_START));
		tempCustomersPanel.add(editCustomer, new GBC(1, 1)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.CENTER));
		tempCustomersPanel.add(removeCustomer, new GBC(2, 1)
			.setFill(GBC.HORIZONTAL)
			.setAnchor(GBC.LINE_END));
		tempCustomersPanel.add(totalsLabel, new GBC(0, 2)
			.setAnchor(GBC.CENTER)
			.setSpan(3, 1));
		customersPanel.add(tempCustomersPanel, BorderLayout.PAGE_START);
		customersPanel.add(customersScrollPane, BorderLayout.CENTER);
	}

	public class CustomerListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me) throws IllegalArgumentException
		{
			AbstractButton source = (AbstractButton) me.getSource();

			switch(source.getText())
			{
			case "Add Customer":
				//Add new customer with specified name, then sort
				String newCustomerName = JOptionPane.showInputDialog(frame, "Enter the new customer's name:", "Add Customer",
					JOptionPane.QUESTION_MESSAGE);

				if(newCustomerName != null)
				{
					Customer newCustomer = new Customer(newCustomerName);
					customers.add(newCustomer);
					currentCustomer = newCustomer;

					Collections.sort(customers);
				}
				break;
			case "Edit Customer's Name":
				//Check to make sure a customer is selected, then set the customers name as specified
				if(currentCustomer == null)
					JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "Error Editing Customer's Name",
						JOptionPane.ERROR_MESSAGE);
				else
				{
					String nameBefore = currentCustomer.getName();
					String nameAfter = JOptionPane.showInputDialog(frame, "Enter " + nameBefore + "'s new name:",
						"Edit Customer's Name", JOptionPane.QUESTION_MESSAGE);

					if(nameAfter != null)
					{
						currentCustomer.setName(nameAfter);

						JOptionPane.showMessageDialog(frame, nameBefore + "'s name was changed to " + nameAfter + ".",
							"Edit Customer's Name", JOptionPane.INFORMATION_MESSAGE);

						Collections.sort(customers);

						frame.setTitle(nameAfter + " - " + file.getFileName().toString() + " - Money Book v9");
					}
				}
				break;
			case "Remove Customer":
				//Check to make sure a customer is selected, then remove
				if(currentCustomer == null)
					JOptionPane.showMessageDialog(frame, "Please a select customer and try again.", "Error Removing Customer",
						JOptionPane.ERROR_MESSAGE);
				else
				{
					int returnOption = JOptionPane.showOptionDialog(frame, "Are you sure you want to remove the selected customer?",
						"Confirm Customer Removal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
							new String[] {"Yes, remove.", "No, cancel."}, null);

					if(returnOption == JOptionPane.OK_OPTION)
					{
						String customerName = currentCustomer.getName();
						boolean wasRemoved = customers.remove(currentCustomer);

						if(wasRemoved)
						{
							currentCustomer = null;
							JOptionPane.showMessageDialog(frame, customerName + " was successfully removed.", "Remove Customer",
								JOptionPane.INFORMATION_MESSAGE);

							Collections.sort(customers);

							currentCustomer = null;
							frame.setTitle(file.getFileName().toString() + " - Money Book v9");

							//Update totals
							//Zero out these variables so that nothing is doubled up
							totalEarned = 0;
							totalPending = 0;
							totalCredit = 0;

							//Go through all of the customers in the arraylist and add their individual totals together,
							//then set the text of the label
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

							totalsLabel.setText("Total earned: " + fmt.format(totalEarned) + " . . . Total Pending: " +
								fmt.format(totalPending) + " . . . Total Credit: " + fmt.format(totalCredit));
						}
						else
							JOptionPane.showMessageDialog(frame, customerName + " could not be removed at this time.",
								"Error Removing Customer", JOptionPane.ERROR_MESSAGE);
					}
				}
				break;
			default:
				throw new IllegalArgumentException("The source text didn't match any of the three options.");
			}

			save();
			refreshCustomersPanel();
			refreshCustomerPanel();
		}
	}

	public class ShowRecent extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			ArrayList<NameAndDate> namesAndDates = new ArrayList<NameAndDate>();

			for(Customer c : customers)
			{
				NameAndDate temp = new NameAndDate(c.getName(), c.getLastCutDate());
				namesAndDates.add(temp);
			}

			Collections.sort(namesAndDates);
			Collections.reverse(namesAndDates);

			JPanel grossingList = new JPanel(new GridLayout(namesAndDates.size(), 2));

			for(NameAndDate nad : namesAndDates)
			{
				grossingList.add(new JLabel(nad.getName()));
				if(nad.getDate() == null)
					grossingList.add(new JLabel());
				else
					grossingList.add(new JLabel(sdfmt.format(nad.getDate().getTime())));
			}

			JOptionPane.showMessageDialog(frame, grossingList, "Recent Grass Cuttings", JOptionPane.INFORMATION_MESSAGE);
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

	public class ShowTopGrossing extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			String result = "";

			ArrayList<NameAndMoney> namesAndMonies = new ArrayList<NameAndMoney>();

			for(Customer c : customers)
			{
				namesAndMonies.add(new NameAndMoney(c.getName(), c.getTotalReceived() + c.getBalance()));
			}

			Collections.sort(namesAndMonies);

			JPanel recentList = new JPanel(new GridLayout(namesAndMonies.size(), 2));

			for(NameAndMoney n : namesAndMonies)
			{
				recentList.add(new JLabel(fmt.format(n.getMoney())));
				recentList.add(new JLabel(n.getName()));
			}
			JOptionPane.showMessageDialog(frame, recentList, "Top Grossing Customers", JOptionPane.INFORMATION_MESSAGE);
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

	public void refreshCustomersPanel()
	{
		customersListModel.removeAllElements();

		for(Customer c : customers)
		{
			customersListModel.addElement(c.getName());
		}
	}

	public void addTransaction()
	{
		//Check to see if a transaction is selected
		if(currentCustomer == null)
		{
			JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "No Customer Selected",
				JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			//Initialize the transaction panel that will be used in a dialog box to get the transaction data from the user
			TransactionPanel transactionPanel = new TransactionPanel(null, null, -1.0);

			//Show the dialog
			int returnOption = JOptionPane.showConfirmDialog(frame, transactionPanel, "Add Transaction",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			//Check to see that the user didn't abort, then add the trasaction
			if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
				currentCustomer.add(transactionPanel.getTransaction());

			if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION && transactionPanel.getType()
				!= TType.PAYMENT && transactionPanel.getType() != TType.TIP)
			{
				returnOption = JOptionPane.showConfirmDialog(frame, "Was the amount of this transaction paid?", "Paid?",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(returnOption == JOptionPane.YES_OPTION)
					currentCustomer.add(new Transaction(TType.PAYMENT, transactionPanel.getDate(), -transactionPanel.getAmount()));
			}
		}
	}

	public void editTransaction()
	{
		//Check to see if a transaction is selected
		if(currentTransaction == null)
		{
			JOptionPane.showMessageDialog(frame, "Please select a transaction and try again.", "No Transaction Selected",
				JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			//Remove old transaction, currentTransaction is still in tact
			boolean wasRemoved = currentCustomer.remove(currentTransaction);

			if(wasRemoved)
			{
				//Initialize the transaction panel using the currentTransaction's data that will be used in a dialog box
				//to get the transaction data from the user
				TransactionPanel transactionPanel = new TransactionPanel(currentTransaction);

				//Show the dialog
				int returnOption = JOptionPane.showConfirmDialog(frame, transactionPanel, "Edit Transaction",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				//Check to see that the user didn't abort, then add the trasaction
				if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
					currentCustomer.add(transactionPanel.getTransaction());
				else
					currentCustomer.add(currentTransaction);
			}
		}
	}

	public void removeTransaction()
	{
		//Check to see if a transaction is selected
		if(currentTransaction == null)
		{
			JOptionPane.showMessageDialog(frame, "Please select a transaction and try again.", "No Transaction Selected",
				JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			int returnOption = JOptionPane.showOptionDialog(frame, "Are you sure you want to remove the selected transaction?",
				"Confirm Transaction Removal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
				new String[] {"Yes, remove it.", "No, cancel it."}, null);

			if(returnOption == JOptionPane.OK_OPTION)
			{
				currentCustomer.remove(currentTransaction);
			}
		}
	}

	public void initializeCustomerPanel()
	{
		//Set up the main panel
		customerPanel = new JPanel(new BorderLayout());

		//Set up the buttons that either deal with transactions or exporting the customer's  xml data
		addTransaction = new JButton("Add Transaction", new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("images/Add Transaction.gif"))));
		addTransaction.addActionListener(new TransactionListener());
		editTransaction = new JButton("Edit Transaction", new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("images/Edit Transaction.gif"))));
		editTransaction.addActionListener(new TransactionListener());
		removeTransaction = new JButton("Remove Transaction", new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("images/Remove Transaction.gif"))));
		removeTransaction.addActionListener(new TransactionListener());
		exportCustomerXML = new JButton("Export as XML", new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("images/Export.gif"))));
		exportCustomerXML.addActionListener(new CustomerExportListener());

		addressField = new JTextField("1000 N. Main Street");
		addressField.setForeground(Color.GRAY);
		addressField.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent event)
			{
				JTextComponent source = (JTextComponent) event.getSource();
				source.setForeground(Color.BLACK);
				source.selectAll();
			}
			public void focusLost(FocusEvent event){
				JTextComponent source = (JTextComponent) event.getSource();
				if(source.getText().equals("1000 N. Main Street") || source.getText().equals("")){
					source.setForeground(Color.GRAY);
					source.setText("1000 N. Main Street");
				}
			}
		});

		try{
			phoneField = new JFormattedTextField(new MaskFormatter("(###) ###-####"));
		}catch(ParseException pe){handler.handle(pe);}
		phoneField.setText("(555) 555-5555");
		phoneField.setForeground(Color.GRAY);
		phoneField.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent event){
				JTextComponent source = (JTextComponent) event.getSource();
				source.setForeground(Color.BLACK);
				source.selectAll();
			}
			public void focusLost(FocusEvent event){
				JTextComponent source = (JTextComponent) event.getSource();
				if(source.getText().equals("(   )    -    ") || source.getText().equals("(555) 555-5555")){
					source.setForeground(Color.GRAY);
					source.setText("(555) 555-5555");
				}
			}
		});

		emailField = new JTextField("john@doe.com");
		emailField.setForeground(Color.GRAY);
		emailField.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent event)
			{
				JTextComponent source = (JTextComponent) event.getSource();
				source.setForeground(Color.BLACK);
				source.selectAll();
			}
			public void focusLost(FocusEvent event){
				JTextComponent source = (JTextComponent) event.getSource();
				if(source.getText().equals("john@doe.com") || source.getText().equals("")){
					source.setForeground(Color.GRAY);
					source.setText("john@doe.com");
				}
			}
		});

		saveCustomerInfo = new JButton("Save");
		saveCustomerInfo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event)
			{
				if(currentCustomer == null)
				{
					JOptionPane.showMessageDialog(frame, "Please select a customer and try again.", "No Customer Selected",
						JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					int currentCustomerIndex = customers.indexOf(currentCustomer);

					if(addressField.getText().equals("1000 N. Main Street"))
						currentCustomer.setAddress("");
					else
						currentCustomer.setAddress(addressField.getText());

					if(phoneField.getText().equals("(555) 555-5555"))
						currentCustomer.setPhoneNumber("");
					else
						currentCustomer.setPhoneNumber(phoneField.getText());

					if(emailField.getText().equals("john@doe.com"))
						currentCustomer.setEmail("");
					else
						currentCustomer.setEmail(emailField.getText());

					customers.set(currentCustomerIndex, currentCustomer);
					save();
				}
			}
		});

		//Set up the label the tracks the total received and balance
		ccTotalsLabel = new JLabel("Total Received: $0.00 . . . Total Balance: $0.00");

		//Set up the main list of transactions for this customer
		customerListModel = new DefaultListModel<String>();
		customerJList = new JList<String>(customerListModel);
		customerJList.setCellRenderer(new MultiLineCellRenderer());
		customerJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		customerJList.setLayoutOrientation(JList.VERTICAL);
		customerJList.setVisibleRowCount(-1);
		customerJList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent lse)
			{
				try
				{
					currentTransaction = currentCustomer.getTransactions().get(customerJList.getSelectedIndex());
				}catch(ArrayIndexOutOfBoundsException aioobe){}
			}
		});
		customerScrollPane = new JScrollPane(customerJList);

		//Add every component to the main panel using the GBC helper class
		JPanel tempCustomerPanel = new JPanel(new GridBagLayout());
		tempCustomerPanel.add(addressField, new GBC(0, 0)
			.setFill(GBC.HORIZONTAL)
			.setSpan(2, 1));
		tempCustomerPanel.add(phoneField, new GBC(2, 0)
			.setFill(GBC.HORIZONTAL)
			.setSpan(2, 1));
		tempCustomerPanel.add(saveCustomerInfo, new GBC(4, 0)
			.setFill(GBC.BOTH)
			.setSpan(1, 3));
		tempCustomerPanel.add(ccTotalsLabel, new GBC(0, 1)
			.setSpan(2, 1)
			.setAnchor(GBC.CENTER));
		tempCustomerPanel.add(emailField, new GBC(2, 1)
			.setFill(GBC.HORIZONTAL)
			.setSpan(2, 1));
		tempCustomerPanel.add(addTransaction, new GBC(0, 2)
			.setFill(GBC.HORIZONTAL)
			.setWeight(0.5, 0));
		tempCustomerPanel.add(editTransaction, new GBC(1, 2)
			.setFill(GBC.HORIZONTAL)
			.setWeight(0.5, 0));
		tempCustomerPanel.add(removeTransaction, new GBC(2, 2)
			.setFill(GBC.HORIZONTAL)
			.setWeight(0.5, 0));
		tempCustomerPanel.add(exportCustomerXML, new GBC(3, 2)
			.setFill(GBC.HORIZONTAL)
			.setWeight(0.5, 0));
		customerPanel.add(tempCustomerPanel, BorderLayout.PAGE_START);
		customerPanel.add(customerScrollPane, BorderLayout.CENTER);
	}

	public void refreshCustomerPanel()
	{
		if(currentCustomer != null)
		{
			if(currentCustomer.getAddress() != ""){
				addressField.setText(currentCustomer.getAddress());
				addressField.setForeground(Color.BLACK);
			}
			else{
				addressField.setText("1000 N. Main Street");
				addressField.setForeground(Color.GRAY);
			}

			if(currentCustomer.getPhoneNumber() != ""){
				phoneField.setText(currentCustomer.getPhoneNumber());
				phoneField.setForeground(Color.BLACK);
			}
			else{
				phoneField.setText("(555) 555-5555");
				phoneField.setForeground(Color.GRAY);
			}

			if(currentCustomer.getEmail() != ""){
				emailField.setText(currentCustomer.getEmail());
				emailField.setForeground(Color.BLACK);
			}
			else{
				emailField.setText("john@doe.com");
				emailField.setForeground(Color.GRAY);
			}

			ccTotalsLabel.setText("Total Received: " + fmt.format(currentCustomer.getTotalReceived()) + " . . . Total Balance: "
				+ fmt.format(currentCustomer.getBalance()));

			customerListModel.removeAllElements();

			for(Transaction t : currentCustomer.getTransactions())
			{
				customerListModel.addElement(t.toString());
			}
		}
		else
		{
			addressField.setText("1000 N. Main Street");
			addressField.setForeground(Color.GRAY);
			phoneField.setText("(555) 555-5555");
			phoneField.setForeground(Color.GRAY);
			emailField.setText("john@doe.com");
			emailField.setForeground(Color.GRAY);
			ccTotalsLabel.setText("Total Received: $0.00 . . . Total Balance: $0.00");
			customerListModel.removeAllElements();
		}
	}

	public class TransactionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			AbstractButton source = (AbstractButton) ae.getSource();

			switch(source.getText())
			{
			case "Add Transaction":
				addTransaction();
				break;
			case "Edit Transaction":
				editTransaction();
				break;
			case "Remove Transaction":
				removeTransaction();
				break;
			default:
				throw new IllegalArgumentException("The source didn't match any of the three options.");
			}

			//Update totals
			//Zero out these variables so that nothing is doubled up
			totalEarned = 0;
			totalPending = 0;
			totalCredit = 0;

			//Go through all of the customers in the arraylist and add their individual totals together,
			//then set the text of the label
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

			totalsLabel.setText("Total earned: " + fmt.format(totalEarned) + " . . . Total Pending: " + fmt.format(totalPending) +
				" . . . Total Credit: " + fmt.format(totalCredit));

			save();
			refreshCustomerPanel();
		}
	}

	public class CustomerExportListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			try
			{
				///Initialize the xml printout file
				Path xmlPrintout = Paths.get(XMLPrintoutsFolder.toAbsolutePath().toString() + "\\" + currentCustomer.getName() + ".xml");
				Files.deleteIfExists(xmlPrintout);
				Files.createFile(xmlPrintout);

				//Create a new instance of notebook to handle the saving of the text
				Notebook nb = new Notebook();

				//Sets the text for the file, saves the file using the file previously created, and then
				//opens it so that the filename is correct
				String result = "";
				result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
				result += "<?xml-stylesheet type=\"text/xsl\" href=\"ZimmsYardServices.xsl\"?>\n";
				result += currentCustomer.getXML();
				nb.setText(result);
				nb.saveAs(xmlPrintout);
				nb.exit();
				Runtime.getRuntime().exec("C:\\Progra~1\\Intern~1\\iexplore.exe" + " " + xmlPrintout.toString());
			}
			catch(IOException ioe){handler.handle(ioe);}
		}
	}

	/**
	 * Used to keep track of the panel that shows the expenses and buttons to manipulate them
	 */
	public class ExpenseInfoPanel extends JPanel
	{
		//Buttons to add, remove, and edit expense transactions as well as create a text representation
		//of all the information about this customer
		private JButton addExpense;
		private JButton removeExpense;
		private JButton editExpense;
		private JButton exportExpenseList;

		//Label that shows the totals
		private JLabel totalExpendedLabel;

		//The list where the transactions are and the scroll pane that allows it to scroll
		private JList<String> expenseJList;
		private DefaultListModel<String> expenseListModel;
		private JScrollPane expenseScrollPane;

		//Used to format doubles into money notation
		private NumberFormat fmt;

		public ExpenseInfoPanel()
		{
			fmt = NumberFormat.getCurrencyInstance();

			//Initialize the main panel
			setLayout(new BorderLayout());

			//Inititalize the panel with transaction buttons and add the buttons, then add listeners to the buttons
			addExpense = new JButton("Add Expense", new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("images/Add Transaction.gif"))));
			addExpense.addActionListener(new ExpenseListener());

			removeExpense = new JButton("Remove Expense", new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("images/Remove Transaction.gif"))));
			removeExpense.addActionListener(new ExpenseListener());

			editExpense = new JButton("Edit Expense", new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("images/Edit Transaction.gif"))));
			editExpense.addActionListener(new ExpenseListener());

			exportExpenseList = new JButton("Export as XML", new ImageIcon(Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("images/Export.gif"))));
			exportExpenseList.addActionListener(new ExpenseExportListener());

			//Set up the totals label
			totalExpendedLabel = new JLabel();

			//Set up the JList
			expenseListModel = new DefaultListModel<String>();
			expenseJList = new JList<String>(expenseListModel);
			expenseJList.setCellRenderer(new MultiLineCellRenderer());
			expenseJList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			expenseJList.setLayoutOrientation(JList.VERTICAL);
			expenseJList.setVisibleRowCount(-1);
			expenseJList.addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent lse)
				{
					try
					{
						currentExpense = expenseList.get(expenseJList.getSelectedIndex());
					}catch(ArrayIndexOutOfBoundsException aioobe){}
				}
			});
			expenseScrollPane = new JScrollPane(expenseJList);

			//Add every component to the JPanel, using the GBC helper class to align everything in a GridBagLayout
			JPanel tempExpensePanel = new JPanel(new GridBagLayout());
			tempExpensePanel.add(addExpense, new GBC(0, 0)
				.setFill(GBC.HORIZONTAL)
				.setWeight(0.5, 0));
			tempExpensePanel.add(editExpense, new GBC(1, 0)
				.setFill(GBC.HORIZONTAL)
				.setWeight(0.5, 0));
			tempExpensePanel.add(removeExpense, new GBC(2, 0)
				.setFill(GBC.HORIZONTAL)
				.setWeight(0.5, 0));
			tempExpensePanel.add(exportExpenseList, new GBC(3, 0)
				.setFill(GBC.HORIZONTAL)
				.setWeight(0.5, 0));
			tempExpensePanel.add(totalExpendedLabel, new GBC(0, 1)
				.setSpan(4,1)
				.setAnchor(GBC.CENTER));
			add(tempExpensePanel, BorderLayout.PAGE_START);
			add(expenseScrollPane, BorderLayout.CENTER);
		}

		public void addExpense()
		{
			//Initialize the expense panel that will be used in a dialog box to get the expense data from the user
			ExpensePanel expensePanel = new ExpensePanel(null, null, -1.0);

			//Show the dialog
			int returnOption = JOptionPane.showConfirmDialog(frame, expensePanel, "Add Expense",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

			//Check to see that the user didn't abort, then add the expense
			if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
				expenseList.add(expensePanel.getExpense());
		}

		public void removeExpense()
		{
			//Make sure an expense is selected
			if(currentExpense == null)
				JOptionPane.showMessageDialog(frame, "Please select an expense and try again.", "Error Removing Expense",
					JOptionPane.ERROR_MESSAGE);
			else
			{
				int returnOption = JOptionPane.showOptionDialog(frame, "Are you sure you want to remove the selected expense?",
					"Confirm Expense Removal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
					new String[] {"Yes, remove it.", "No, cancel it."}, null);

				if(returnOption == JOptionPane.OK_OPTION)
				{
					//Remove expense
					boolean wasRemoved = expenseList.remove(currentExpense);

					//Check success
					if(wasRemoved)
						JOptionPane.showMessageDialog(frame, "The expense was successfully removed.", "Remove Expense",
							JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(frame, "The expense could not be removed at this time.", "Remove Expense",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		public void editExpense()
		{
			//Make sure an expense is selected
			if(currentExpense == null)
				JOptionPane.showMessageDialog(frame, "Please select an expense and try again.", "Error Removing Expense",
					JOptionPane.ERROR_MESSAGE);
			else
			{
				//Remove old expense, currentExpense still in tact
				boolean wasRemoved = expenseList.remove(currentExpense);
				if(wasRemoved)
				{
					//Create a new expense panel with the old information
					ExpensePanel expensePanel = new ExpensePanel(currentExpense);

					//Show the dialog
					int returnOption = JOptionPane.showConfirmDialog(frame, expensePanel, "Edit Expense",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

					//Check to see that the user didn't abort, then add the new expense
					if(returnOption != JOptionPane.CANCEL_OPTION && returnOption != JOptionPane.CLOSED_OPTION)
						expenseList.add(expensePanel.getExpense());
					else
						expenseList.add(currentExpense);
				}
			}
		}

		public void refresh()
		{
			//Make sure title is set
			frame.setTitle("Expenses - " + file.getFileName().toString() + " - Money Book v9");

			//Set expended label
			totalExpendedLabel.setText("Total expended: " + fmt.format(expenseList.getTotalExpended()));

			//Remove all current elements from the list model and add the new ones
			expenseListModel.removeAllElements();

			for(Expense e : expenseList.getExpenses())
			{
				expenseListModel.addElement(e.toString());
			}
		}

		private class ExpenseListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event) throws IllegalArgumentException
			{
				AbstractButton source = (AbstractButton) event.getSource();

				switch(source.getText())
				{
				case "Add Expense":
					addExpense();
					break;
				case "Remove Expense":
					removeExpense();
					break;
				case "Edit Expense":
					editExpense();
					break;
				default:
					throw new IllegalArgumentException("The source did not match either of the three options.");
				}

				save();
				refresh();
			}
		}

		private class ExpenseExportListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					//Creates the new frame and creates a new file
					Notebook nb = new Notebook();
					Path path = Paths.get(System.getProperty("user.dir"), "Expense List.xml");
					Files.deleteIfExists(path);
					Files.createFile(path);

					//Sets the text for the file, saves the file using the file previously created,
					//and then opens it so that the filename is correct
					String result = "";
					result += "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
					result += expenseList.getXML();
					nb.setText(result);
					nb.saveAs(path);
					nb.open(path);
				}
				catch(IOException ioe)
				{
					handler.handle(ioe);
				}
			}
		}
	}

	/**
	 * Registered as the handler for exceptions on the Swing event thread.
	 * The handler will put up an alert panel and dump the stack trace to the console.
	 */
	private class GUIExceptionHandler implements Thread.UncaughtExceptionHandler
	{
		public void handle(Exception e)
		{
			e.printStackTrace();

			JTextArea area = new JTextArea(10, 40);

			//Print message to a writer because in order to capture output of the stack trace
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			area.setText(writer.toString());

			area.setCaretPosition(0);

			int result = JOptionPane.showOptionDialog(frame, new JScrollPane(area), e.toString(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {"Copy", "Cancel"}, null);

			if (result == JOptionPane.OK_OPTION)
			{
			    area.setSelectionStart(0);
			    area.setSelectionEnd(area.getText().length());
			    area.copy(); // copy to clipboard
			}
		}
		public void uncaughtException(Thread t, Throwable e)
		{
			e.printStackTrace();

			JTextArea area = new JTextArea(10, 40);

			//Print message to a writer because in order to capture output of the stack trace
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			area.setText(writer.toString());

			area.setCaretPosition(0);

			int result = JOptionPane.showOptionDialog(frame, new JScrollPane(area), e.toString(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new String[] {"Copy", "Cancel"}, null);

			if (result == JOptionPane.OK_OPTION)
			{
			    area.setSelectionStart(0);
			    area.setSelectionEnd(area.getText().length());
			    area.copy(); // copy to clipboard
			}
		}
	}

	/**
	 * Class that allows a JList to show multi-line entries.
	 *
	 * The default cell renderer used by a JList simply formats the text using a JLabel as the component,
	 * so this provides a new implementation that uses a JTextArea instead.
	 */
	private class MultiLineCellRenderer extends JTextArea implements ListCellRenderer<String>
	{
		public Component getListCellRendererComponent(JList<? extends String> list,
													  String value,
													  int index,
													  boolean isSelected,
													  boolean cellHasFocus)
		{
			//Set the test of the JTextArea
			setText(value);

			//Take care of the list item if it is selected or not
			if(isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			//Other stuff to make sure the item is displayed properly
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			setOpaque(true);
			return this;
		}
	}
}