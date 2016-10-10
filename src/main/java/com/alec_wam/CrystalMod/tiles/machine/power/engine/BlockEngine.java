package com.alec_wam.CrystalMod.tiles.machine.power.engine;

import java.util.List;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.blocks.ICustomModel;
import com.alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import com.alec_wam.CrystalMod.tiles.machine.BlockMachine;
import com.alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.furnace.TileEntityEngineFurnace;
import com.alec_wam.CrystalMod.tiles.machine.power.engine.lava.TileEntityEngineLava;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ItemUtil;

public class BlockEngine extends BlockMachine implements ICustomModel {
	
	public static int[] tierMulti = {1, 8, 64};
	
	public static final PropertyEnum<EngineType> ENGINE_TYPE = PropertyEnum.<EngineType>create("engine", EngineType.class);
	
	public BlockEngine() {
		super(Material.IRON);
		this.setHardness(2f);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list){
		for(EngineType type : EngineType.values()) {
			for(int i = 0; i < tierMulti.length; i++){
				ItemStack stack = new ItemStack(item, 1, type.getMeta());
				ItemNBTHelper.setInteger(stack, "Tier", i);
				list.add(stack);
			}
		}
	}
	
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
	    return ((EngineType) state.getValue(ENGINE_TYPE)).getMeta();
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
    	TileEntity tile = world.getTileEntity(pos);
        if ((tile instanceof TileEntityEngineBase)) {
        	if(!world.isRemote){
        		player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        	}
        	return true;
        }
    	return false;
    }
    
    public boolean isBlockNormalCube(IBlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    
    public boolean isFullBlock(IBlockState state) {
        return false;
    }
    
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

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		boolean bool = state.getValue(BlockStateMachine.activeProperty);
        return bool ? 14 : super.getLightValue(state, world, pos);
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
		LAVA("lava", TileEntityEngineLava.class);

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
			
			builder.append(BlockStateMachine.facingProperty.getName());
			builder.append("=");
			builder.append(state.getValue(BlockStateMachine.facingProperty));
			
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
