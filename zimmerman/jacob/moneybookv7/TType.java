/**
 * @TType.java
 *
 * 
 * @author Jacob Zimmerman
 * @version 7.00 2011/7/06
 */

package zimmerman.jacob.moneybookv7;

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
		
		if(name.equals(Enum.valueOf(TType.class, "GRASS_CUT").toString()))
			type = Enum.valueOf(TType.class, "GRASS_CUT");
		else if(name.equals(Enum.valueOf(TType.class, "PAYMENT").toString()))
			type = Enum.valueOf(TType.class, "PAYMENT");
		else if(name.equals(Enum.valueOf(TType.class, "GRASS_CUT_HALF_OFF").toString()))
			type = Enum.valueOf(TType.class, "GRASS_CUT_HALF_OFF");
		else if(name.equals(Enum.valueOf(TType.class, "GRASS_CUT_WITH_WEEDWHACK").toString()))
			type = Enum.valueOf(TType.class, "GRASS_CUT_WITH_WEEDWHACK");
		else if(name.equals(Enum.valueOf(TType.class, "WATER_PLANTS").toString()))
			type = Enum.valueOf(TType.class, "WATER_PLANTS");
		else if(name.equals(Enum.valueOf(TType.class, "SPREAD_MULCH").toString()))
			type = Enum.valueOf(TType.class, "SPREAD_MULCH");
		else if(name.equals(Enum.valueOf(TType.class, "SPREAD_TOPSOIL").toString()))
			type = Enum.valueOf(TType.class, "SPREAD_TOPSOIL");
		else if(name.equals(Enum.valueOf(TType.class, "SPREAD_STONE").toString()))
			type = Enum.valueOf(TType.class, "SPREAD_STONE");
		else if(name.equals(Enum.valueOf(TType.class, "GENERAL_LABOR").toString()))
			type = Enum.valueOf(TType.class, "GENERAL_LABOR");
		else if(name.equals(Enum.valueOf(TType.class, "WALK_DOG").toString()))
			type = Enum.valueOf(TType.class, "WALK_DOG");
		else if(name.equals(Enum.valueOf(TType.class, "TIP").toString()))
			type = Enum.valueOf(TType.class, "TIP");
		else
			type = null;
		
		return type;
	}
	
	public String toString()
	{
		return name;
	}
}