package alec_wam.CrystalMod.tiles.machine.worksite;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.worksite.InventorySided.IRotatableTile;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteAnimalFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteCropFarm;
import alec_wam.CrystalMod.tiles.machine.worksite.imp.WorksiteTreeFarm;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWorksite extends BlockContainer implements ICustomModel {

	public static final PropertyEnum<WorksiteType> WORKSITE_TYPE = PropertyEnum.<WorksiteType>create("type", WorksiteType.class);
	
	public BlockWorksite() {
		super(Material.IRON);
		this.setCreativeTab(CrystalMod.tabBlocks);
		setHardness(3.0f);
        this.setResistance(20.0F);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(WorksiteType type : WorksiteType.values()) {
			ItemStack stack = new ItemStack(item, 1, type.getMeta());
			list.add(stack);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomStateMapper(this, new WorksiteBlockStateMapper());
		for(WorksiteType type : WorksiteType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        EnumFacing face = EnumFacing.NORTH;
        if (te !=null) {
        	if(te instanceof IRotatableTile)face = ((IRotatableTile)te).getPrimaryFacing();
        }
        return state.withProperty(BlockStateFacing.facingProperty, face);
    }
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateFacing(this, WORKSITE_TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
	    return this.getDefaultState().withProperty(WORKSITE_TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return state.getValue(WORKSITE_TYPE).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}

	protected WorksiteType fromMeta(int meta) {
	    if(meta < 0 || meta >= WorksiteType.values().length) {
	      meta = 0;
	    }

	    return WorksiteType.values()[meta];
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		WorksiteType type = fromMeta(meta);
		try{
			return type.clazz.newInstance();
		}catch(Exception e){};
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IWorkSite && te instanceof TileWorksiteBase) {
			
			TileWorksiteBase worksite = (TileWorksiteBase)te;
			if(!world.isRemote){
				ItemStack held = player.getHeldItem(hand);
				if(ItemStackTools.isValid(held)){
					if(held.getItem() == ModItems.minionStaff){
						if(player.isSneaking()){
							
						} else {
							ChatUtil.sendNoSpam(player, "Workers: "+worksite.workers.size());
						}
						return true;
					}
				}
			}
			return worksite.onBlockClicked(player);
		}
		return true;
	}

	@Override
	public boolean rotateBlock(World worldObj, BlockPos pos, EnumFacing axis) {
		TileEntity te = worldObj.getTileEntity(pos);
		if (te instanceof TileWorksiteBase) {
			TileWorksiteBase twb = (TileWorksiteBase) te;
			EnumFacing o = twb.getPrimaryFacing();
			if (axis == EnumFacing.DOWN || axis == EnumFacing.UP) {
				o = o.rotateY();
				twb.setPrimaryFacing(o);// twb will send update packets / etc
			}
		}
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof IInventory) {
			ItemUtil.dropContent(0, (IInventory) te, world, pos);
		}
		if (te instanceof IWorkSite) {
			((IWorkSite) te).onBlockBroken();
		}
		super.breakBlock(world, pos, state);
	}
	
	public static enum WorksiteType implements IStringSerializable, IEnumMeta {
		TREE_FARM("treefarm", WorksiteTreeFarm.class),
		ANIMAL_FARM("animalfarm", WorksiteAnimalFarm.class),
		CROP_FARM("cropfarm", WorksiteCropFarm.class);

		private final String unlocalizedName;
		public final Class<? extends TileWorksiteBase> clazz;
		public final int meta;

		WorksiteType(String name, Class<? extends TileWorksiteBase> clazz) {
	      meta = ordinal();
	      this.clazz = clazz;
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
	
	public static class WorksiteBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockWorksite block = (BlockWorksite)state.getBlock();
			WorksiteType type = state.getValue(WORKSITE_TYPE);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateFacing.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateFacing.facingProperty));
			
			nameOverride = block.getRegistryName().getResourcePath() + "_" + type.getName();

			if(builder.length() == 0)
			{
				builder.append("normal");
			}

			ResourceLocation baseLocation = nameOverride == null ? state.getBlock().getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			
			return new ModelResourceLocation(baseLocation, builder.toString());
		}
	}

}
