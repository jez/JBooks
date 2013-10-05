/**
 * @XMLFileFilter.java
 *
 *
 * @author Jacob Zimmerman
 * @version 9.00 Apr 9 2012
 */

package zimmerman.jacob.moneybookv9;

public class XMLFileFilter extends javax.swing.filechooser.FileFilter
{
	public String getDescription()
	{
		return "XML Files";
	}

	public boolean accept(java.io.File f)
	{
		if(f.isDirectory())
			return true;

		String s = f.getName();
		int i = s.lastIndexOf('.');

		String ext = "";

		if (i > 0 &&  i < s.length() - 1)
		{
			ext = s.substring(i+1).toLowerCase();
		}

		if(ext.equals("xml"))
			return true;
		else
			return false;
	}
}