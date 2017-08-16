package alec_wam.CrystalMod.blocks;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalLog extends BlockLog implements ICustomModel
{
    public static final PropertyEnum<WoodType> VARIANT = PropertyEnum.<WoodType>create("variant", WoodType.class);
    
    public static enum WoodType implements IEnumMeta, IStringSerializable {
    	BLUE, RED, GREEN, DARK;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		public static WoodType byMetadata(int meta)
        {
            if (meta < 0 || meta >= values().length)
            {
                meta = 0;
            }

            return values()[meta];
        }
    }
    
    public BlockCrystalLog()
    {
    	super();
        this.setCreativeTab(CrystalMod.tabBlocks);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, WoodType.BLUE).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @SuppressWarnings("deprecation")
	@Override
    public MapColor getMapColor(IBlockState state)
    {
        return super.getMapColor(state);
    }

    @Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new LogBlockStateMapper());
		for(WoodType type : WoodType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
    }
    
    public static class LogBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			WoodType type = state.getValue(VARIANT);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			EnumAxis axis = state.getValue(LOG_AXIS);
			builder.append("axis");
			builder.append("=");
			builder.append(axis == null ? "none" : axis.getName());
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
    
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for(WoodType type : WoodType.values()){
        	list.add(new ItemStack(this, 1, type.getMeta()));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, WoodType.byMetadata((meta & 3) % 4));

        switch (meta & 12)
        {
            case 0:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
                break;
            case 4:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
                break;
            case 8:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
                break;
            default:
                iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
        }

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @SuppressWarnings("incomplete-switch")
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(VARIANT).getMeta();

        switch (state.getValue(LOG_AXIS))
        {
            case X:
                i |= 4;
                break;
            case Z:
                i |= 8;
                break;
            case NONE:
                i |= 12;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {VARIANT, LOG_AXIS});
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
    	return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(VARIANT).getMeta());
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMeta();
    }
}