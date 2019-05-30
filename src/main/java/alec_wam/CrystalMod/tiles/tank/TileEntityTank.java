package alec_wam.CrystalMod.tiles.tank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.EnumCrystalColorSpecialWithCreative;
import alec_wam.CrystalMod.tiles.INBTDrop;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityModVariant;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityTank extends TileEntityModVariant<EnumCrystalColorSpecialWithCreative> implements IMessageHandler, INBTDrop {
	public static final int[] TIER_BUCKETS = {8, 16, 32, 64, 128, 1};
	public int tier;
	public boolean isCreative;
	public boolean isDirty;
	public final FluidTank tank;
	private final LazyOptional<IFluidHandler> holder;
	
	public TileEntityTank() {
		super(ModBlocks.TILE_TANK);
		this.tier = 0;
		this.tank = new FluidTank(1000);
		this.holder = LazyOptional.of(() -> tank);
	}
	
	public TileEntityTank(EnumCrystalColorSpecialWithCreative type) {
		super(ModBlocks.TILE_TANK, type);
		this.tier = type.ordinal();
		this.isCreative = type == EnumCrystalColorSpecialWithCreative.CREATIVE;
		this.tank = new FluidTank(Fluid.BUCKET_VOLUME * TIER_BUCKETS[tier]) {
			@Override
			protected void onContentsChanged()
		    {
				isDirty = true;
		    }
		};
		this.holder = LazyOptional.of(() -> tank);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
	}


	@Override
	public void writeToItemNBT(ItemStack stack) {
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
		nbt.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.tank.readFromNBT(nbt.getCompound("Tank"));
		updateAfterLoad();
	}

	@Override
	public void readFromItemNBT(ItemStack stack) {
		NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
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
			NBTTagCompound fluidNBT = new NBTTagCompound();
			if(fluid !=null){
				fluidNBT.setTag("Fluid", fluid.writeToNBT(new NBTTagCompound()));
			} else {
				fluidNBT.setBoolean("Empty", true);
			}
			CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "FluidSync", fluidNBT), this);
		}
	}

	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("FluidSync")){
			if(messageData.hasKey("Empty")){
				tank.setFluid(null);
			} else {
				tank.setFluid(FluidStack.loadFluidStackFromNBT(messageData.getCompound("Fluid")));
			}
		}
	}
	
	@Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return holder.cast();
        return super.getCapability(cap, side);
    }

}
