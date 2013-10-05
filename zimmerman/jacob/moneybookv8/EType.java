/**
 * @EType.java
 *
 *
 * @author Jacob Zimmerman
 * @version 8.00 3 Sep 2011
 */

package zimmerman.jacob.moneybookv8;

public enum EType
{
	GAS                      ("Gas"),
	OIL                      ("Oil"),
	REPAIR                   ("Repair"),
	NEW_PART                 ("New part"),
	SUPPLIES                 ("Supplies");

	private String name;

	EType(String name)
	{
		this.name = name;
	}

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
		case "New Part":
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

	public String toString()
	{
		return name;
	}
}
