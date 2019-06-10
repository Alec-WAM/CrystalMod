package alec_wam.CrystalMod.tiles.tank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.compatibility.FluidConversion;
import alec_wam.CrystalMod.compatibility.FluidTankFixed;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityModVariant;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityTank extends TileEntityModVariant<EnumCrystalColorSpecialWithCreative> implements IMessageHandler, INBTDrop {
	//TODO Add Fish tank feature if tank is full of water (Maybe seperate tank)
	public static final int[] TIER_BUCKETS = {8, 16, 32, 64, 128, 1};
	public int tier;
	public boolean isCreative;
	public boolean isDirty;
	public final FluidTankFixed tank;
	private final LazyOptional<IFluidHandler> holder;
	
	public TileEntityTank() {
		this(EnumCrystalColorSpecialWithCreative.BLUE);
	}
	
	public TileEntityTank(EnumCrystalColorSpecialWithCreative type) {
		super(ModBlocks.tankGroup.getTileType(type), type);
		this.tier = type.ordinal();
		this.isCreative = type == EnumCrystalColorSpecialWithCreative.CREATIVE;
		this.tank = new FluidTankFixed(Fluid.BUCKET_VOLUME * TIER_BUCKETS[tier]) {
			@Override
			protected void onContentsChanged()
		    {
				isDirty = true;
		    }
		};
		this.holder = LazyOptional.of(() -> tank);
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
	}


	@Override
	public void writeToItemNBT(ItemStack stack) {
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
		nbt.put("Tank", tank.writeToNBT(new CompoundNBT()));
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		this.tank.readFromNBT(nbt.getCompound("Tank"));
		updateAfterLoad();
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		CompoundNBT nbt = ItemNBTHelper.getCompound(stack);
		this.tank.readFromNBT(nbt.getCompound("Tank"));
	}
	
	@Override
	public void tick(){
		super.tick();
		if(!getWorld().isRemote){
			if(isDirty){
				syncFluid();
				this.isDirty = false;
			}
		}
	}
	
	public void syncFluid(){
		if(getWorld() !=null && !getWorld().isRemote && getPos() !=null){
			FluidStack fluid = tank.getFluid();
			CompoundNBT fluidNBT = new CompoundNBT();
			if(fluid !=null){
				fluidNBT.put("Fluid", FluidConversion.writeToNBT(fluid, new CompoundNBT()));
			} else {
				fluidNBT.putBoolean("Empty", true);
			}
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "FluidSync", fluidNBT), this);
		}
	}

	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		if(messageId.equalsIgnoreCase("FluidSync")){
			if(messageData.contains("Empty")){
				tank.setFluid(null);
			} else {
				tank.setFluid(FluidConversion.loadFluidStackFromNBT(messageData.getCompound("Fluid")));
			}
		}
	}
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }

}
