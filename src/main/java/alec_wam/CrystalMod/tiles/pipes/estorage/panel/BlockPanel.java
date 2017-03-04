package alec_wam.CrystalMod.tiles.pipes.estorage.panel;


import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.pipes.estorage.EnumRenderMode6;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.crafting.TileEntityPanelCrafting;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.display.TileEntityPanelItem;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.monitor.TileEntityPanelMonitor;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPanel extends BlockContainer implements ICustomModel {
	
	public static final PropertyEnum<PanelType> PANEL_TYPE = PropertyEnum.<PanelType>create("panel", PanelType.class);
	
	public BlockPanel() {
		super(Material.IRON);
		this.setHardness(1f);
		this.setCreativeTab(CrystalMod.tabBlocks);
		setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode6.RENDER, EnumRenderMode6.AUTO));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
	    for(PanelType type : PanelType.values()) {
	      list.add(new ItemStack(this, 1, type.getMeta()));
	    }
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new PanelBlockStateMapper());
		for(PanelType type : PanelType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "render="+EnumRenderMode6.RENDER.getName(EnumRenderMode6.DEFAULTS)));
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityPanel){
			TileEntityPanel panel = (TileEntityPanel) tile;
			return state.withProperty(EnumRenderMode6.RENDER, panel.connected ? EnumRenderMode6.FRONT_ON.rotate(panel.facing) : EnumRenderMode6.FRONT.rotate(panel.facing));
		}
        return super.getActualState(state, worldIn, pos);
    }
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { EnumRenderMode6.RENDER, PANEL_TYPE });
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(PANEL_TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return ((PanelType) state.getValue(PANEL_TYPE)).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}

	protected PanelType fromMeta(int meta) {
	    if(meta < 0 || meta >= PanelType.values().length) {
	      meta = 0;
	    }

	    return PanelType.values()[meta];
	}
	
	@Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if (tile !=null && (tile instanceof TileEntityPanel)) {
        	return ((TileEntityPanel)tile).onActivated(player, hand, player.getHeldItem(hand), side);
        }
    	return false;
    }
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
		TileEntity tileentitychest = world.getTileEntity(pos);
        if (tileentitychest != null && tileentitychest instanceof IInventory)
        {
        	ItemUtil.dropContent(0, (IInventory)tileentitychest, world, tileentitychest.getPos());
        }
        super.breakBlock(world, pos, blockState);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		try{
			PanelType type = fromMeta(meta);
			return type.clazz.newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private static final float BLOCK_SIZE = 4f / 16f;
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return getFacing(worldIn, pos) == side.getOpposite();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
	    return false;
	}
	  
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		EnumFacing facing = getFacing(world, pos);
	    return getBoundingBox(pos, facing);
	}

	public AxisAlignedBB getBoundingBox(IBlockAccess world, BlockPos pos) {
	    EnumFacing facing = getFacing(world, pos);
	    return getBoundingBox(pos, facing);
  	}

	public AxisAlignedBB getBoundingBox(BlockPos pos, EnumFacing facing) {
	    int x = 0, y = 0, z = 0;
	    switch (facing) {
	    case DOWN:
	      return new AxisAlignedBB(x, y + (1 - BLOCK_SIZE), z, x + 1, y + 1, z + 1);
	    case UP:
	      return new AxisAlignedBB(x, y, z, x + 1, y + BLOCK_SIZE, z + 1);
	    case NORTH:
	      return new AxisAlignedBB(x, y, z + (1- BLOCK_SIZE), x + 1, y + 1, z + 1);
	    case SOUTH:
	      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + BLOCK_SIZE);
	    case WEST:
	      return new AxisAlignedBB(x + (1 - BLOCK_SIZE), y, z, x + 1, y + 1, z + 1);
	    case EAST:
	      return new AxisAlignedBB(x, y, z, x + BLOCK_SIZE, y + 1, z + 1);
	    default:
	      return new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1);
	    }
	}

	private EnumFacing getFacing(IBlockAccess world, BlockPos pos) {
	    TileEntity te = world.getTileEntity(pos);
	    if(te instanceof TileEntityPanel) {
	      return ((TileEntityPanel) te).facing;
	    }
	    return EnumFacing.NORTH;
	}
	
	public static enum PanelType implements IStringSerializable, IEnumMeta {
		STORAGE("storage", TileEntityPanel.class),
		CRAFTING("crafting", TileEntityPanelCrafting.class),
		DISPLAY("display", TileEntityPanelItem.class),
		MONITOR("monitor", TileEntityPanelMonitor.class);

		private final String unlocalizedName;
		public final int meta;
		public final Class<? extends TileEntityPanel> clazz;

		PanelType(String name, Class<? extends TileEntityPanel> clazz) {
	      meta = ordinal();
	      unlocalizedName = name;
	      this.clazz = clazz;
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
	
	public static class PanelBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockPanel block = (BlockPanel)state.getBlock();
			PanelType type = state.getValue(PANEL_TYPE);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(EnumRenderMode6.RENDER.getName());
			builder.append("=");
			builder.append(state.getValue(EnumRenderMode6.RENDER));
			
			nameOverride = block.getRegistryName().getResourcePath() + "_" + type.getName();

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
