package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFancyObsidian extends EnumBlock<BlockFancyObsidian.ObsidianType> {

	public static final PropertyEnum<ObsidianType> TYPE = PropertyEnum.<ObsidianType>create("type", ObsidianType.class);
	
	public BlockFancyObsidian() {
		super(Material.ROCK, TYPE, ObsidianType.class);
		setCreativeTab(CrystalMod.tabBlocks);
		setHardness(50f);
		setSoundType(SoundType.STONE);
		setResistance(2000.0F);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, ObsidianType.FAN));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(ObsidianType type : ObsidianType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
    
	@Override
	public MapColor getMapColor(IBlockState state)
    {
        return MapColor.BLACK;
    }
	
    public static enum ObsidianType implements IStringSerializable, IEnumMeta {
    	FAN(),
    	FLAT(),
		FLAT_CORNERED(),
		GRIDDED(),
		GRIDDED_LITE(),
		INSIGNIA(),
		INSIGNIA_CORNERED(),
		PATTERNED(),
		SEPARATED(),
		SEPARATED_SHARPLY(),
		SMOOTH(),
		SMOOTH_GRIDDED(),
		SMOOTH_SPORADIC(),
		SPORADIC(),
		SPORADIC_GRIDDED(),
		SPORADIC_GRIDDED_SMOOTH();

		private final String unlocalizedName;
		public final int meta;

		ObsidianType(){
			meta = ordinal();
			unlocalizedName = name().toLowerCase();
		}
		
	    ObsidianType(String name) {
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
