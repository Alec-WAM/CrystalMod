package alec_wam.CrystalMod.blocks.glass;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalGlass extends EnumBlock<CrystalColors.Special> implements ICustomModel {
    
	/*public static final PropertyEnum<GlassType> TYPE = PropertyEnum.<GlassType>create("type", GlassType.class);
	public static enum GlassType implements IStringSerializable, IEnumMeta {
			BLUE("blue"),
			RED("red"),
			GREEN("green"),
			DARK("dark"),
			PURE("pure");

			private final String unlocalizedName;
			private final int meta;

			GlassType(String name) {
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
	}*/
	
    // These are the properties used for determining whether or not a side is connected. They
    // do NOT take up block IDs, they are unlisted properties
    public static final PropertyBool CONNECTED_DOWN = PropertyBool.create("connected_down");
    public static final PropertyBool CONNECTED_UP = PropertyBool.create("connected_up");
    public static final PropertyBool CONNECTED_NORTH = PropertyBool.create("connected_north");
    public static final PropertyBool CONNECTED_SOUTH = PropertyBool.create("connected_south");
    public static final PropertyBool CONNECTED_WEST = PropertyBool.create("connected_west");
    public static final PropertyBool CONNECTED_EAST = PropertyBool.create("connected_east");
    
    public BlockCrystalGlass() {
        
      super(Material.GLASS, CrystalColors.COLOR_SPECIAL, CrystalColors.Special.class);
      
      this.setHardness(0.3f);
      setHarvestLevel("pickaxe", -1);
      this.setSoundType(SoundType.GLASS);
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
    
    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
    }
    
    @Override
	public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer)
    {
        return layer == BlockRenderLayer.CUTOUT || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
      return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
      return false;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new GlassBlockStateMapper());
    	ModelResourceLocation inv = new ModelResourceLocation(this.getRegistryName(), "inventory");
    	ClientProxy.registerCustomModel(inv, ModelGlass.INSTANCE);
		for(CrystalColors.Special type : CrystalColors.Special.values()){
	        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), inv);
	        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "color="+type.getName()), new ModelGlass(type));
		}
    }
    
    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new GlassBlockState(state, world, pos);
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
    	return new BlockStateContainer(this, new IProperty[] { CrystalColors.COLOR_SPECIAL, CONNECTED_DOWN, CONNECTED_UP, CONNECTED_NORTH, CONNECTED_SOUTH, CONNECTED_WEST, CONNECTED_EAST });
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
    	if(connected.getBlock() == this){
    		CrystalColors.Special typeO = original.getValue(CrystalColors.COLOR_SPECIAL);
    		CrystalColors.Special typeC = connected.getValue(CrystalColors.COLOR_SPECIAL);
    		return typeO == typeC;
    	}
    	return false;
    }
    
    @Override
    public boolean canPlaceTorchOnTop(IBlockState state, IBlockAccess world, BlockPos pos)
    {
    	return true;
    }
    
    public static class GlassBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			//BlockCrystalGlass block = (BlockCrystalGlass)state.getBlock();
			CrystalColors.Special type = state.getValue(CrystalColors.COLOR_SPECIAL);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(CrystalColors.COLOR_SPECIAL.getName());
			builder.append("=");
			builder.append(type);
			
			nameOverride = state.getBlock().getRegistryName().getResourcePath();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}
}
