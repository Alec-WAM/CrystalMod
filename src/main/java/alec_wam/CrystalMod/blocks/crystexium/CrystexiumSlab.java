package alec_wam.CrystalMod.blocks.crystexium;

import java.util.Random;

import javax.annotation.Nonnull;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CrystexiumSlab extends BlockSlab implements ICustomModel 
{
    public static final PropertyEnum<CrystexiumSlab.EnumType> VARIANT = PropertyEnum.<CrystexiumSlab.EnumType>create("type", CrystexiumSlab.EnumType.class);

    public CrystexiumSlab()
    {
        super(Material.GLASS);
        IBlockState iblockstate = this.blockState.getBaseState();

        iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);

        this.setDefaultState(iblockstate.withProperty(VARIANT, CrystexiumSlab.EnumType.NORMAL));
        this.setCreativeTab(CrystalMod.tabBlocks);
        setHardness(2.0F).setResistance(10.0F);
        setSoundType(SoundType.GLASS);
    }
    
    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
      return BlockRenderLayer.TRANSLUCENT;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return super.isOpaqueCube(state);
    }
	
	@Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        if (net.minecraftforge.common.ForgeModContainer.disableStairSlabCulling)
            return super.doesSideBlockRendering(state, world, pos, face);

        if ( state.isOpaqueCube() )
            return true;

        return false;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side)
	{
		IBlockState other = worldIn.getBlockState(pos.offset(side));
		if(other.getBlock() == this && getMetaFromState(state) == getMetaFromState(other)){
			return false;
		}
		return super.shouldSideBeRendered(state, worldIn, pos, side);
	}
    
    @SideOnly(Side.CLIENT)
    @Override
    public void initModel(){
    	for(EnumType type : EnumType.values()){
    		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(),	new ModelResourceLocation(getRegistryName(), "half=bottom,type="+type.toString()));
    	}
    }
    
    /**
     * Get the Item that this Block should drop when harvested.
     */
    @Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(ModBlocks.crystexiumSlab);
    }

    @Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModBlocks.crystexiumSlab, 1, state.getValue(VARIANT).getMetadata());
    }

    /**
     * Returns the slab block name with the type associated with it
     */
    @Override
	public String getUnlocalizedName(int meta)
    {
        return super.getUnlocalizedName() + "." + CrystexiumSlab.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    @Override
	public IProperty<?> getVariantProperty()
    {
        return VARIANT;
    }

    @Override
	public Comparable<?> getTypeForItem(ItemStack stack)
    {
        return CrystexiumSlab.EnumType.byMetadata(stack.getMetadata() & 7);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @Override
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
    	for (CrystexiumSlab.EnumType blockstoneslab$enumtype : CrystexiumSlab.EnumType.values())
    	{
    		list.add(new ItemStack(itemIn, 1, blockstoneslab$enumtype.getMetadata()));
    	}
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, CrystexiumSlab.EnumType.byMetadata(meta & 7));

        iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
	public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP)
        {
            i |= 8;
        }

        return i;
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return this.isDouble() ? new BlockStateContainer(this, new IProperty[] {VARIANT}): new BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    @Override
	public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    @Override
	public MapColor getMapColor(IBlockState state)
    {
        return state.getValue(VARIANT).getMapColor();
    }

    public static enum EnumType implements IStringSerializable
    {
        NORMAL(0, MapColor.PINK, "normal"),
        BLUE(1, MapColor.BLUE, "blue"),
        RED(2, MapColor.RED, "red"),
        GREEN(3, MapColor.GREEN, "green"),
        DARK(4, MapColor.BLACK, "dark"),
        PURE(5, MapColor.IRON, "pure");

        private static final CrystexiumSlab.EnumType[] META_LOOKUP = new CrystexiumSlab.EnumType[values().length];
        private final int meta;
        private final MapColor mapColor;
        private final String name;
        private final String unlocalizedName;

        private EnumType(int p_i46381_3_, MapColor p_i46381_4_, String p_i46381_5_)
        {
            this(p_i46381_3_, p_i46381_4_, p_i46381_5_, p_i46381_5_);
        }

        private EnumType(int p_i46382_3_, MapColor p_i46382_4_, String p_i46382_5_, String p_i46382_6_)
        {
            this.meta = p_i46382_3_;
            this.mapColor = p_i46382_4_;
            this.name = p_i46382_5_;
            this.unlocalizedName = p_i46382_6_;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public MapColor getMapColor()
        {
            return this.mapColor;
        }

        @Override
		public String toString()
        {
            return this.name;
        }

        public static CrystexiumSlab.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        @Override
		public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static
        {
            for (CrystexiumSlab.EnumType blockstoneslab$enumtype : values())
            {
                META_LOOKUP[blockstoneslab$enumtype.getMetadata()] = blockstoneslab$enumtype;
            }
        }
    }

	@Override
	public boolean isDouble() {
		return false;
	}
}