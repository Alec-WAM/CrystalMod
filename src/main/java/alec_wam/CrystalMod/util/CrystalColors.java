package alec_wam.CrystalMod.util;

import java.util.Random;

import alec_wam.CrystalMod.util.IEnumMeta;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public class CrystalColors {
	public static final PropertyEnum<Basic> COLOR_BASIC = PropertyEnum.<Basic>create("color", Basic.class);
	public static final PropertyEnum<Special> COLOR_SPECIAL = PropertyEnum.<Special>create("color", Special.class);
	
	public static enum Basic implements IStringSerializable, IEnumMeta {
		BLUE("blue"),
		RED("red"),
		GREEN("green"),
		DARK("dark");

		private final String unlocalizedName;
		public final int meta;

		Basic(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }

		public static Basic byMetadata(int meta)
        {
            if (meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
		
		public static Basic getRandom(Random rand){
			return byMetadata(MathHelper.getInt(rand, 0, values().length-1));
		}
	}
	
	public static enum Special implements IStringSerializable, IEnumMeta {
		BLUE("blue"),
		RED("red"),
		GREEN("green"),
		DARK("dark"),
		PURE("pure");

		private final String unlocalizedName;
		public final int meta;

		Special(String name) {
	      meta = ordinal();
	      unlocalizedName = name;
	    }

	    @Override
	    public String getName() {
	      return unlocalizedName;
	    }

	    @Override
	    public int getMeta() {
	      return meta;
	    }

	    public static Special byMetadata(int meta)
        {
            if (meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
	    
		public static Special convert(Basic color) {
			if(color == Basic.RED){
				return RED;
			}
			if(color == Basic.GREEN){
				return GREEN;
			}
			if(color == Basic.DARK){
				return DARK;
			}
			return BLUE;
		}
	}
}
