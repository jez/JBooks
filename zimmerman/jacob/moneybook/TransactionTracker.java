/**
 * @Notebook.java
 *
 * Last updated: 2010/6/5
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/5
 */

package zimmerman.jacob.moneybook;

import zimmerman.jacob.notebook.Notebook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.MessageFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
 
public class TransactionTracker
{
	//Objects used in constructor an GUI
	private JFrame frame;
	private JTextArea area;
	private JScrollPane scroll;
	private JMenuBar standard;
	private JMenu fileMenu;
	private JMenuItem current;
	
	//String objects used to name the file and save the text
	private String entry;
	private String temp;
	private String filename;
	private String filenameWithExt;
	private String ext = new String(".cst");
	private CustomerHistory customer;
		
	//sets up GUI and event listeners
	public TransactionTracker(CustomerHistory customer)
	{
		frame = new JFrame(customer.getCustomer() + " - Transaction Tracker");
		frame.setLayout(new BorderLayout());
		this.customer = customer;
		
		//Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		
		//Creates the area that
		area = new JTextArea(customer.toString());
		area.setEditable(false);
		
		//Adds the scrollbars
		scroll = new JScrollPane(area);
		frame.add(scroll, BorderLayout.CENTER);
		
		//Create the JMenu bar that holds the menus
		standard = new JMenuBar();
		
		//Creates the menus
		fileMenu = new JMenu("File");
		
		//Adds the file JMenu to the JMenu bar and displays the JMenu bar
		standard.add(fileMenu);
		frame.setJMenuBar(standard);
		
		current = new JMenuItem("Add Transaction...", new ImageIcon("images/Add Transaction.gif"));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new TransactionListener());
		fileMenu.add(current);
		
		current = new JMenuItem("Remove Transaction...", new ImageIcon("images/Remove Transaction.gif"));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new TransactionListener());
		fileMenu.add(current);
		
		current = new JMenuItem("Edit Transaction...", new ImageIcon("images/Edit Transaction.gif"));
		current.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		current.addActionListener(new TransactionListener());
		fileMenu.add(current);
		
		//Horizontal bar
		fileMenu.addSeparator();
		
		current = new JMenuItem("Export to Notebook...", new ImageIcon("images/Export.gif"));
		current.addActionListener(new ExportListener());
		fileMenu.add(current);
		
		//Horizontal bar
		fileMenu.addSeparator();
		
		//Creates a JMenu item that is can be used to close the frame
		current = new JMenuItem("Exit");
		current.addActionListener(new ExitListener());
		fileMenu.add(current);
		
		//Sets up the size, title, and visibility of the frame
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("images/Money Book.gif")));
		frame.setLocation(100, 50);
		frame.setPreferredSize(new Dimension(800, 600));
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				frame.dispose();
			}
		});
	}
	
	public void addTransaction()
	{
		Class[] types = {Date.class, Date.class, String.class, double.class, double.class};
		
		Object[] values = new Object[types.length];
		values[0] = new Date(1, 6, 2010);
		values[1] = new Date(1, 6, 2010);
		values[2] = new String();
		values[3] = new Double(0.0);
		values[4] = new Double(0.0);
		
		TransactionPropertySheet sheet = new TransactionPropertySheet(values);
		JOptionPane.showMessageDialog(frame, sheet, "Transaction Parameters", JOptionPane.QUESTION_MESSAGE);
		values = sheet.getValues();
		
		customer.add(new Transaction((Date) values[0], (Date) values[1], (String) values[2],((Double) values[3]).doubleValue(), ((Double) values[4]).doubleValue()));
	}
	
	public void addTransaction(Transaction transaction)
	{
		customer.add(transaction);
	}
	
	public Transaction removeTransaction()
	{
		try
		{
			String indexStr = JOptionPane.showInputDialog(frame, "Enter the entry number you want to remove:", "Remove Parameters", JOptionPane.QUESTION_MESSAGE);
			int index = Integer.parseInt(indexStr);
			return customer.remove(index);
		}
		catch(NumberFormatException e)
		{
			return null;
		}
	}
	
	public Transaction removeTransaction(int index)
	{
		return customer.remove(index);
	}
	
	public void editTransaction()
	{
		try
		{
			int option = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter the entry number you want to edit: ", "Edit Parameters", JOptionPane.QUESTION_MESSAGE));
			Transaction temp = removeTransaction(option);
			
			Class[] types = {Date.class, Date.class, String.class, double.class, double.class};
			
			Object[] values = new Object[types.length];
			values[0] = temp.getStartDate();
			values[1] = temp.getEndDate();
			values[2] = temp.getMessage();
			values[3] = temp.getMoneyReceived();
			values[4] = temp.getMoneyPending();
			
			TransactionPropertySheet sheet = new TransactionPropertySheet(values);
			JOptionPane.showMessageDialog(frame, sheet, "Transaction Parameters", JOptionPane.QUESTION_MESSAGE);
			values = sheet.getValues();
			
			customer.add(new Transaction((Date) values[0], (Date) values[1], (String) values[2],((Double) values[3]).doubleValue(), ((Double) values[4]).doubleValue()));
		}
		catch(NumberFormatException e){}
	}
	
	private class TransactionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			String text = ((JMenuItem) event.getSource()).getText();
			
			if(text.equals("Add Transaction..."))
				addTransaction();
			else if(text.equals("Remove Transaction..."))
				removeTransaction();
			else if(text.equals("Edit Transaction..."))
				editTransaction();
			
			area.setText(customer.toString());
		}
	}
	private class ExitListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			frame.dispose();
		}
	}
	private class ExportListener implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			Notebook nb = new Notebook();
			File file = new File(customer.getCustomer() + ".txt");
			nb.setText(customer.toString());
			nb.saveAs(file);
			nb.open(file);
		}
	}
}

