/**
 * @TextFilter.java
 *
 * Last updated: 2010/6/9
 * @author Jacob Zimmerman
 * @version 1.00 2010/6/9
 */

package zimmerman.jacob.notebook;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Filters out all files that are not text files in a JFileChoose object. 
 */
public class TextFilter extends FileFilter
{
	/**
	 * Determines whether a file is filtered out according to this filter
	 *
	 * @param file the file to filter
	 */
	public boolean accept(File file)
	{
		//Always return true for diretories for navigation of folders
		if(file.isDirectory())
			return true;
		else
		{
			String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
			return extension.equals("txt");
		}
	}
	
	/**
	 * Returns the description of what files this FileFilter filters.
	 *
	 * @return a description of this TextFilter
	 */
	public String getDescription()
	{
		return "Text Documents (*.txt)";
	}
}