/**
 * @Notebook.java
 *
 * Last updated: 2010/6/9
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/9
 */

package zimmerman.jacob.notebook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuShortcut;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.MessageFormat;

import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Creates a frame that has a text field and a menu. Provides methods for opening,
 * saving, and editing text files. Has many functions in common with Microsoft(r)
 * Notepad.
 */
public class Notebook
{
	//Objects used in constructor GUI
	private JFrame frame;
	private JTextArea area;
	private JScrollPane scroll;
	private JMenuBar standard;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu formatMenu;
	private JMenu fontMenu;
	private JMenu fontFamilyMenu;
	private JMenu fontStyleMenu;
	private JMenu fontColorMenu;
	private JMenu helpMenu;
	private JMenuItem current;
	private JCheckBoxMenuItem checkBoxMenuItem;

	//String objects used to name the file and save the text
	private String entry;
	private String temp;
	private String filename;
	private String filenameWithExt;
	public final static String ext = new String(".txt");

	//Objects used to read and write the file
	private PrintWriter output;
	private Scanner scan;
	private Path file;

	//Objects used to change the font
	private Font font;
	private Font tempFont;
	private Font printFont = new Font("Times New Roman", Font.PLAIN, 11);

	//Objects used to print a file
	private PrinterJob printTask;
	private PageFormat format;

	//sets up GUI and event listeners
	public Notebook()
	{
		//Sets up the size, title, and visibility of the frame
		frame = new JFrame("Notebook");
		frame.setLayout(new BorderLayout());

		filename = null;
		filenameWithExt = null;

		//Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		//Creates the area where text is edited
		area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);

		//Adds the scrollbars
		scroll = new JScrollPane(area);
		frame.add(scroll, BorderLayout.CENTER);

		//Create the menu bar that holds the menus
		standard = new JMenuBar();

		//Creates the menus
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		formatMenu = new JMenu("Format");
		fontMenu = new JMenu("Font");
		fontFamilyMenu = new JMenu("Family");
		fontStyleMenu = new JMenu("Style");
		fontColorMenu = new JMenu("Color");
		helpMenu = new JMenu("Help");

		//Adds the menus to the menu bar and displays the menu bar
		standard.add(fileMenu);
		standard.add(editMenu);
		standard.add(formatMenu);
		standard.add(helpMenu);
		frame.setJMenuBar(standard);

		//Adds font menu to format menu
		formatMenu.add(fontMenu);

		//Adds submenus to font menu
		fontMenu.add(fontFamilyMenu);
		fontMenu.add(fontStyleMenu);
		fontMenu.add(fontColorMenu);

		//Creates a menu item that opens a new frame
		current = new JMenuItem("New", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/New.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new FileMenuListener());
		fileMenu.add(current);

		//Creates a menu item that opens a file
		current = new JMenuItem("Open...", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Open.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new FileMenuListener());
		fileMenu.add(current);

		//Creates a menu item that closes a file
		current = new JMenuItem("Close");
		current.addActionListener(new FileMenuListener());
		fileMenu.add(current);

		//Horizontal bar
		fileMenu.addSeparator();

		//Creates a menu item that saves the current file based on the current filename
		current = new JMenuItem("Save", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Save.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new FileMenuListener());
		fileMenu.add(current);

		//Creates a menu item that creates a new file and saves it
		current = new JMenuItem("Save As...");
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
		current.addActionListener(new FileMenuListener());
		fileMenu.add(current);

		//Horizontal bar
		fileMenu.addSeparator();

		//Creates a menu item that prints the text file to the printer
		current = new JMenuItem("Quick Print");
		current.addActionListener(new PrintListener());
		fileMenu.add(current);

		//Creates a menu item that shows a print dialog
		current = new JMenuItem("Print...", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Print.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new PrintListener());
		fileMenu.add(current);

		//Creates a menu item that shows a print dialog
		current = new JMenuItem("Standard Print...");
		current.addActionListener(new PrintListener());
		fileMenu.add(current);

