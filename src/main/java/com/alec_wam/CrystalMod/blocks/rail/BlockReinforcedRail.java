package com.alec_wam.CrystalMod.blocks.rail;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;

public class BlockReinforcedRail extends BlockRailBase implements ICustomModel {

    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.<BlockRailBase.EnumRailDirection>create("shape", BlockRailBase.EnumRailDirection.class);

	public BlockReinforcedRail() {
		super(false);
		this.setCreativeTab(CrystalMod.tabBlocks);
		this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
	}

	@SuppressWarnings("deprecation")
	protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        if (neighborBlock.canProvidePower(state) && countAdjacentRails(worldIn, pos) == 3)
        {
            this.updateDir(worldIn, pos, state, false);
        }
    }
	
	protected int countAdjacentRails(World world, BlockPos pos)
    {
        int i = 0;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
        	BlockPos pos2 = pos.offset(enumfacing);
        	if(BlockRailBase.isRailBlock(world, pos2) || BlockRailBase.isRailBlock(world, pos2.up()) || BlockRailBase.isRailBlock(world, pos2.down()))
            {
                ++i;
            }
        }

        return i;
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty()
    {
        return SHAPE;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {SHAPE});
    }

    //OVERRIDES
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return true;
    }
    
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block neighborBlock)
    {
        if (!worldIn.isRemote)
        {
            this.onNeighborChangedInternal(worldIn, pos, worldIn.getBlockState(pos), neighborBlock);
        }
    }

	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));

		/*StateMap.Builder ignorePower = new StateMap.Builder();
        ModelLoader.setCustomStateMapper(this, ignorePower.build());*/
    }
    
}
