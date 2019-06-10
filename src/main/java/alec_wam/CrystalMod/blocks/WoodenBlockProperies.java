package alec_wam.CrystalMod.blocks;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;

public class WoodenBlockProperies {
	public static final EnumProperty<WoodType> WOOD = EnumProperty.<WoodType>create("wood", WoodType.class);
	
	public static enum WoodType implements IStringSerializable
    {
        OAK("oak"),
        SPRUCE("spruce"),
        BIRCH("birch"),
        JUNGLE("jungle"),
        ACACIA("acacia"),
        DARK_OAK("darkoak");

        private static final WoodType[] META_LOOKUP = new WoodType[values().length];
        private final String name;

        private WoodType(String nameIn)
        {
            this.name = nameIn;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        public static WoodType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        @Override
		public String getName()
        {
            return this.name;
        }

        static
        {
            for (WoodType blockplanks$enumtype : values())
            {
                META_LOOKUP[blockplanks$enumtype.ordinal()] = blockplanks$enumtype;
            }
        }
    }

	/*public static TextureAtlasSprite getPlankTexture(WoodType type) {
		switch(type){
			default : case OAK : return RenderUtil.getSprite("minecraft:blocks/planks_oak");
			case SPRUCE : return RenderUtil.getSprite("minecraft:blocks/planks_spruce");
			case BIRCH : return RenderUtil.getSprite("minecraft:blocks/planks_birch");
			case JUNGLE : return RenderUtil.getSprite("minecraft:blocks/planks_jungle");
			case ACACIA : return RenderUtil.getSprite("minecraft:blocks/planks_acacia");
			case DARK_OAK : return RenderUtil.getSprite("minecraft:blocks/planks_big_oak");
		}
	}*/
}
