package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFancyGlowstone extends EnumBlock<BlockFancyGlowstone.GlowstoneType> {

	public static final PropertyEnum<GlowstoneType> TYPE = PropertyEnum.<GlowstoneType>create("type", GlowstoneType.class);
	
	public BlockFancyGlowstone() {
		super(Material.GLASS, TYPE, GlowstoneType.class);
		setCreativeTab(CrystalMod.tabBlocks);
		setHardness(0.3f);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, GlowstoneType.ASYMMETRICAL));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(GlowstoneType type : GlowstoneType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
    
    public static enum GlowstoneType implements IStringSerializable, IEnumMeta {
		ASYMMETRICAL("asymmetrical"),
		CHECKERED("checkered"),
		FLAT("flat"),
		GLITTERING("glittering"),
		GRIDDED("gridded"),
		GRIDDED_LITE("gridded_lite"),
		LATTICED("latticed"),
		MOVING("moving"),
		NOISY("noisy"),
		ORGANIZED("organized"),
		UNORGANIZED("unorganized"),
		OUTER_SQUARE("outer_square"),
		POPPED("popped"),
		POPPED_ORGANIZED("popped_organized"),
		ROCKY("rocky"),
		SPARKLING("sparkling");

		private final String unlocalizedName;
		public final int meta;

	    GlowstoneType(String name) {
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
    	
    }

}
