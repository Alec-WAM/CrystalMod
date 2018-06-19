package alec_wam.CrystalMod.tiles.machine.power.engine;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import alec_wam.CrystalMod.tiles.machine.power.engine.furnace.TileEntityEngineFurnace;
import alec_wam.CrystalMod.tiles.machine.power.engine.lava.TileEntityEngineLava;
import alec_wam.CrystalMod.tiles.machine.power.engine.vampire.TileEntityEngineVampire;
import alec_wam.CrystalMod.util.ChatUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemUtil;
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
import net.minecraft.util.BlockRenderLayer;
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

public class BlockEngine extends BlockMachine implements ICustomModel {
	
	public static int[] tierMulti = {1, 8, 64};
	
	public static final PropertyEnum<EngineType> ENGINE_TYPE = PropertyEnum.<EngineType>create("engine", EngineType.class);
	
	public BlockEngine() {
		super(Material.IRON);
		this.setHardness(2f);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, NonNullList<ItemStack> list){
		for(EngineType type : EngineType.values()) {
			for(int i = 0; i < tierMulti.length; i++){
				ItemStack stack = new ItemStack(item, 1, type.getMeta());
				ItemNBTHelper.setInteger(stack, "Tier", i);
				list.add(stack);
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void initModel() {
		ModelLoader.setCustomStateMapper(this, new EngineBlockStateMapper());
		for(EngineType type : EngineType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
    }
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateMachine(this, ENGINE_TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
	    return this.getDefaultState().withProperty(ENGINE_TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return state.getValue(ENGINE_TYPE).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}

	protected EngineType fromMeta(int meta) {
	    if(meta < 0 || meta >= EngineType.values().length) {
	      meta = 0;
	    }

	    return EngineType.values()[meta];
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if ((tile instanceof TileEntityEngineBase)) {
        	TileEntityEngineBase engine = (TileEntityEngineBase)tile;
        	if(player.isSneaking()){
        		String powerinfo = engine.energyStorage.getCEnergyStored()+" / "+engine.energyStorage.getMaxCEnergyStored()+" CU";
        		String fuelInfo = engine.fuel.getValue()+" / "+engine.maxFuel.getValue();
        		ChatUtil.sendNoSpam(player, powerinfo, fuelInfo);
        	} else {
	        	if(!world.isRemote){
	        		player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
	        	}
        	}
        	return true;
        }
    	return false;
    }
    
	@Override
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
	@Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
	@Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
    	return super.getBlockLayer();
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
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
    	TileEntity tile = world.getTileEntity(pos);
        if ((tile instanceof TileEntityEngineBase)) {
        	TileEntityEngineBase engine = (TileEntityEngineBase)tile;
        	if(engine.isActive())return 14;
        }
        return 0;
    }
    
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		try{
			EngineType type = fromMeta(meta);
			if(type.clazz !=null){
				return type.clazz.newInstance();
			}
		}catch(Exception e){
			
		}
		return null;
	}
	
	public static enum EngineType implements IStringSerializable, IEnumMeta {
		FURNACE("furnace", TileEntityEngineFurnace.class),
		LAVA("lava", TileEntityEngineLava.class),
		VAMPIRE("vampire", TileEntityEngineVampire.class);

		private final String unlocalizedName;
		public final Class<? extends TileEntityEngineBase> clazz;
		public final int meta;

		EngineType(String name, Class<? extends TileEntityEngineBase> clazz) {
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
	
	public static class EngineBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockEngine block = (BlockEngine)state.getBlock();
			EngineType type = state.getValue(ENGINE_TYPE);
			StringBuilder builder = new StringBuilder();
			String nameOverride = null;
			
			builder.append(BlockStateMachine.activeProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateMachine.activeProperty));
			
			builder.append(",");
			
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
