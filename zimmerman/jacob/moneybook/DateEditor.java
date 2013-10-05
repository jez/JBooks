/**
 * @DateEditor.java
 *
 * Last updated: 2010/6/6
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/6
 */

package zimmerman.jacob.moneybook;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

public class DateEditor extends PropertyEditorSupport
{
	private JFormattedTextField dayField = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField monthField = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField yearField = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JPanel panel = new JPanel();
	
	public DateEditor()
	{
		dayField.setColumns(5);
		monthField.setColumns(5);
		yearField.setColumns(5);
		
		panel.add(monthField);
		panel.add(dayField);
		panel.add(yearField);
	}
	
	public Object getValue()
	{
		int day = ((Number) dayField.getValue()).intValue();
		int month = ((Number) monthField.getValue()).intValue();
		int year = ((Number) yearField.getValue()).intValue();
		return new Date(day, month, year);
	}
	
	public void setValue(Object newValue)
	{
		Date date = (Date) newValue;
			
		dayField.setValue(new Integer(date.getDay()));
		monthField.setValue(new Integer(date.getMonth()));
		yearField.setValue(new Integer(date.getYear()));
	}
	
	public boolean supportsCustomEditor()
	{
		return true;
	}
	
	public Component getCustomEditor()
	{
		return panel;
	}
}