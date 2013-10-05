/**
 * @EType.java
 *
 * Last updated: 2011/6/1
 * @author Jacob Zimmerman
 * @version 1.00 2011/6/1
 */

package zimmerman.jacob.moneybookv6;

public enum EType
{
	GAS                      ("Gas"),
	OIL                      ("Oil"),
	REPAIR                   ("Repair"),
	NEW_PART                 ("New part");
	
	private String name;
	
	EType(String name)
	{
		this.name = name;
	}
	
	public static EType parse(String name)
	{
		EType type;
		
		if(name.equals(Enum.valueOf(EType.class, "GAS").toString()))
			type = Enum.valueOf(EType.class, "GAS");
		else if(name.equals(Enum.valueOf(EType.class, "OIL").toString()))
			type = Enum.valueOf(EType.class, "OIL");
		else if(name.equals(Enum.valueOf(EType.class, "REPAIR").toString()))
			type = Enum.valueOf(EType.class, "REPAIR");
		else if(name.equals(Enum.valueOf(EType.class, "NEW_PART").toString()))
			type = Enum.valueOf(EType.class, "NEW_PART");
		else
			type = null;
		
		return type;
	}
	
	public String toString()
	{
		return name;
	}
}
