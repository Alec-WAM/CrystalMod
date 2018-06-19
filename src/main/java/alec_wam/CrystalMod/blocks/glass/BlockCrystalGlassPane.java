package alec_wam.CrystalMod.blocks.glass;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassBlockStateMapper;
import alec_wam.CrystalMod.proxy.ClientProxy;
import alec_wam.CrystalMod.util.CrystalColors;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalGlassPane extends BlockPane implements ICustomModel {

	public BlockCrystalGlassPane() {
		super(Material.GLASS, true);
		this.setHardness(0.3f);
		setHarvestLevel("pickaxe", -1);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

    @Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
    	ModelLoader.setCustomStateMapper(this, new GlassBlockStateMapper());
		for(CrystalColors.Special type : CrystalColors.Special.values()){
			 ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(new ResourceLocation("crystalmod", getRegistryName().getResourcePath() + "_" + type.getName()), "inventory"));
	        ClientProxy.registerCustomModel(new ModelResourceLocation(getRegistryName(), "type="+type.getName()), new ModelGlassPane(type));
		}
	}
    
    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    @Override
	public int damageDropped(IBlockState state)
    {
        return state.getValue(CrystalColors.COLOR_SPECIAL).getMeta();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        for (int i = 0; i < CrystalColors.Special.values().length; ++i)
        {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
    	return EnumBlockRenderType.MODEL;
    }
    
    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(CrystalColors.COLOR_SPECIAL, CrystalColors.Special.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
	public int getMetaFromState(IBlockState state)
    {
        return state.getValue(CrystalColors.COLOR_SPECIAL).getMeta();
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
	public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case CLOCKWISE_180:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(EAST, state.getValue(WEST)).withProperty(SOUTH, state.getValue(NORTH)).withProperty(WEST, state.getValue(EAST));
            case COUNTERCLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(EAST)).withProperty(EAST, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(WEST)).withProperty(WEST, state.getValue(NORTH));
            case CLOCKWISE_90:
                return state.withProperty(NORTH, state.getValue(WEST)).withProperty(EAST, state.getValue(NORTH)).withProperty(SOUTH, state.getValue(EAST)).withProperty(WEST, state.getValue(SOUTH));
            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        switch (mirrorIn)
        {
            case LEFT_RIGHT:
                return state.withProperty(NORTH, state.getValue(SOUTH)).withProperty(SOUTH, state.getValue(NORTH));
            case FRONT_BACK:
                return state.withProperty(EAST, state.getValue(WEST)).withProperty(WEST, state.getValue(EAST));
            default:
                return super.withMirror(state, mirrorIn);
        }
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {NORTH, EAST, WEST, SOUTH, CrystalColors.COLOR_SPECIAL});
    }
    
    @Override
    public IBlockState getExtendedState(final IBlockState state, final IBlockAccess world, final BlockPos pos) {
		return new GlassBlockState(state, world, pos);
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
    public static boolean isSideConnectable(IBlockAccess world, BlockPos pos, EnumFacing side) {
      final IBlockState original = world.getBlockState(pos);
      final IBlockState connected = world.getBlockState(pos.offset(side));

      return original != null && connected != null && canConnect(original, connected);
    }
    
    /**
     * Checks if this block should connect to another block
     * @param state BlockState to check
     * @return True if the block is valid to connect
     */
    public static boolean canConnect(@Nonnull IBlockState original, @Nonnull IBlockState connected) {
    	if(original.getBlock() == connected.getBlock()){
    		CrystalColors.Special typeO = original.getValue(CrystalColors.COLOR_SPECIAL);
    		CrystalColors.Special typeC = connected.getValue(CrystalColors.COLOR_SPECIAL);
    		return typeO == typeC;
    	}
    	return false;
    }

}
