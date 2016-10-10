package com.alec_wam.CrystalMod.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.alec_wam.CrystalMod.blocks.BlockCrystalLog.LogBlockStateMapper;
import com.alec_wam.CrystalMod.blocks.BlockCrystalLog.WoodType;
import com.alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassBlockStateMapper;
import com.alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks.EnumType;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalLeaves extends BlockLeaves implements ICustomModel
{
    public static final PropertyEnum<BlockCrystalLog.WoodType> VARIANT = PropertyEnum.<BlockCrystalLog.WoodType>create("variant", BlockCrystalLog.WoodType.class);

    public BlockCrystalLeaves()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockCrystalLog.WoodType.BLUE).withProperty(CHECK_DECAY, Boolean.valueOf(true)).withProperty(DECAYABLE, Boolean.valueOf(true)));
    }
    
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new LeaveBlockStateMapper());
    	for(BlockCrystalLog.WoodType type : BlockCrystalLog.WoodType.values())
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(this.getRegistryName(), (""+type).toLowerCase()));
	}
    
    @SideOnly(Side.CLIENT)
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    public static class LeaveBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			WoodType type = state.getValue(VARIANT);
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

    protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance)
    {
        
    }

    protected int getSaplingDropChance(IBlockState state)
    {
        return 25;
    }

    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(ModBlocks.crystalSapling);
    }
    
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
    	for(BlockCrystalLog.WoodType type : BlockCrystalLog.WoodType.values()){
    		list.add(new ItemStack(itemIn, 1, type.getMeta()));
    	}
    }

    protected ItemStack createStackedBlock(IBlockState state)
    {
        return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockCrystalLog.WoodType)state.getValue(VARIANT)).getMeta());
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, BlockCrystalLog.WoodType.byMetadata(meta)).withProperty(DECAYABLE, Boolean.valueOf((meta & 4) == 0)).withProperty(CHECK_DECAY, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = ((BlockCrystalLog.WoodType)state.getValue(VARIANT)).getMeta();

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

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {VARIANT, CHECK_DECAY, DECAYABLE});
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(IBlockState state)
    {
        return ((BlockCrystalLog.WoodType)state.getValue(VARIANT)).getMeta();
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune)
    {
        return java.util.Arrays.asList(createStackedBlock(world.getBlockState(pos)));
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