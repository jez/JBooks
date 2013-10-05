/**
 * @TType.java
 *
 * Defines the different types of transactions that can occur.
 *
 * @author Jacob Zimmerman
 * @version 10.00 25 May 2012
 */

package zimmerman.jacob.moneybookv10;

public enum TType
{
	GRASS_CUT        ("Grass cut"),
	PAYMENT          ("Payment"),
	TRIM_EDGES       ("Trim edges"),
	SWEEP_GRASS      ("Sweep grass"),
	WATER_PLANTS     ("Water plants"),
	SPREAD_MULCH     ("Spread mulch"),
	SPREAD_TOPSOIL   ("Spread topsoil"),
	SPREAD_STONE     ("Spread stone"),
	GENERAL_LABOR    ("General labor"),
	WALK_DOG         ("Walk dog"),
	TIP              ("Tip");

	//Used when returning the strong representation of this name
	private String name;

	/**
	 * Initializes the transaction types so that they all have the specified string as represented above in the private name variable
	 *
	 * @param name the name variable as specified above
	 */
	TType(String name)
	{
		this.name = name;
	}

	/**
	 * Static method that converts a specified string into the respective enum transaction value
	 *
	 * @param name the string to be parsed
	 *
	 * @returns the TType value of the specified string
	 */
	public static TType parse(String name)
	{
		TType type;

		switch(name)
		{
		case "Grass cut":
			type = Enum.valueOf(TType.class, "GRASS_CUT");
			break;
		case "Payment":
			type = Enum.valueOf(TType.class, "PAYMENT");
			break;
		case "Trim edges":
			type = Enum.valueOf(TType.class, "TRIM_EDGES");
			break;
		case "Sweep grass":
			type = Enum.valueOf(TType.class, "SWEEP_GRASS");
			break;
		case "Water plants":
			type = Enum.valueOf(TType.class, "WATER_PLANTS");
			break;
		case "Spread mulch":
			type = Enum.valueOf(TType.class, "SPREAD_MULCH");
			break;
		case "Spread topsoil":
			type = Enum.valueOf(TType.class, "SPREAD_TOPSOIL");
			break;
		case "Spread stone":
			type = Enum.valueOf(TType.class, "SPREAD_STONE");
			break;
		case "General labor":
			type = Enum.valueOf(TType.class, "GENERAL_LABOR");
			break;
		case "Walk dog":
			type = Enum.valueOf(TType.class, "WALK_DOG");
			break;
		case "Tip":
			type = Enum.valueOf(TType.class, "TIP");
			break;
		default:
			type = null;
			break;
		}

		return type;
	}

	/**
	 * Returns the string representation of this particular TType
	 *
	 * @returns the string as specified by the name value
	 */
	public String toString()
	{
		return name;
	}
}