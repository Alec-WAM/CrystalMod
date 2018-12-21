package alec_wam.CrystalMod.tiles.xp;

import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.fluids.xp.ExperienceContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.INBTDrop;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityXPTank extends TileEntityMod implements IMessageHandler, INBTDrop {
	public ExperienceContainer xpCon;
	public TileEntityXPTank(){
		xpCon = new ExperienceContainer(XpUtil.getExperienceForLevel(100));
	}
	
	public void changeXP(EntityPlayer player, int amount, boolean add){
		if(add){
			int realAmount = Math.min(amount, XpUtil.getPlayerXP(player));
			int added = xpCon.addExperience(realAmount);
			if(added > 0)XpUtil.addPlayerXP(player, -realAmount);
		} else {
			xpCon.givePlayerXp(player, amount);
		}
	}
	
	public static int getXPFromStack(ItemStack stack){
		if(ItemNBTHelper.verifyExistance(stack, BlockXPTank.TILE_NBT_STACK)){
        	NBTTagCompound nbt = ItemNBTHelper.getCompound(stack).getCompoundTag(BlockXPTank.TILE_NBT_STACK);
        	if(nbt.hasKey("XPStorage")){
        		return nbt.getCompoundTag("XPStorage").getInteger("experienceLevel");
        	}
        }
		return 0;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setTag("XPStorage", xpCon.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		xpCon.readFromNBT(nbt.getCompoundTag("XPStorage"));
	}
	
	@Override
	public void update(){
		super.update();
		IBlockState state = world.getBlockState(getPos());
		if(state.getBlock() == ModBlocks.xpTank && state.getValue(BlockXPTank.ENDER)){
			if(!getWorld().isBlockPowered(getPos())){
				TileEntityXPVacuum.vacuumXP(getWorld(), getPos(), xpCon, 8, 4.5, 1.5);
			}
		}
		
		if (getWorld().isRemote) return;
		if(shouldDoWorkThisTick(10)){
			if(xpCon.isDirty()){
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setInteger("XP", xpCon.getExperienceTotal());
				PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateXP", nbt);
				CrystalModNetwork.sendToAllAround(packet, this);
				xpCon.setDirty(false);
			}
		}
	}
	
	@Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facingIn) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facingIn);
    }

    @SuppressWarnings("unchecked")
	@Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            //noinspection unchecked
            return (T) new IFluidHandler() {
            	
            	public FluidTank getTank(){
            		return xpCon;
            	}
            	
            	@Override
				public int fill(FluidStack resource, boolean doFill) {
            		if (resource == null) {
                        return 0;
                    }
                    if (!resource.isFluidEqual(getTank().getFluid())) {
                        return 0;
                    }
            		return getTank().fill(resource, doFill);
                }

                @Override
				public FluidStack drain(int maxEmpty, boolean doDrain) {
                	return getTank().drain(maxEmpty, doDrain);
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
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateXP")){
			if(messageData.hasKey("XP")){
				xpCon.setExperience(messageData.getInteger("XP"));
			}
		}
	}

	@Override
	public void writeToStack(NBTTagCompound nbt) {
		this.writeCustomNBT(nbt);
	}

	@Override
	public void readFromStack(NBTTagCompound nbt) {
		this.readCustomNBT(nbt);
	}
}
