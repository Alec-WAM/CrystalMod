package alec_wam.CrystalMod.tiles.machine.crafting;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.blocks.EnumBlock.IEnumMeta;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.TileEntityCrystalFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.TileEntityGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.TileEntityCrystalInfuser;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.TileEntityLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.press.TileEntityPress;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCrystalMachine extends BlockMachine implements ICustomModel  {
	
	public static final PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.<MachineType>create("machine", MachineType.class);

	public BlockCrystalMachine() {
		super(Material.IRON);
		this.setHardness(1f).setResistance(10F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
	    for(MachineType type : MachineType.values()) {
	      list.add(new ItemStack(this, 1, type.getMeta()));
	    }
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, new MachineBlockStateMapper());
		for(MachineType type : MachineType.values()){
			String nameOverride = getRegistryName().getResourcePath() + "_" + type.getName();
			ResourceLocation baseLocation = nameOverride == null ? getRegistryName() : new ResourceLocation("crystalmod", nameOverride);
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMeta(), new ModelResourceLocation(baseLocation, "inventory"));
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateMachine(this, MACHINE_TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
	    return this.getDefaultState().withProperty(MACHINE_TYPE, fromMeta(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
	    return ((MachineType) state.getValue(MACHINE_TYPE)).getMeta();
	}

	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}

	protected MachineType fromMeta(int meta) {
	    if(meta < 0 || meta >= MachineType.values().length) {
	      meta = 0;
	    }

	    return MachineType.values()[meta];
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityMachine){
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}

	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		boolean bool = state.getValue(BlockStateMachine.activeProperty);
        return bool ? 14 : super.getLightValue(state, world, pos);
    }
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		MachineType type = fromMeta(meta);
		if(type !=null){
			try{
				return type.clazz.newInstance();
			}catch(Exception e){
				
			}
		}
		return null;
	}
	
	public static enum MachineType implements IStringSerializable, IEnumMeta {
		FURNACE("furnace", TileEntityCrystalFurnace.class),
		PRESS("press", TileEntityPress.class),
		LIQUIDIZER("liquidizer", TileEntityLiquidizer.class),
		GRINDER("grinder", TileEntityGrinder.class),
		INFUSER("infuser", TileEntityCrystalInfuser.class);

		private final String unlocalizedName;
		public final Class<? extends TileEntityMachine> clazz;
		public final int meta;

		MachineType(String name, Class<? extends TileEntityMachine> clazz) {
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
	
	public static class MachineBlockStateMapper extends StateMapperBase
	{
		@Override
		protected ModelResourceLocation getModelResourceLocation(IBlockState state)
		{
			BlockMachine block = (BlockMachine)state.getBlock();
			MachineType type = state.getValue(MACHINE_TYPE);
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