/**
 * Modified from Cay Hortsmann's PropertySheet class in MenuMaker.java from the 
 * AP(r) Computer Science GridWorld Case Study
 */
class TransactionPropertySheet extends JPanel
{
	public TransactionPropertySheet(Object[] values)
	{
		JLabel label;
		setLayout(new GridLayout(5, 2));
		editors = new PropertyEditor[5];
		
		label = new JLabel("Start Date");
		add(label);
		editors[0] = new DateEditor();
		editors[0].setValue(values[0]);
		add(getEditorComponent(editors[0]));
		
		label = new JLabel("End Date");
		add(label);
		editors[1] = new DateEditor();
		editors[1].setValue(values[1]);
		add(getEditorComponent(editors[1]));
		
		label = new JLabel("Message");
		add(label);
		editors[2] = getEditor(String.class);
		editors[2].setValue(values[2]);
		add(getEditorComponent(editors[2]));
		
		label = new JLabel("Money Received");
		add(label);
		editors[3] = getEditor(double.class);
		editors[3].setValue(values[3]);
		add(getEditorComponent(editors[3]));
		
		label = new JLabel("Money Pending");
		add(label);
		editors[4] = getEditor(double.class);
		editors[4].setValue(values[4]);
		add(getEditorComponent(editors[4]));
	}

    /**
     * Gets the property editor for a given property, and wires it so that it
     * updates the given object.
     * @param bean the object whose properties are being edited
     * @param descriptor the descriptor of the property to be edited
     * @return a property editor that edits the property with the given
     * descriptor and updates the given object
     */
	public PropertyEditor getEditor(Class type)
	{
		PropertyEditor editor;
		editor = defaultEditors.get(type);
		if (editor != null)
			return editor;
		editor = PropertyEditorManager.findEditor(type);
		return editor;
	}

    /**
     * Wraps a property editor into a component.
     * @param editor the editor to wrap
     * @return a button (if there is a custom editor), combo box (if the editor
     * has tags), or text field (otherwise)
     */
    public Component getEditorComponent(final PropertyEditor editor)
    {
        String[] tags = editor.getTags();
        String text = editor.getAsText();
        if (editor.supportsCustomEditor())
        {
            return editor.getCustomEditor();
        }
        else if (tags != null)
        {
            // make a combo box that shows all tags
            final JComboBox comboBox = new JComboBox(tags);
            comboBox.setSelectedItem(text);
            comboBox.addItemListener(new ItemListener()
            {
                public void itemStateChanged(ItemEvent event)
                {
                    if (event.getStateChange() == ItemEvent.SELECTED)
                        editor.setAsText((String) comboBox.getSelectedItem());
                }
            });
            return comboBox;
        }
        else
        {
            final JTextField textField = new JTextField(text, 10);
            textField.getDocument().addDocumentListener(new DocumentListener()
            {
                public void insertUpdate(DocumentEvent e)
                {
                    try
                    {
                        editor.setAsText(textField.getText());
                    }
                    catch (IllegalArgumentException exception)
                    {
                    }
                }

                public void removeUpdate(DocumentEvent e)
                {
                    try
                    {
                        editor.setAsText(textField.getText());
                    }
                    catch (IllegalArgumentException exception)
                    {
                    }
                }

                public void changedUpdate(DocumentEvent e)
                {
                }
            });
            return textField;
        }
    }

    public Object[] getValues()
    {
        for (int i = 0; i < editors.length; i++)
            if (editors[i] != null)
                values[i] = editors[i].getValue();
        return values;
    }

    private PropertyEditor[] editors;
    private Object[] values = new Object[5];;

    private static Map<Class, PropertyEditor> defaultEditors;

    // workaround for Web Start bug
    public static class StringEditor extends PropertyEditorSupport
    {
        public String getAsText()
        {
            return (String) getValue();
        }

        public void setAsText(String s)
        {
            setValue(s);
        }
    }

    static
    {
        defaultEditors = new HashMap<Class, PropertyEditor>();
        defaultEditors.put(String.class, new StringEditor());
        defaultEditors.put(Date.class, new DateEditor());
    }
}