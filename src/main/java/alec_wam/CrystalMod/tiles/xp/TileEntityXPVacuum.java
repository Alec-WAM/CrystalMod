package alec_wam.CrystalMod.tiles.xp;

import java.util.List;

import alec_wam.CrystalMod.fluids.XpUtil;
import alec_wam.CrystalMod.fluids.xp.ExperienceContainer;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.FluidUtil;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class TileEntityXPVacuum extends TileEntityMod implements IMessageHandler {
	//TODO Add NBT item drop
	public ExperienceContainer xpCon;
	
	public TileEntityXPVacuum(){
		xpCon = new ExperienceContainer(XpUtil.getExperienceForLevel(30));
	    xpCon.setCanFill(false);
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

		if(!getWorld().isBlockPowered(getPos())){
			vacuumXP(getWorld(), getPos(), xpCon, 8, 4.5, 1.5);
		}
		if(!getWorld().isRemote){
			for(EnumFacing dir : EnumFacing.VALUES){
				TileEntity tile = world.getTileEntity(getPos().offset(dir));
				if(tile !=null && tile instanceof TileEntityXPTank){
					TileEntityXPTank tank = (TileEntityXPTank)tile;
					
					IFluidHandler handler = FluidUtil.getFluidHandler(tank, dir.getOpposite());
					int fillCheck = handler.fill(xpCon.drain(20, false), false);
					if(fillCheck > 0){
						FluidStack stack = xpCon.drain(fillCheck, true);
						handler.fill(stack, true);
					}
				}
			}
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
                   return 0;
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
	
	public static void vacuumXP(World world, BlockPos pos, ExperienceContainer xpCon, double boxSize, double range, double pickUpDistance) {
		vacuumXP(world, pos, xpCon, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(boxSize, boxSize, boxSize), range, pickUpDistance);
	}
	
	public static void vacuumXP(World world, BlockPos pos, ExperienceContainer xpCon, AxisAlignedBB bb, double range, double pickUpDistance){
		double maxDist = range * 2;

		List<EntityXPOrb> xp = world.getEntitiesWithinAABB(EntityXPOrb.class, bb);

		for (EntityXPOrb entity : xp) {
			double xDist = (pos.getX() + 0.5D - entity.posX);
			double yDist = (pos.getY() + 0.5D - entity.posY);
			double zDist = (pos.getZ() + 0.5D - entity.posZ);

			double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

			if (totalDistance < pickUpDistance) {
				if (!entity.isDead) {
					int xpValue = entity.getXpValue();
					if (xpValue > 0) {
						int added = xpCon.addExperience(xpValue);
						if(added > 0){
							entity.xpValue-=added;
							if(entity.xpValue <=0){
								entity.setDead();
							}
						}
					}
				}
			} else if(xpCon.getFluidAmount() < xpCon.getCapacity()){
				double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
				double speed = 0.01 + (d * 0.02);

				entity.motionX += xDist / totalDistance * speed;
				entity.motionZ += zDist / totalDistance * speed;
				entity.motionY += yDist / totalDistance * speed;
				if (yDist > 0.5) {
					entity.motionY = 0.12;
				}
			}
		}
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateXP")){
			if(messageData.hasKey("XP")){
				xpCon.setExperience(messageData.getInteger("XP"));
				xpCon.setDirty(false);
			}
		}
	}
	
}
