/**
 * @EType.java
 *
 * Defines the different types of expenses that can be entered.
 *
 * @author Jacob Zimmerman
 * @version 9.00 3 Apr 2012
 */

package zimmerman.jacob.moneybookv9;

public enum EType
{
	GAS                      ("Gas"),
	OIL                      ("Oil"),
	REPAIR                   ("Repair"),
	NEW_PART                 ("New part"),
	SUPPLIES                 ("Supplies");

	//Used when making the toString method
	private String name;

	/**
	 * Sets up the EType with the specified string, as listed above
	 *
	 * @param name the string representation of the enum
	 */
	EType(String name)
	{
		this.name = name;
	}

	/**
	 * Static method that returns the enum type of the specified string that represents the enum value
	 *
	 * @param name the string value of the enum to be parsed
	 *
	 * @reutrn the parsed EType enum value
	 */
	public static EType parse(String name)
	{
		EType type;

		switch(name)
		{
		case "Gas":
			type = Enum.valueOf(EType.class, "GAS");
			break;
		case "Oil":
			type = Enum.valueOf(EType.class, "OIL");
			break;
		case "Repair":
			type = Enum.valueOf(EType.class, "REPAIR");
			break;
		case "New part":
			type = Enum.valueOf(EType.class, "NEW_PART");
			break;
		case "Supplies":
			type = Enum.valueOf(EType.class, "SUPPLIES");
		default:
			type = null;
			break;
		}

		return type;
	}

	/**
	 * @return the string representation of a particular enum value as determined by the string value name
	 */
	public String toString()
	{
		return name;
	}
}
