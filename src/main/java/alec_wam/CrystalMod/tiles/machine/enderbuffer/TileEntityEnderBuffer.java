package alec_wam.CrystalMod.tiles.machine.enderbuffer;

import java.util.UUID;

import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.IActiveTile;
import alec_wam.CrystalMod.tiles.machine.enderbuffer.EnderBufferManager.EnderBuffer;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe.RedstoneMode;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.PlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityEnderBuffer extends TileEntityMod implements IEnderBuffer, IMessageHandler, IActiveTile {

	public int code;
	private UUID boundToPlayer;
	public Mode cuMode = Mode.DISABLED;
	public Mode rfMode = Mode.DISABLED;
	public Mode fluidMode = Mode.DISABLED;
	public Mode invMode = Mode.DISABLED;
	
	public RedstoneMode cuRSMode = RedstoneMode.IGNORE;
	public RedstoneMode rfRSMode = RedstoneMode.IGNORE;
	public RedstoneMode fluidRSMode = RedstoneMode.IGNORE;
	public RedstoneMode invRSMode = RedstoneMode.IGNORE;
	public RedstoneMode masterRedstoneMode = RedstoneMode.IGNORE;
	
	private EnderBuffer buffer;
	
	public RedstoneMode getRedstoneMode() {
		return masterRedstoneMode;
	}

	public void setRedstoneMode(RedstoneMode mode) {
		this.masterRedstoneMode = mode;
	}
	
	public boolean getRedstone(String id){
		if(id.equalsIgnoreCase("CU"))return cuRSMode.passes(getWorld(), getPos());
		if(id.equalsIgnoreCase("RF"))return rfRSMode.passes(getWorld(), getPos());
		if(id.equalsIgnoreCase("Fluid"))return fluidRSMode.passes(getWorld(), getPos());
		if(id.equalsIgnoreCase("Inv"))return invRSMode.passes(getWorld(), getPos());
		return masterRedstoneMode.passes(getWorld(), getPos());
	}
	
	public void incrsRedstone(String id){
		if(id.equalsIgnoreCase("CU")){
			cuRSMode = cuRSMode.next();
		}
		if(id.equalsIgnoreCase("RF")){
			rfRSMode = rfRSMode.next();
		}
		if(id.equalsIgnoreCase("Fluid")){
			fluidRSMode = fluidRSMode.next();
		}
		if(id.equalsIgnoreCase("Inv")){
			invRSMode = invRSMode.next();
		}
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("CU", cuRSMode.ordinal());
		nbt.setInteger("RF", rfRSMode.ordinal());
		nbt.setInteger("Fluid", fluidRSMode.ordinal());
		nbt.setInteger("Inv", invRSMode.ordinal());
		if(!getWorld().isRemote)CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateRedstone", nbt), this);
	}
	
	public void decrsRedstone(String id){
		if(id.equalsIgnoreCase("CU"))cuRSMode = cuRSMode.previous();
		if(id.equalsIgnoreCase("RF"))rfRSMode = rfRSMode.previous();
		if(id.equalsIgnoreCase("Fluid"))fluidRSMode = fluidRSMode.previous();
		if(id.equalsIgnoreCase("Inv"))invRSMode = invRSMode.previous();
		
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("CU", cuRSMode.ordinal());
		nbt.setInteger("RF", rfRSMode.ordinal());
		nbt.setInteger("Fluid", fluidRSMode.ordinal());
		nbt.setInteger("Inv", invRSMode.ordinal());
		if(!getWorld().isRemote)CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateRedstone", nbt), this);
	}
	
	@Override
	public void update(){
		super.update();
		if(this.getWorld().isRemote){
			//getWorld().markBlockForUpdate(xCoord, yCoord, zCoord);
			return;
		}
		
		if(getRedstone("CU")){
			if(this.cuMode == Mode.BOTH || this.cuMode == Mode.RECIEVE){
				for(int i = 0; (i < 6) && this.getBuffer().cuStorage.getCEnergyStored() > 0; i++){
					transferCEnergy(i);
				}
			}
	    }
		if(getRedstone("RF")){
	        if(this.rfMode == Mode.BOTH || this.rfMode == Mode.RECIEVE){
	        	for(int i = 0; (i < 6) && this.getBuffer().rfStorage.getEnergyStored() > 0; i++){
					transferEnergy(i);
				}
			}
		}
	}
	
	public void incrsCUMode(){
		int mode = this.cuMode.ordinal() + 1;
		mode%=Mode.values().length;
		this.cuMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("CU", cuMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void decrsCUMode(){
		int mode = this.cuMode.ordinal() - 1;
		mode%=Mode.values().length;
		this.cuMode = Mode.values()[mode];
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("CU", cuMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void incrsRFMode(){
		int mode = this.rfMode.ordinal() + 1;
		mode%=Mode.values().length;
		this.rfMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("RF", rfMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void decrsRFMode(){
		int mode = this.rfMode.ordinal() - 1;
		mode%=Mode.values().length;
		this.rfMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("RF", rfMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void incrsFluidMode(){
		int mode = this.fluidMode.ordinal() + 1;
		mode%=Mode.values().length;
		this.fluidMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Fluid", fluidMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void decrsFluidMode(){
		int mode = this.fluidMode.ordinal() - 1;
		mode%=Mode.values().length;
		this.fluidMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Fluid", fluidMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void incrsInvMode(){
		int mode = this.invMode.ordinal() + 1;
		mode%=Mode.values().length;
		this.invMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Inv", invMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	public void decrsInvMode(){
		int mode = this.invMode.ordinal() - 1;
		mode%=Mode.values().length;
		this.invMode = Mode.values()[mode];
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("Inv", invMode.ordinal());
		PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateMode", nbt);
		if(getWorld().isRemote)CrystalModNetwork.sendToServer(packet);
		else CrystalModNetwork.sendToAllAround(packet, this);
	}
	
	protected void transferCEnergy(int bSide)
	{
		if(!hasBuffer())return;
		EnumFacing face = EnumFacing.getFront(bSide);
		TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
		if(tile !=null && tile.hasCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite())){
			ICEnergyStorage rec = tile.getCapability(CapabilityCrystalEnergy.CENERGY, face.getOpposite());
			if(rec !=null )getBuffer().cuStorage.modifyEnergyStored(-rec.fillCEnergy(getBuffer().cuStorage.getCEnergyStored(), false));
		}
	}
	
	protected void transferEnergy(int bSide)
	{
		if(!hasBuffer())return;
		EnumFacing face = EnumFacing.getFront(bSide);
		TileEntity tile = getWorld().getTileEntity(getPos().offset(face));
		if(tile !=null && tile.hasCapability(CapabilityEnergy.ENERGY, face.getOpposite())){
			IEnergyStorage rec = tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
			if(rec !=null)getBuffer().rfStorage.extractEnergy(rec.receiveEnergy(getBuffer().rfStorage.getEnergyStored(), false), false);
		}
    }
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInteger("Code", code);
		if(boundToPlayer !=null)PlayerUtil.uuidToNBT(nbt, boundToPlayer);
		nbt.setByte("Mode.CU", (byte)cuMode.ordinal());
		nbt.setByte("Mode.RF", (byte)rfMode.ordinal());
		nbt.setByte("Mode.Fluid", (byte)fluidMode.ordinal());
		nbt.setByte("Mode.Inv", (byte)invMode.ordinal());
		
		nbt.setByte("RSMode.Master", (byte)this.masterRedstoneMode.ordinal());
		nbt.setByte("RSMode.CU", (byte)this.cuRSMode.ordinal());
		nbt.setByte("RSMode.RF", (byte)this.rfRSMode.ordinal());
		nbt.setByte("RSMode.Fluid", (byte)this.fluidRSMode.ordinal());
		nbt.setByte("RSMode.Inv", (byte)this.invRSMode.ordinal());
	}
	
    @Override
	public void readCustomNBT(NBTTagCompound nbt){
    	super.readCustomNBT(nbt);
    	if(nbt.hasKey("Code"))this.code = nbt.getInteger("Code");
    	boundToPlayer = PlayerUtil.uuidFromNBT(nbt);
        releasePreviousInventory();
    	this.cuMode = Mode.values()[nbt.getByte("Mode.CU")];
    	this.rfMode = Mode.values()[nbt.getByte("Mode.RF")];
    	this.fluidMode = Mode.values()[nbt.getByte("Mode.Fluid")];
    	this.invMode = Mode.values()[nbt.getByte("Mode.Inv")];
    	
    	if(nbt.hasKey("RSMode.Master")){
    		this.masterRedstoneMode = RedstoneMode.values()[nbt.getByte("RSMode.Master")];
    	}
    	
    	if(nbt.hasKey("RSMode.CU")){
    	RedstoneMode modeCU = RedstoneMode.values()[nbt.getByte("RSMode.CU")];
    	this.cuRSMode = modeCU;
    	}
    	
    	if(nbt.hasKey("RSMode.RF")){
    	RedstoneMode modeRF = RedstoneMode.values()[nbt.getByte("RSMode.RF")];
    	this.rfRSMode = modeRF;
    	}
    	
    	if(nbt.hasKey("RSMode.Fluid")){
    	RedstoneMode modeFluid = RedstoneMode.values()[nbt.getByte("RSMode.Fluid")];
    	this.fluidRSMode = modeFluid;
    	}
    	
    	if(nbt.hasKey("RSMode.Inv")){
    	RedstoneMode modeInv = RedstoneMode.values()[nbt.getByte("RSMode.Inv")];
    	this.invRSMode = modeInv;
    	}
	}
	
	public static enum Mode{
		DISABLED, RECIEVE, SEND, BOTH;
	}
	
	public void setCode(int code) {
		this.code = code;
		releasePreviousInventory();
        markDirty();

        BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}

	public UUID getPlayerBound()
    {
        return boundToPlayer;
    }

    public void bindToPlayer(UUID boundToPlayer)
    {
        this.boundToPlayer = boundToPlayer;

        releasePreviousInventory();
        markDirty();

        IBlockState state = getWorld().getBlockState(pos);
        getWorld().notifyBlockUpdate(pos, state, state, 3);
    }

    public boolean isBoundToPlayer()
    {
        return boundToPlayer != null;
    }

    private void releasePreviousInventory()
    {
        buffer = null;
    }

    public boolean hasBuffer()
    {
        return (code >= 0) && getBuffer() !=null;
    }

    public EnderBuffer getBuffer()
    {
        if (code < 0)
            return null;

        if (buffer == null)
        {
            if (isBoundToPlayer())
                buffer = EnderBufferManager.get(getWorld()).getPrivate(boundToPlayer).getBuffer(code);
            else
                buffer = EnderBufferManager.get(getWorld()).getBuffer(code);
        }
        return buffer;
    }

	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateInfo")){
			if(messageData.hasKey("Code")){
				this.code = messageData.getInteger("Code");
			}
		}
		if(messageId.equalsIgnoreCase("MarkDirty")){
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase("UpdateMode")){
			if(messageData.hasKey("CU")){
				this.cuMode = Mode.values()[messageData.getInteger("CU")];
			}
			if(messageData.hasKey("RF")){
				this.rfMode = Mode.values()[messageData.getInteger("RF")];
			}
			if(messageData.hasKey("Fluid")){
				this.fluidMode = Mode.values()[messageData.getInteger("Fluid")];
			}
			if(messageData.hasKey("Inv")){
				this.invMode = Mode.values()[messageData.getInteger("Inv")];
			}
		}
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
		if(capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return this.invMode !=Mode.DISABLED && hasBuffer();
		}
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return this.fluidMode !=Mode.DISABLED && hasBuffer();
		}
		if(capability == CapabilityEnergy.ENERGY){
			return this.rfMode !=Mode.DISABLED && hasBuffer();
		}
		if(capability == CapabilityCrystalEnergy.CENERGY){
			return this.cuMode !=Mode.DISABLED && hasBuffer();
		}
		return super.hasCapability(capability, facingIn);
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, net.minecraft.util.EnumFacing facing)
    {
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
        	if(!hasBuffer())return super.getCapability(capability, facing);
        	return (T) getBuffer().sendInv;
        }
        if (capability == CapabilityEnergy.ENERGY){
        	if(!hasBuffer())return super.getCapability(capability, facing);
        	return (T) new IEnergyStorage(){

				@Override
				public int receiveEnergy(int maxReceive, boolean simulate) {
					if(!canReceive())return 0;
					return getBuffer().rfStorage.receiveEnergy(maxReceive, simulate);
				}

				@Override
				public int extractEnergy(int maxExtract, boolean simulate) {
					if(!canExtract())return 0;
					return getBuffer().rfStorage.extractEnergy(maxExtract, simulate);
				}

				@Override
				public int getEnergyStored() {
					return getBuffer().rfStorage.getEnergyStored();
				}

				@Override
				public int getMaxEnergyStored() {
					return getBuffer().rfStorage.getMaxEnergyStored();
				}

				@Override
				public boolean canExtract() {
					if(TileEntityEnderBuffer.this.rfMode == Mode.BOTH || TileEntityEnderBuffer.this.rfMode == Mode.SEND){
						return getBuffer().rfStorage.canExtract() && TileEntityEnderBuffer.this.getRedstone("RF");
					}
					return false;
				}

				@Override
				public boolean canReceive() {
					if(TileEntityEnderBuffer.this.rfMode == Mode.BOTH || TileEntityEnderBuffer.this.rfMode == Mode.RECIEVE){
						return getBuffer().rfStorage.canReceive() && TileEntityEnderBuffer.this.getRedstone("RF");
					}
					return false;
				}
        		
        	};
        }
        if (capability == CapabilityCrystalEnergy.CENERGY){
        	if(!hasBuffer())return super.getCapability(capability, facing);
        	return (T) new ICEnergyStorage(){

				@Override
				public int fillCEnergy(int maxReceive, boolean simulate) {
					if(!canReceive())return 0;
					return getBuffer().cuStorage.fillCEnergy(maxReceive, simulate);
				}

				@Override
				public int drainCEnergy(int maxExtract, boolean simulate) {
					if(!canExtract())return 0;
					return getBuffer().cuStorage.drainCEnergy(maxExtract, simulate);
				}

				@Override
				public int getCEnergyStored() {
					return getBuffer().cuStorage.getCEnergyStored();
				}

				@Override
				public int getMaxCEnergyStored() {
					return getBuffer().cuStorage.getMaxCEnergyStored();
				}

				@Override
				public boolean canExtract() {
					if(TileEntityEnderBuffer.this.cuMode == Mode.BOTH || TileEntityEnderBuffer.this.cuMode == Mode.SEND){
						return getBuffer().cuStorage.canExtract() && TileEntityEnderBuffer.this.getRedstone("CU");
					}
					return false;
				}

				@Override
				public boolean canReceive() {
					if(TileEntityEnderBuffer.this.cuMode == Mode.BOTH || TileEntityEnderBuffer.this.cuMode == Mode.RECIEVE){
						return getBuffer().cuStorage.canReceive() && TileEntityEnderBuffer.this.getRedstone("CU");
					}
					return false;
				}
        		
        	};
        }
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        	if(!hasBuffer())return super.getCapability(capability, facing);
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return getBuffer().tank;
            	}
            	
            	@Override
				public int fill(FluidStack resource, boolean doFill) {
                    if (resource == null || getTank() == null) {
                        return 0;
                    }
                    FluidStack resourceCopy = resource.copy();
                    int totalUsed = 0;

                    FluidStack liquid = getTank().getFluid();
                    if (liquid != null && liquid.amount > 0 && !liquid.isFluidEqual(resourceCopy)) {
                        return 0;
                    }

                    if(resourceCopy.amount > 0) {
                        int used = getTank().fill(resourceCopy, doFill);
                        resourceCopy.amount-=used;
                        if (used > 0) {
                        	//DIRTY
                        }
                        totalUsed += used;
                    }

                    return totalUsed;
                }

                @Override
				public FluidStack drain(int maxEmpty, boolean doDrain) {
                	FluidStack output = getTank().drain(maxEmpty, doDrain);
                    return output;
                }

                @Override
				public FluidStack drain(FluidStack resource, boolean doDrain) {
                    if (resource == null) {
                        return null;
                    }
                    if (!resource.isFluidEqual(getTank().getFluid())) {
                        return null;
                    }
                    return drain(resource.amount, doDrain);
                }

				@Override
				public IFluidTankProperties[] getTankProperties() {
					return getTank().getTankProperties();
				}
                
            };
        }
        return super.getCapability(capability, facing);
    }

	@Override
	public boolean isActive() {
		return hasBuffer();
	}

}
