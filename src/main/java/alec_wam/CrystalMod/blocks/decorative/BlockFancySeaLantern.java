package alec_wam.CrystalMod.blocks.decorative;

import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockFancySeaLantern extends EnumBlock<BlockFancySeaLantern.SeaLanternType> {

	public static final PropertyEnum<SeaLanternType> TYPE = PropertyEnum.<SeaLanternType>create("type", SeaLanternType.class);
	
	public BlockFancySeaLantern() {
		super(Material.GLASS, TYPE, SeaLanternType.class);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setHardness(0.3f);
		setSoundType(SoundType.GLASS);
		setLightLevel(1.0F);
		setDefaultState(this.blockState.getBaseState().withProperty(TYPE, SeaLanternType.BORDERLESS));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		for(SeaLanternType type : SeaLanternType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), TYPE.getName()+"="+type.getName()));
	}
    
	@Override
	public MapColor getMapColor(IBlockState state)
    {
        return MapColor.QUARTZ;
    }
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) { 
		SeaLanternType type = state.getValue(TYPE);
		return type == SeaLanternType.GRATED || type == SeaLanternType.VINE;
	}
	
    public static enum SeaLanternType implements IStringSerializable, IEnumMeta {
    	BORDERLESS,
    	BORDERLESS_X,
    	CROSSED,
    	CROSSED_X,
    	GRATED,
    	GRATED_BAR,
    	GRIDDED,
    	GRIDDED_LITE,
    	LATTICED,
    	LATTICED_DIAGONAL,
    	LATTICED_X,
    	SHARPENED,
    	SEPERATED_HORIZONTAL,
    	SEPERATED_VERTICAL,
    	VINE,
    	WEB;

		private final String unlocalizedName;
		public final int meta;

		SeaLanternType(){
			meta = ordinal();
			unlocalizedName = name().toLowerCase();
		}
		
	    SeaLanternType(String name) {
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
