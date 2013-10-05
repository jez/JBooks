/**
 * @MoneyBook.java
 *
 * Last updated: 2010/6/9
 * @author Jacob Zimmerman 2010/6/9
 * @version 1.00 2010/6/9
 */

package zimmerman.jacob.moneybook;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class MoneyBook
{
	private JFrame frame;
	private JButton add;
	private java.awt.List list;
	private JLabel total;
	
	private File customerList = new File("Customers.txt");
	private ArrayList<CustomerHistory> customers = new ArrayList<CustomerHistory>();
	private NumberFormat fmt = NumberFormat.getCurrencyInstance();
	private double totalReceived = 0;
	private double totalPending = 0;
	
	public MoneyBook()
	{
		//Sets up the size, title, and visibility of the frame
		frame = new JFrame("MoneyBook");
		frame.setLayout(new BorderLayout());
		
		//Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		
		add = new JButton("Add new customer");
		add.addMouseListener(new AddNewCustomer());
		frame.add(add, BorderLayout.PAGE_START);
		
		list = new java.awt.List();
		list.addItemListener(new OpenTransactionTracker());
		frame.add(list, BorderLayout.CENTER);
		
		total = new JLabel("Total received: " + fmt.format(totalReceived) + " Total pending: " + fmt.format(totalPending));
		frame.add(total, BorderLayout.PAGE_END);
		
		refreshList();
		
		//Sets the icon image in the top left corner
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("images/Money Book.gif"));
		frame.setLocation(100, 50);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.addWindowFocusListener(new RefreshList());
	}
	
	private void refreshList()
	{
		list.removeAll();
		customers.clear();
		
		try
		{
			Scanner scan = new Scanner(customerList);
			while(scan.hasNext())
			{
				String customer = scan.nextLine();
				list.add(customer);
				customers.add(CustomerHistory.load(customer));
			}
			list.repaint();
		}
		catch(FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		}
		
		totalReceived = 0;
		totalPending = 0;
		
		for(CustomerHistory c : customers)
		{
			totalReceived += c.getTotalReceived();
			totalPending += c.getTotalPending();
			total.setText("Total received: " + fmt.format(totalReceived) + " Total pending: " + fmt.format(totalPending));
		}
	}
	
	private class AddNewCustomer extends MouseAdapter
	{
		public void mouseClicked(MouseEvent me)
		{
			String customerName = JOptionPane.showInputDialog(frame, "Enter customer name:", "Customer Name", JOptionPane.QUESTION_MESSAGE);
			CustomerHistory customer = new CustomerHistory(customerName);
			
			try
			{
				PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(customerList, true)));
				output.println(customerName);
				output.flush();
				output.close();
				customers.add(customer);
			}
			catch(IOException ioe)
			{
				JOptionPane.showMessageDialog(frame, "Error: Could not find file: Customers.txt", "Error", JOptionPane.ERROR_MESSAGE);
				ioe.printStackTrace();
			}
		}
	}
	
	private class OpenTransactionTracker implements ItemListener
	{
		public void itemStateChanged(ItemEvent ie)
		{
			String customer = list.getSelectedItem();
			TransactionTracker tt = new TransactionTracker(CustomerHistory.load(customer));
		}
	}
	
	public class RefreshList extends WindowAdapter
	{
		public void windowGainedFocus(WindowEvent event)
		{
			refreshList();
		}
	}
}