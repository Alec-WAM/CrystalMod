package alec_wam.CrystalMod.blocks.connected;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.client.model.dynamic.ModelConnectedTexture;
import alec_wam.CrystalMod.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockConnectedTexture extends Block implements ICustomModel {
    
	// These are the properties used for determining whether or not a side is connected. They
    // do NOT take up block IDs, they are unlisted properties
    public static final PropertyBool CONNECTED_DOWN = PropertyBool.create("connected_down");
    public static final PropertyBool CONNECTED_UP = PropertyBool.create("connected_up");
    public static final PropertyBool CONNECTED_NORTH = PropertyBool.create("connected_north");
    public static final PropertyBool CONNECTED_SOUTH = PropertyBool.create("connected_south");
    public static final PropertyBool CONNECTED_WEST = PropertyBool.create("connected_west");
    public static final PropertyBool CONNECTED_EAST = PropertyBool.create("connected_east");
    
    public BlockConnectedTexture() {
        
      super(Material.ROCK);
      
      this.setHardness(5f);
      this.setCreativeTab(CrystalMod.tabBlocks);
      // By default none of the sides are connected
      this.setDefaultState(this.blockState.getBaseState()
                                          .withProperty(CONNECTED_DOWN, Boolean.FALSE)
                                          .withProperty(CONNECTED_EAST, Boolean.FALSE)
                                          .withProperty(CONNECTED_NORTH, Boolean.FALSE)
                                          .withProperty(CONNECTED_SOUTH, Boolean.FALSE)
                                          .withProperty(CONNECTED_UP, Boolean.FALSE)
                                          .withProperty(CONNECTED_WEST, Boolean.FALSE));
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new GlassBlockStateMapper());
    	ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
    	ClientProxy.registerCustomModel(inv, getModel());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, inv);
        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "normal"), getModel());
    }
    
    @SideOnly(Side.CLIENT)
	public abstract ModelConnectedTexture getModel();
    
    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new ConnectedBlockState(state, world, pos);
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos position) {
        
      // Creates the state to use for the block. This is where we check if every side is
      // connectable or not.
      return state.withProperty(CONNECTED_DOWN,  isSideConnectable(world, position, EnumFacing.DOWN))
                  .withProperty(CONNECTED_EAST,  isSideConnectable(world, position, EnumFacing.EAST))
                  .withProperty(CONNECTED_NORTH, isSideConnectable(world, position, EnumFacing.NORTH))
                  .withProperty(CONNECTED_SOUTH, isSideConnectable(world, position, EnumFacing.SOUTH))
                  .withProperty(CONNECTED_UP,    isSideConnectable(world, position, EnumFacing.UP))
                  .withProperty(CONNECTED_WEST,  isSideConnectable(world, position, EnumFacing.WEST));
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
    	return new BlockStateContainer(this, new IProperty[] { CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST });
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
      return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
      return 0;
    }
    
    /**
     * Checks if a specific side of a block can connect to this block. For this example, a side
     * is connectable if the block is the same block as this one.
     * 
     * @param world The world to run the check in.
     * @param pos The position of the block to check for.
     * @param side The side of the block to check.
     * @return Whether or not the side is connectable.
     */
    public boolean isSideConnectable(IBlockAccess world, BlockPos pos, EnumFacing side) {
      final IBlockState original = world.getBlockState(pos);
      final IBlockState connected = world.getBlockState(pos.offset(side));

      return original != null && connected != null && canConnect(original, connected);
    }
    
    /**
     * Checks if this block should connect to another block
     * @param state BlockState to check
     * @return True if the block is valid to connect
     */
    public boolean canConnect(@Nonnull IBlockState original, @Nonnull IBlockState connected) {
    	return original.getBlock() == connected.getBlock();
    }
    
    public static class GlassBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			//BlockCrystalGlass block = (BlockCrystalGlass)state.getBlock();
			String nameOverride = null;
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, "normal");
		}
	}
}
