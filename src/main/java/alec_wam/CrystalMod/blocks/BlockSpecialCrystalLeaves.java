package alec_wam.CrystalMod.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.CrystalMod;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSpecialCrystalLeaves extends BlockLeaves implements ICustomModel
{
	public BlockSpecialCrystalLeaves()
    {
    	super();
    	this.setCreativeTab(CrystalMod.tabBlocks);
    	this.setHardness(0.3f);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, false).withProperty(DECAYABLE, true));
    }
    
    @Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new LeaveBlockStateMapper());
    	for(BlockSpecialCrystalLog.SpecialCrystalLog type : BlockSpecialCrystalLog.SpecialCrystalLog.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), (""+type).toLowerCase()));
	}
    
    @Override
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    public static class LeaveBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockSpecialCrystalLog.SpecialCrystalLog type = state.getValue(BlockSpecialCrystalLog.COLOR);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			builder.append(type.getName());
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

    @Override
     protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance)
    {
        
    }

    @Override
    protected int getSaplingDropChance(IBlockState state)
    {
        return 25;
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(ModBlocks.crystalSapling);
    }
    
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
    	for(BlockSpecialCrystalLog.SpecialCrystalLog type : BlockSpecialCrystalLog.SpecialCrystalLog.values()){
    		list.add(new ItemStack(itemIn, 1, type.getMeta()));
    	}
    }

    @Override
    protected ItemStack getSilkTouchDrop(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, state.getValue(BlockSpecialCrystalLog.COLOR).getMeta());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
    	int type = meta % 4;
        return this.getDefaultState().withProperty(BlockSpecialCrystalLog.COLOR, BlockSpecialCrystalLog.SpecialCrystalLog.byMetadata(type)).withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = state.getValue(BlockSpecialCrystalLog.COLOR).getMeta();

        if (!state.getValue(DECAYABLE))
        {
            i |= 4;
        }

        if (state.getValue(CHECK_DECAY))
        {
            i |= 8;
        }

        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockSpecialCrystalLog.COLOR, CHECK_DECAY, DECAYABLE});
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(BlockSpecialCrystalLog.COLOR).getMeta() + 4 /*Offset Sapling Meta*/;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        return java.util.Arrays.asList(getSilkTouchDrop(world.getBlockState(pos)));
    }

	@Override
	public EnumType getWoodType(int meta) {
		return null;
	}
	
	@Override
	public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}
}