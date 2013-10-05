/**
 * @XMLConverter.java
 *
 * Last updated: 2010/5/6
 * @author Jacob Zimmerman
 * @version 5.00 2010/6/9
 */

package zimmerman.jacob.moneybookv6;

import zimmerman.jacob.notebook.Notebook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import java.text.NumberFormat;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class XMLConverter
{
	public static void toXML(String filename)
	{
		ObjectInputStream inputStream = null;
		ExpenseList expenseList = null;
		ArrayList<Customer> customers = new ArrayList<Customer>();
		double totalEarned = 0;
		double totalPending = 0;
		double totalCredit = 0;
		
		try
		{
			inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(filename))));
			int numCustomers = inputStream.readInt();
			
			expenseList = (ExpenseList) inputStream.readObject();
			
			for(int i = 0; i < numCustomers; i++)
			{
				customers.add((Customer) inputStream.readObject());
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
		
		String newFilename = "";
		try
		{
			newFilename = filename.substring(0, filename.indexOf("."));
			newFilename = newFilename.replaceAll("[\\W]", "");
		}
		catch(ArrayIndexOutOfBoundsException aioobe)
		{
			aioobe.printStackTrace();
		}
		
		String result = "";
		result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		result += "<" + newFilename + ">\n";
		
		String temp = "";
		Scanner scan;
		
		temp = expenseList.getXML();
		scan = new Scanner(temp);
		while(scan.hasNext())
		{
			result += "\t" + scan.nextLine() + "\n";
		}
		
		for(Customer c : customers)
		{
			temp = c.getXML();
			scan = new Scanner(temp);
			
			while(scan.hasNext())
			{
				result += "\t" + scan.nextLine() + "\n";
			}
		}
		
		result += "</" + newFilename + ">";
		
		PrintWriter output = null;
		
		try
		{
			File file = new File(newFilename + ".xml");
			file.createNewFile();
		
			output = new PrintWriter(new BufferedWriter(new FileWriter(file)));
			scan = new Scanner(result);
			while(scan.hasNext())
			{
				output.println(scan.nextLine());
			}
			
			output.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	public static void fromXML(String nameForFile)
	{
		ObjectOutputStream outputStream = null;
		NumberFormat fmt = NumberFormat.getCurrencyInstance();
		
		String xmlFilename = nameForFile + ".xml";
		String mbkFilename = nameForFile + ".mbk";
		
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
		ExpenseList expenseList = new ExpenseList();
		ArrayList<Customer> customers = new ArrayList<Customer>();
		int numCustomers = 0;
		
		try 
		{
			File file = new File(xmlFilename);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			
			doc.getDocumentElement().normalize();
			
			NodeList nodeExpenseList = doc.getElementsByTagName("expense");
			
			for(int i = 0; i < nodeExpenseList.getLength(); i++)
			{
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
			
			numCustomers = customers.size();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		try
		{
			outputStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(mbkFilename)));
			
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
	}
	
	public static void main(String[] args)
	{
		fromXML("Zimm's Yard Services");
	}
}