package alec_wam.CrystalMod.tiles.energy.battery;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.GuiHandler;
import alec_wam.CrystalMod.core.BlockVariantGroup;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.TileEntityIOSides.IOType;
import alec_wam.CrystalMod.tiles.crate.ContainerBlockVariant;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

public class BlockBattery extends ContainerBlockVariant<EnumCrystalColorSpecialWithCreative> {
	public static final DirectionProperty FACING = DirectionalBlock.FACING;
	public static final EnumProperty<IOType> UP = EnumProperty.create("up", IOType.class);
	public static final EnumProperty<IOType> DOWN = EnumProperty.create("down", IOType.class);
	public static final EnumProperty<IOType> NORTH = EnumProperty.create("north", IOType.class);
	public static final EnumProperty<IOType> SOUTH = EnumProperty.create("south", IOType.class);
	public static final EnumProperty<IOType> EAST = EnumProperty.create("east", IOType.class);
	public static final EnumProperty<IOType> WEST = EnumProperty.create("west", IOType.class);
	
	public BlockBattery(EnumCrystalColorSpecialWithCreative type, BlockVariantGroup<EnumCrystalColorSpecialWithCreative, BlockBattery> variantGroup, Properties properties) {
		super(type, variantGroup, properties);
		this.setDefaultState(
				getDefaultState().with(FACING, Direction.NORTH)
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
    		CompoundNBT engineData = ItemNBTHelper.getCompound(stack).getCompound(TileEntityBattery.NBT_DATA);
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
			tooltip.add(new TranslationTextComponent("crystalmod.info.battery.energy", energyString, maxEnergyString));
			tooltip.add(new TranslationTextComponent("crystalmod.info.battery.energy.io", sendString, receiveString));
    	}else {
			tooltip.add(new TranslationTextComponent("crystalmod.power.infinite"));
		}
    }

	public static CompoundNBT getDefaultItemNBT(EnumCrystalColorSpecialWithCreative type){
		CompoundNBT nbt = new CompoundNBT();
		for(Direction face : Direction.values()){
			nbt.putByte("io."+face.name().toLowerCase(), (byte)IOType.IN.ordinal());
		}
		nbt.putInt("Send", TileEntityBattery.MAX_IO[type.ordinal()]);
		nbt.putInt("Receive", TileEntityBattery.MAX_IO[type.ordinal()]);
		return nbt;
	}
	
	@Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> list)
    {
		ItemStack stack = new ItemStack(this);
		if(this.type == EnumCrystalColorSpecialWithCreative.CREATIVE){
			CompoundNBT nbt = new CompoundNBT();
			for(Direction face : Direction.values()){
				nbt.putByte("io."+face.name().toLowerCase(), (byte)IOType.OUT.ordinal());
			}
			ItemNBTHelper.getCompound(stack).put(TileEntityBattery.NBT_DATA, nbt);
		} else {
			CompoundNBT nbt = getDefaultItemNBT(type);
			for(Direction face : Direction.values()){
				nbt.putByte("io."+face.name().toLowerCase(), (byte)IOType.IN.ordinal());
			}
			ItemNBTHelper.getCompound(stack).put(TileEntityBattery.NBT_DATA, nbt);
		}
		list.add(stack);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray)
    {
		TileEntity tile = worldIn.getTileEntity(pos);
		if(player.isSneaking() && ToolUtil.isHoldingWrench(player, hand)){
    		return ToolUtil.breakBlockWithWrench(worldIn, pos, player, hand);
    	}
		if(tile instanceof TileEntityBattery){
			TileEntityBattery battery = (TileEntityBattery)tile;
			if(worldIn.isRemote)return true;
			if (player instanceof ServerPlayerEntity && !(player instanceof FakePlayer))
	        {
	            ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity) player;
	
	            GuiHandler.openCustomGui(GuiHandler.TILE_NORMAL, entityPlayerMP, battery, buf -> buf.writeBlockPos(pos));
	        }
			return true;
		}
        return super.onBlockActivated(state, worldIn, pos, player, hand, ray);
    }
	
	public static IOType getIOFromState(BlockState state, Direction facing){
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
	
	public static EnumProperty<IOType> getPropertyFromFace(Direction facing){
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
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, UP, DOWN, NORTH, EAST, WEST, SOUTH);
	}
	
	@Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction enumfacing = context.getNearestLookingDirection().getOpposite();
		return this.getDefaultState().with(FACING, enumfacing);
	}
	
	@Override
	public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos)
    {
		TileEntity tile = world.getTileEntity(pos);		
		if(tile instanceof TileEntityBattery){
			TileEntityBattery battery = (TileEntityBattery)tile;
			return state.with(UP, battery.getIO(Direction.UP))
					.with(DOWN, battery.getIO(Direction.DOWN))
					.with(NORTH, battery.getIO(Direction.NORTH))
					.with(SOUTH, battery.getIO(Direction.SOUTH))
					.with(EAST, battery.getIO(Direction.EAST))
					.with(WEST, battery.getIO(Direction.WEST));
		}
		return state;
    }

}
