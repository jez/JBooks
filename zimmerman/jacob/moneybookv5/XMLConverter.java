/**
 * @XMLConverter.java
 *
 * Last updated: 2010/5/6
 * @author Jacob Zimmerman
 * @version 5.00 2010/6/9
 */

package zimmerman.jacob.moneybookv5;

import zimmerman.jacob.notebook.Notebook;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;

public class XMLConverter
{
	public static void toXML(String filename)
	{
		ObjectInputStream inputStream = null;
		ArrayList<Customer> customers = new ArrayList<Customer>();
		
		try
		{
			inputStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(filename))));
			int numCustomers = inputStream.readInt();
			
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
		}
		catch(ArrayIndexOutOfBoundsException aioobe)
		{
			aioobe.printStackTrace();
		}
		StringBuffer buffer = new StringBuffer(newFilename);
		char character;
		for(int i = 0; i < buffer.length(); i++)
		{
			character = buffer.charAt(i);
			if(!Character.isLetter(character))
				buffer.delete(i, i + 1);
		}
		newFilename = buffer.toString();
		
		String result = "";
		result += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		result += "<" + newFilename + ">\n";
		
		String temp = "";
		Scanner scan;
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
	
	public static void main(String[] args)
	{
		toXML("Zimm's Yard Services.mbk");
	}
}