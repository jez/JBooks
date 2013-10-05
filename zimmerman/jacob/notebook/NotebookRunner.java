/**
 * @MoneyBookRunner.java
 *
 * Last updated: 2010/6/10
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/10
 */

import zimmerman.jacob.notebook.Notebook;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

public class NotebookRunner
{
	public static void main(String[] args)
	{
		Notebook nb = new Notebook();

		//If there is an argument, uses it to open a text file
		if(args.length != 0 && args[0].indexOf(Notebook.ext) != -1)
		{
			try
			{
				nb.open(Paths.get(args[0]));
			}
			catch(InvalidPathException ipe)
			{
				JOptionPane.showMessageDialog(null, ipe.getMessage(), "Error Opening File", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}
}