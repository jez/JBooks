/**
 * @TType.java
 *
 *
 * @author Jacob Zimmerman
 * @version 8.00 3 Sep 2011
 */

package zimmerman.jacob.moneybookv8;

public enum TType
{
	GRASS_CUT                ("Grass cut"),
	PAYMENT                  ("Payment"),
	GRASS_CUT_HALF_OFF       ("Grass cut half off"),
	GRASS_CUT_WITH_WEEDWHACK ("Grass cut with weedwhack"),
	WATER_PLANTS             ("Water plants"),
	SPREAD_MULCH             ("Spread mulch"),
	SPREAD_TOPSOIL           ("Spread topsoil"),
	SPREAD_STONE             ("Spread stone"),
	GENERAL_LABOR            ("General labor"),
	WALK_DOG                 ("Walk dog"),
	TIP                      ("Tip");

	private String name;

	TType(String name)
	{
		this.name = name;
	}

	public static TType parse(String name)
	{
		TType type;

		switch(name)
		{
		case "GRASS_CUT":
			type = Enum.valueOf(TType.class, "GRASS_CUT");
			break;
		case "PAYMENT":
			type = Enum.valueOf(TType.class, "PAYMENT");
			break;
		case "GRASS_CUT_HALF_OFF":
			type = Enum.valueOf(TType.class, "GRASS_CUT_HALF_OFF");
			break;
		case "GRASS_CUT_WITH_WEEDWHACK":
			type = Enum.valueOf(TType.class, "GRASS_CUT_WITH_WEEDWHACK");
			break;
		case "WATER_PLANTS":
			type = Enum.valueOf(TType.class, "WATER_PLANTS");
			break;
		case "SPREAD_MULCH":
			type = Enum.valueOf(TType.class, "SPREAD_MULCH");
			break;
		case "SPREAD_TOPSOIL":
			type = Enum.valueOf(TType.class, "SPREAD_TOPSOIL");
			break;
		case "SPREAD_STONE":
			type = Enum.valueOf(TType.class, "SPREAD_STONE");
			break;
		case "GENERAL_LABOR":
			type = Enum.valueOf(TType.class, "GENERAL_LABOR");
			break;
		case "WALK_DOG":
			type = Enum.valueOf(TType.class, "WALK_DOG");
			break;
		case "TIP":
			type = Enum.valueOf(TType.class, "TIP");
			break;
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