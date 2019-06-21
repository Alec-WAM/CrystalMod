package alec_wam.CrystalMod.tiles.xp;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.client.gui.overlay.IOvelayTile;
import alec_wam.CrystalMod.client.gui.overlay.InfoProviderTank;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.tiles.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.XPUtil;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityXPVacuum extends TileEntityMod implements IMessageHandler, IOvelayTile {
	//TODO Add NBT item drop
	public ExperienceContainer xpCon;
	private final LazyOptional<IFluidHandler> holder;
	private final InfoProvider info;
	
	public TileEntityXPVacuum(){
		super(ModBlocks.TILE_XP_VACUUM);
		xpCon = new ExperienceContainer(XPUtil.getExperienceForLevel(30));
	    xpCon.setCanFill(false);
	    this.info = new InfoProviderTank(xpCon);
		this.holder = LazyOptional.of(() -> xpCon);
	}
	
	@Override
	public void writeCustomNBT(CompoundNBT nbt){
		super.writeCustomNBT(nbt);
		nbt.put("XPStorage", xpCon.writeToNBT(new CompoundNBT()));
	}
	
	@Override
	public void readCustomNBT(CompoundNBT nbt){
		super.readCustomNBT(nbt);
		xpCon.readFromNBT(nbt.getCompound("XPStorage"));
	}
	
	@Override
	public void tick(){
		super.tick();

		if(!getWorld().isBlockPowered(getPos())){
			vacuumXP(getWorld(), getPos(), xpCon, 8, 4.5, 1.5);
		}
		if(!getWorld().isRemote){
			for(Direction dir : Direction.values()){
				TileEntity tile = world.getTileEntity(getPos().offset(dir));
				if(tile !=null && tile instanceof TileEntityXPTank){
					TileEntityXPTank tank = (TileEntityXPTank)tile;
					
					IFluidHandler handler = FluidUtil.getFluidHandler(tank.getWorld(), tank.getPos(), dir.getOpposite()).orElse(null);
					if(handler !=null){
						int fillCheck = handler.fill(xpCon.drain(100, false), false);
						if(fillCheck > 0){
							FluidStack stack = xpCon.drain(fillCheck, true);
							handler.fill(stack, true);
						}
					}
				}
			}
			if(shouldDoWorkThisTick(10)){
				if(xpCon.isDirty()){
					CompoundNBT nbt = new CompoundNBT();
					nbt.putInt("XP", xpCon.getExperienceTotal());
					PacketTileMessage packet = new PacketTileMessage(getPos(), "UpdateXP", nbt);
					CrystalModNetwork.sendToAllAround(packet, this);
					xpCon.setDirty(false);
				}
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
	
	public static void vacuumXP(World world, BlockPos pos, ExperienceContainer xpCon, double boxSize, double range, double pickUpDistance) {
		vacuumXP(world, pos, xpCon, new AxisAlignedBB(pos).grow(boxSize, boxSize, boxSize), range, pickUpDistance);
	}
	
	public static void vacuumXP(World world, BlockPos pos, ExperienceContainer xpCon, AxisAlignedBB bb, double range, double pickUpDistance){
		double maxDist = range * 2;

		List<ExperienceOrbEntity> xp = world.getEntitiesWithinAABB(ExperienceOrbEntity.class, bb, EntityPredicates.IS_ALIVE);
		for (ExperienceOrbEntity entity : xp) {
			double xDist = (pos.getX() + 0.5D - entity.posX);
			double yDist = (pos.getY() + 0.5D - entity.posY);
			double zDist = (pos.getZ() + 0.5D - entity.posZ);

			double totalDistance = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);

			if (totalDistance < pickUpDistance) {
				if (entity.isAlive()) {
					int xpValue = entity.getXpValue();
					if (xpValue > 0) {
						int added = xpCon.addExperience(xpValue);
						if(added > 0){
							entity.xpValue-=added;
							if(entity.xpValue <=0){
								if(!world.isRemote){
									world.playSound(null, pos, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.05F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.35F + 0.9F);
								}
								entity.remove();
							}
						}
					}
				}
			} else if(xpCon.getFluidAmount() < xpCon.getCapacity()){
				double d = 1 - (Math.max(0.1, totalDistance) / maxDist);
				double speed = 0.01 + (d * 0.02);
				double x = entity.getMotion().x + (xDist / totalDistance * speed);
				double z = entity.getMotion().z + (zDist / totalDistance * speed);
				double y = entity.getMotion().y + (yDist / totalDistance * speed);
				if (yDist > 0.5) {
					y = 0.12;
				}
				entity.setMotion(x, y, z);
			}
		}
	}
	
	@Override
	public void handleMessage(String messageId, CompoundNBT messageData, boolean client) {
		if(messageId.equalsIgnoreCase("UpdateXP")){
			if(messageData.contains("XP")){
				xpCon.setExperience(messageData.getInt("XP"));
				xpCon.setDirty(false);
			}
		}
	}

	//TODO Make tiny XP bar infoprovider
	@Override
	public InfoProvider getInfo() {
		return info;
	}
	
}
