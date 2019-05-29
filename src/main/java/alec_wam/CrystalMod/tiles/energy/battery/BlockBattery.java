package alec_wam.CrystalMod.tiles.energy.battery;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.tiles.crate.BlockContainerVariant;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkHooks;

public class BlockBattery extends BlockContainerVariant<EnumCrystalColorSpecialWithCreative> {
	public static final DirectionProperty FACING = BlockDirectional.FACING;
	public static final EnumProperty<IOType> UP = EnumProperty.create("up", IOType.class);
	public static final EnumProperty<IOType> DOWN = EnumProperty.create("down", IOType.class);
	public static final EnumProperty<IOType> NORTH = EnumProperty.create("north", IOType.class);
	public static final EnumProperty<IOType> SOUTH = EnumProperty.create("south", IOType.class);
	public static final EnumProperty<IOType> EAST = EnumProperty.create("east", IOType.class);
	public static final EnumProperty<IOType> WEST = EnumProperty.create("west", IOType.class);
	
	public BlockBattery(EnumCrystalColorSpecialWithCreative type, BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockBattery> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(
				getDefaultState().with(FACING, EnumFacing.NORTH)
				.with(UP, IOType.IN)
				.with(DOWN, IOType.IN)
				.with(NORTH, IOType.IN)
				.with(SOUTH, IOType.IN)
				.with(EAST, IOType.IN)
				.with(WEST, IOType.IN));
	}
	
	@Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
		int energy = 0;
		int maxEnergy = TileEntityBattery.MAX_ENERGY[type.ordinal()];
		int send = TileEntityBattery.MAX_IO[type.ordinal()];
		int receive = TileEntityBattery.MAX_IO[type.ordinal()];
    	if(stack.hasTag() && ItemNBTHelper.verifyExistance(stack, TileEntityBattery.NBT_DATA)){
    		NBTTagCompound engineData = ItemNBTHelper.getCompound(stack).getCompound(TileEntityBattery.NBT_DATA);
    		if(this.type != EnumCrystalColorSpecialWithCreative.CREATIVE){
    			energy = engineData.getInt("Energy");
    			send = engineData.getInt("Send");
    			receive = engineData.getInt("Receive");
    		} 
    	}
    	
    	if(this.type != EnumCrystalColorSpecialWithCreative.CREATIVE){
    		NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
			String energyString = format.format(energy);
			String maxEnergyString = format.format(maxEnergy);
			String sendString = format.format(send);
			String receiveString = format.format(receive);
			tooltip.add(new TextComponentTranslation("crystalmod.info.battery.energy", energyString, maxEnergyString));
			tooltip.add(new TextComponentTranslation("crystalmod.info.battery.energy.io", sendString, receiveString));
    	}else {
			tooltip.add(new TextComponentTranslation("crystalmod.power.infinite"));
		}
    }
	
	@Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list)
    {
		ItemStack stack = new ItemStack(this);
		if(this.type == EnumCrystalColorSpecialWithCreative.CREATIVE){
			NBTTagCompound nbt = new NBTTagCompound();
			for(EnumFacing face : EnumFacing.values()){
				nbt.setByte("io."+face.name().toLowerCase(), (byte)IOType.OUT.ordinal());
			}
			ItemNBTHelper.getCompound(stack).setTag(TileEntityBattery.NBT_DATA, nbt);
		}
		list.add(stack);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
    		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
    	}
		if(tile instanceof TileEntityBattery){
			TileEntityBattery battery = (TileEntityBattery)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof EntityPlayerMP && !(player instanceof FakePlayer))
	        {
	            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
	
	            NetworkHooks.openGui(entityPlayerMP, battery, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }
	
	public static IOType getIOFromState(IBlockState state, EnumFacing facing){
		switch(facing){
			default : case UP :
				return state.get(UP);
			case DOWN :
				return state.get(DOWN);
			case NORTH :
				return state.get(NORTH);
			case SOUTH : 
				return state.get(SOUTH);
			case EAST : 
				return state.get(EAST);
			case WEST :
				return state.get(WEST);
		}
	}
	
	public static EnumProperty<IOType> getPropertyFromFace(EnumFacing facing){
		switch(facing){
			default : case UP :
				return UP;
			case DOWN :
				return DOWN;
			case NORTH :
				return NORTH;
			case SOUTH : 
				return SOUTH;
			case EAST : 
				return EAST;
			case WEST :
				return WEST;
		}
	}
	
	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
		builder.add(FACING, UP, DOWN, NORTH, EAST, WEST, SOUTH);
	}
	
	@Override
    public IBlockState getStateForPlacement(BlockItemUseContext context) {
		EnumFacing enumfacing = context.getNearestLookingDirection().getOpposite();
		return this.getDefaultState().with(FACING, enumfacing);
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockReader world, BlockPos pos)
    {
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityBattery){
			TileEntityBattery battery = (TileEntityBattery)tile;
			return state.with(UP, battery.getIO(EnumFacing.UP))
					.with(DOWN, battery.getIO(EnumFacing.DOWN))
					.with(NORTH, battery.getIO(EnumFacing.NORTH))
					.with(SOUTH, battery.getIO(EnumFacing.SOUTH))
					.with(EAST, battery.getIO(EnumFacing.EAST))
					.with(WEST, battery.getIO(EnumFacing.WEST));
		}
		return state;
    }

}