		//Horizontal bar
		fileMenu.addSeparator();

		//Creates a menu item that closes the frame
		current = new JMenuItem("Exit");
		current.addActionListener(new Exit());
		fileMenu.add(current);

		//Creates a menu item that cuts text
		current = new JMenuItem("Cut", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Cut.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new CutCopyPaste());
		editMenu.add(current);

		//Creates a menu item that copies text
		current = new JMenuItem("Copy", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Copy.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new CutCopyPaste());
		editMenu.add(current);

		//Creates a menu item that pastes text
		current = new JMenuItem("Paste", new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("images/Paste.gif")))));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new CutCopyPaste());
		editMenu.add(current);

		//Creates a menu item that wraps or doesn't wrap text
		checkBoxMenuItem = new JCheckBoxMenuItem("Word Wrap", true);
		checkBoxMenuItem.addActionListener(new SetWordWrap());
		formatMenu.add(checkBoxMenuItem);

		//Sets up font family options
		current = new JMenuItem("Times New Roman");
		current.addActionListener(new SetFamily());
		fontFamilyMenu.add(current);

		current = new JMenuItem("Arial");
		current.addActionListener(new SetFamily());
		fontFamilyMenu.add(current);

		current = new JMenuItem("Verdana");
		current.addActionListener(new SetFamily());
		fontFamilyMenu.add(current);

		current = new JMenuItem("Courier New");
		current.addActionListener(new SetFamily());
		fontFamilyMenu.add(current);

		//Sets up font style options
		current = new JMenuItem("Plain");
		current.addActionListener(new SetStyle());
		fontStyleMenu.add(current);

		current = new JMenuItem("Bold");
		current.addActionListener(new SetStyle());
		fontStyleMenu.add(current);

		current = new JMenuItem("Italic");
		current.addActionListener(new SetStyle());
		fontStyleMenu.add(current);

		current = new JMenuItem("Bold and Italic");
		current.addActionListener(new SetStyle());
		fontStyleMenu.add(current);

		//Sets up font color options
		current = new JMenuItem("Red");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Orange");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Yellow");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Green");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Blue");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Purple");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Brown");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		current = new JMenuItem("Black");
		current.addActionListener(new SetColor());
		fontColorMenu.add(current);

		//Creates a menu item that allows the user to set the size of the font
		current = new JMenuItem("Size");
		current.addActionListener(new SetSize());
		fontMenu.add(current);

		//Creates a menu item that shows the help topics
		current = new JMenuItem("Topics");
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		current.addActionListener(new Topics());
		helpMenu.add(current);

		//Creates a menu item that closes the help topics or about page
		current = new JMenuItem("Close Help");
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		current.addActionListener(new CloseHelp());
		helpMenu.add(current);

		helpMenu.addSeparator();

		//Creates a menu item that shows the about page
		current = new JMenuItem("About");
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		current.addActionListener(new About());
		helpMenu.add(current);

		//Sets the icon image in the top left corner
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Notebook.gif")));
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				int option = JOptionPane.showConfirmDialog(frame, "Do you want to save this file before closing?", "Notebook", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				switch(option)
				{
				case JOptionPane.YES_OPTION:
					save();
				case JOptionPane.NO_OPTION:
					frame.dispose();
					break;
				}
			}
		});
	}

	public void setText(String text)
	{
		entry = text;
		area.setText(entry);
	}

	/**
	 * Creates a new Notebook object, thus creating a new Notebook frame.
	 */
	public void newNotebook()
	{
		Notebook nb = new Notebook();
	}

	/**
	 * Displays an open dialog to open a file.
	 */
	public void open()
	{
		JFileChooser fc = new JFileChooser(".");
		fc.addChoosableFileFilter(new TextFilter());
		int returnVal = fc.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile().toPath();
			try
			{
				filenameWithExt = file.getName(file.getNameCount()-1).toString();
				scan = new Scanner(file);
				entry = "";
				while(scan.hasNext())
				{
					entry += scan.nextLine() + "\n";
				}
				scan.close();
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(frame, "ERROR: " + e.getMessage());
			}
			catch(NoSuchElementException e)
			{
				JOptionPane.showMessageDialog(frame, "ERROR: " + e.getMessage());
			}
		}

		area.setText(entry);
		frame.setTitle(filenameWithExt + " - Notebook");
	}

	/**
	 * Opens <code>file</code>.
	 * @param file the file to be opened.
	 */
	public void open(Path file)
	{
		try
		{
			filenameWithExt = file.getName(file.getNameCount()-1).toString();
			scan = new Scanner(file);
			entry = "";
			while(scan.hasNext())
			{
				entry += scan.nextLine() + "\n";
			}
			scan.close();
		}
		catch(IOException e)
		{
			JOptionPane.showMessageDialog(frame, "ERROR: " + e.getMessage());
		}
		catch(NoSuchElementException e)
		{
			JOptionPane.showMessageDialog(frame, "ERROR: " + e.getMessage());
		}

		area.setText(entry);
		frame.setTitle(filenameWithExt + " - Notebook");
	}

	/**
	 * Closes the current file and keeps this window open.
	 */
	public void close()
	{
		filenameWithExt = null;
		filename = null;
		entry = null;
		area.setText(entry);
		frame.setTitle("Notebook");
	}

	/**
	 * Saves the current file under the current filename.
	 */
	public void save()
	{
		try
		{
			//Uses the current value in filenameWithExt to save the file
			output = new PrintWriter(Files.newBufferedWriter(file, Charset.forName("ISO-LATIN-1")));
			entry = area.getText();
			scan = new Scanner(entry);
			while(scan.hasNext())
			{
				output.println(scan.nextLine());
			}
			//output.write(entry);
			output.close();
		}
		catch(IOException e)
		{
			//Displays the error
			JOptionPane.showMessageDialog(frame, "ERROR: \"" + filenameWithExt + "\" is invalid.");
		}
		catch(NullPointerException e)
		{
			saveAs();
		}
	}

	/**
	 * Displays a save dialog to save a file as the name returned by the dialog.
	 */
	public void saveAs()
	{
		JFileChooser fc = new JFileChooser(".");
		fc.addChoosableFileFilter(new TextFilter());
		int returnVal = fc.showSaveDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			file = fc.getSelectedFile().toPath();
			try
			{
				if (file.getName(file.getNameCount()-1).toString().indexOf(".") == -1)
				{
					filenameWithExt = file.getName(file.getNameCount()-1) + ext;
					file = Paths.get(System.getProperty("user.dir"), filenameWithExt);
					Files.deleteIfExists(file);
					Files.createFile(file);
				}
				else
				{
					filenameWithExt = file.getName(file.getNameCount()-1).toString();
					Files.deleteIfExists(file);
					Files.createFile(file);
				}

				//Creates a new file based on the variable filenameWithExt and writes the data to it
				output = new PrintWriter(Files.newBufferedWriter(file, Charset.forName("ISO-LATIN-1")));
				entry = area.getText();
				scan = new Scanner(entry);
				while(scan.hasNext())
				{
					output.println(scan.nextLine());
				}
				//output.write(entry);
				output.close();
			}
			catch(IOException e)
			{
				//Displays the error
				JOptionPane.showMessageDialog(frame, "ERROR: \"" + filenameWithExt + "\" is invalid.");
			}
		}
	}

	/**
	 * Saves a the current file to <code>file</code>.
	 * @param file the file to which to save.
	 */
	public void saveAs(Path file)
	{
		try
		{
			filenameWithExt = file.getName(file.getNameCount()-1).toString();
			//Creates a new file based on the variable filenameWithExt and writes the data to it
			output = new PrintWriter(Files.newBufferedWriter(file, Charset.forName("UTF-8")));
			entry = area.getText();
			scan = new Scanner(entry);
			while(scan.hasNext())
				output.println(scan.nextLine());

			output.close();
		}
		catch(IOException e)
		{
			//Displays the error
			JOptionPane.showMessageDialog(frame, "ERROR: \"" + filenameWithExt + "\" is invalid.");
		}
	}

	/**
	 * Exits the program
	 */
	public void exit()
	{
		frame.dispose();
	}

	/**
	 * Prints a file.
	 * @param showPrintDialog if true, will show the print dialog
	 */
	private void print(boolean showPrintDialog)
	{
		try
		{
			try
			{
				//Prints the document
				boolean complete = area.print(null, new MessageFormat(filenameWithExt + " Page : {0}"), showPrintDialog, null, null, true);
				if (complete)
					JOptionPane.showMessageDialog(frame, "Print successful");
				else
					JOptionPane.showMessageDialog(frame, "Print canceled");
			}
			catch(NullPointerException e){}
		}
		catch (PrinterException e)
		{
			JOptionPane.showMessageDialog(frame, "Print failed");
		}
	}

	//Calls either new, open, close, save, or saveAs
	private class FileMenuListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = ((JMenuItem) event.getSource()).getText();
			if(text.equals("New"))
				newNotebook();
			else if(text.equals("Open..."))
				open();
			else if(text.equals("Close"))
				close();
			else if(text.equals("Save"))
				save();
			else if(text.equals("Save As..."))
				saveAs();
		}
	}
	//Either calls print(true), sets a standard font and then calls print(true), or calls print(false)
	private class PrintListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = ((JMenuItem) event.getSource()).getText();
			if(text.equals("Print..."))
				print(true);
			else if(text.equals("SPrint..."))
			{
				//Sets the current font settings and temporarily saves the current font settings
				font = area.getFont();
				area.setFont(printFont);
				print(true);
				//Resets the old font settings
				area.setFont(font);
			}
			else if(text.equals("Quick Print"))
				print(false);
		}
	}
	//Exits the program, thus closing all active windows
	private class Exit implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			int option = JOptionPane.showConfirmDialog(frame, "Do you want to save this file before closing?", "Notebook", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			switch(option)
			{
			case JOptionPane.YES_OPTION:
				saveAs();
			case JOptionPane.NO_OPTION:
				frame.dispose();
				break;
			}
		}
	}
	//Either cuts, copies, or pastes the selected text
	private class CutCopyPaste implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = ((JMenuItem) event.getSource()).getText();

			if(text.equals("Cut"))
				area.cut();
			else if(text.equals("Copy"))
				area.copy();
			else if(text.equals("Paste"))
				area.paste();
		}
	}
	//Turns on or off word wrap
	private class SetWordWrap implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			area.setLineWrap(checkBoxMenuItem.getState());
		}
	}
	//Sets the font family
	private class SetFamily implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = event.getActionCommand();
			tempFont = area.getFont();
			area.setFont(new Font(text, tempFont.getStyle(), tempFont.getSize()));
			tempFont = null;
		}
	}
	//Sets the font style
	private class SetStyle implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//Determines which menu item was pressed
			String text = event.getActionCommand();
			tempFont = area.getFont();

			//Does a different action depending on which menu item was pressed
			if(text.equals("Plain"))
				area.setFont(new Font(tempFont.getFamily(), Font.PLAIN, tempFont.getSize()));
			else if(text.equals("Bold"))
				area.setFont(new Font(tempFont.getFamily(), Font.BOLD, tempFont.getSize()));
			else if(text.equals("Italic"))
				area.setFont(new Font(tempFont.getFamily(), Font.ITALIC, tempFont.getSize()));
			else if(text.equals("Bold and Italic"))
				area.setFont(new Font(tempFont.getFamily(), Font.BOLD | Font.ITALIC, tempFont.getSize()));
			tempFont = null;
		}
	}
	//Sets the font color
	private class SetColor implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//Determines which menu item was pressed
			String text = event.getActionCommand();

			//Does a different action depending on which menu item was pressed
			if(text.equals("Red"))
				area.setForeground(Color.RED);
			else if(text.equals("Orange"))
				area.setForeground(Color.ORANGE);
			else if(text.equals("Yellow"))
				area.setForeground(Color.YELLOW);
			else if(text.equals("Green"))
				area.setForeground(Color.GREEN);
			else if(text.equals("Blue"))
				area.setForeground(Color.BLUE);
			else if(text.equals("Purple"))
				area.setForeground(new Color(255, 0, 255));
			else if(text.equals("Brown"))
				area.setForeground(new Color(128, 64, 0));
			else if(text.equals("Black"))
				area.setForeground(Color.BLACK);
		}
	}
	//Sets the size of the text with an input dialog box
	private class SetSize implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = JOptionPane.showInputDialog(frame, "Size:");
			tempFont = area.getFont();
			try
			{
				area.setFont(new Font(tempFont.getFamily(), tempFont.getStyle(), Integer.parseInt(text)));
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(frame, "ERROR: Invalid font size: " + text + ".");
			}
			tempFont = null;
		}
	}
	//Shows the help topics
	private class Topics implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			//gets the current text
			temp = new String(area.getText());

			//displays help topics
			entry =  "Notepad Help Topics\n";
			entry += "I. File\n";
			entry += "  1. New\n";
			entry += "    - Opens a new window with a blank file.\n";
			entry += "  2. Open\n";
			entry += "    - Opens a prexisting file.\n";
			entry += "  3. Close\n";
			entry += "    - Closes an open file.\n";
			entry += "  4. Save\n";
			entry += "    - Saves the open file.\n";
			entry += "  5. Save As\n";
			entry += "    - Saves the current file as a new file.\n";
			entry += "  6. Quick Print\n";
			entry += "    - Prints one copy of the file to the default printer.\n";
			entry += "  7. Print\n";
			entry += "    - Shows the print dialog, which can change the number of copies, printer, etc.\n";
			entry += "  8. Exit\n";
			entry += "    - Exits the program.\n";
			entry += "II. Edit\n";
			entry += "  1. Cut\n";
			entry += "    - Cuts the selected text from the document.\n";
			entry += "  2. Copy\n";
			entry += "    - Copies the selected text in the document.\n";
			entry += "  3. Paste\n";
			entry += "    - Pastes the selected text into the document.\n";
			entry += "III. Font.\n";
			entry += "  1. Famliy\n";
			entry += "    - Sets the font family to the selected option. Ex: Times New Roman.\n";
			entry += "  2. Style\n";
			entry += "    - Sets the font style to the selected option. Ex: Bold.\n";
			entry += "  3. Size\n";
			entry += "    - Sets the font family to the given input. Ex: 20.\n";
			entry += "  4. Color\n";
			entry += "    - Sets the font color to the selected option. Ex: Red.\n";
			entry += "IV. Help\n";
			entry += "  1. Topics\n";
			entry += "    - Shows the help topics.\n";
			entry += "  2. Close Help\n";
			entry += "    - Reverts the text back to what it was before opening the help topics or about page.\n";
			entry += "  3. About\n";
			entry += "    - Shows the about information of the program.\n";
			area.setText(entry);
		}
	}
	//Closes the help topics or about page
	private class CloseHelp implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				//resets old text
				entry = temp;
				area.setText(entry);
				temp = null;
			}
			catch(NullPointerException e)
			{
			}
		}
	}
	//Displays the about information
	private class About implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			temp = new String(area.getText());
			entry =  "About\n";
			entry += "\n";
			entry += "Notepad - v5.0\n";
			entry += "Author: Jacob Zimmerman\n";
			entry += "Last updated: June 9, 2010\n";
			area.setText(entry);
		}
	}
}