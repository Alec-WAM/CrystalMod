package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.IStringSerializable;

public class WoodenBlockProperies {
	public static final PropertyEnum<WoodType> WOOD = PropertyEnum.<WoodType>create("wood", WoodType.class);
	
	public static enum WoodType implements IStringSerializable, IEnumMeta
    {
        OAK(0, "oak"),
        SPRUCE(1, "spruce"),
        BIRCH(2, "birch"),
        JUNGLE(3, "jungle"),
        ACACIA(4, "acacia"),
        DARK_OAK(5, "darkoak");

        private static final WoodType[] META_LOOKUP = new WoodType[values().length];
        private final int meta;
        private final String name;

        private WoodType(int metaIn, String nameIn)
        {
            this.meta = metaIn;
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
                META_LOOKUP[blockplanks$enumtype.getMeta()] = blockplanks$enumtype;
            }
        }

		@Override
		public int getMeta() {
			return this.meta;
		}
    }

	public static TextureAtlasSprite getPlankTexture(WoodType type) {
		switch(type){
			default : case OAK : return RenderUtil.getSprite("minecraft:blocks/planks_oak");
			case SPRUCE : return RenderUtil.getSprite("minecraft:blocks/planks_spruce");
			case BIRCH : return RenderUtil.getSprite("minecraft:blocks/planks_birch");
			case JUNGLE : return RenderUtil.getSprite("minecraft:blocks/planks_jungle");
			case ACACIA : return RenderUtil.getSprite("minecraft:blocks/planks_acacia");
			case DARK_OAK : return RenderUtil.getSprite("minecraft:blocks/planks_big_oak");
		}
	}
}
