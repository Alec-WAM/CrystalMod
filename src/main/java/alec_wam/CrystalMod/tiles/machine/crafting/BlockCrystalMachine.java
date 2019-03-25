package alec_wam.CrystalMod.tiles.machine.crafting;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.util.IEnumMeta;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.tool.ToolUtil;
import alec_wam.CrystalMod.blocks.ICustomModel;
import alec_wam.CrystalMod.tiles.BlockStateFacing;
import alec_wam.CrystalMod.tiles.machine.BlockMachine;
import alec_wam.CrystalMod.tiles.machine.BlockStateMachine;
import alec_wam.CrystalMod.tiles.machine.TileEntityMachine;
import alec_wam.CrystalMod.tiles.machine.crafting.fluidmixer.TileEntityFluidMixer;
import alec_wam.CrystalMod.tiles.machine.crafting.furnace.TileEntityCrystalFurnace;
import alec_wam.CrystalMod.tiles.machine.crafting.grinder.TileEntityGrinder;
import alec_wam.CrystalMod.tiles.machine.crafting.infuser.TileEntityCrystalInfuser;
import alec_wam.CrystalMod.tiles.machine.crafting.liquidizer.TileEntityLiquidizer;
import alec_wam.CrystalMod.tiles.machine.crafting.press.TileEntityPress;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class BlockCrystalMachine extends BlockMachine implements ICustomModel  {
	//TODO Add speed and power upgrades
	public static final PropertyEnum<MachineType> MACHINE_TYPE = PropertyEnum.<MachineType>create("machine", MachineType.class);

	public BlockCrystalMachine() {
		super(Material.IRON);
		this.setSoundType(SoundType.METAL);
		this.setHardness(1f).setResistance(15F);
		this.setCreativeTab(CrystalMod.tabBlocks);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
	    for(MachineType type : MachineType.values()) {
	      list.add(new ItemStack(this, 1, type.getMeta()));
	    }
	}
	
	@Override
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
	    return state.getValue(MACHINE_TYPE).getMeta();
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hX, float hY, float hZ){
		TileEntity tile = world.getTileEntity(pos);
		if(tile !=null && tile instanceof TileEntityMachine){
			TileEntityMachine machine = (TileEntityMachine)tile;
			ItemStack stack = player.getHeldItem(hand);
			if (ItemStackTools.isValid(stack)) {
            	if(ToolUtil.isToolEquipped(player, hand) && player.isSneaking()){
            		return ToolUtil.breakBlockWithTool(this, world, pos, player, hand);
            	}            

            	IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(stack);
            	if (containerFluidHandler != null)
            	{
            		IItemHandler playerInventory = new InvWrapper(player.inventory);
            		IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, side);
            		if(handler !=null){
            			FluidActionResult result = null;            			
            			if(machine.canInsertFluidWithBucket()){            				
            				result = FluidUtil.tryEmptyContainerAndStow(stack, handler, playerInventory, Integer.MAX_VALUE, player);
            			}
            			if(machine.canExtractFluidWithBucket()){
            				if(result == null){
            					result = FluidUtil.tryFillContainerAndStow(stack, handler, playerInventory, Integer.MAX_VALUE, player);
            				}
            			}
            			if(result !=null && result.isSuccess()){
            				player.setHeldItem(hand, result.getResult());
            				return true;
            			}
            		}
            	}
        	}
			
			if(!world.isRemote){
				player.openGui(CrystalMod.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
			}
			return true;
		}
		return false;
	}
	
	@Override
    public void breakBlock(World world, BlockPos pos, IBlockState blockState)
    {
        world.getTileEntity(pos);
        /*if (tile != null && tile instanceof IInventory)
        {
            ItemUtil.dropContent(0, (IInventory)tile, world, tile.getPos());
        }*/
        super.breakBlock(world, pos, blockState);
    }

	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
		boolean bool = state.getValue(BlockStateMachine.activeProperty);
        return bool ? 14 : 0;
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
		INFUSER("infuser", TileEntityCrystalInfuser.class),
		FLUID_MIXER("fluid_mixer", TileEntityFluidMixer.class);

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
