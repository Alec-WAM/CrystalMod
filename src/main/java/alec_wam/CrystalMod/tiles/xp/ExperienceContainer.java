package alec_wam.CrystalMod.tiles.xp;

import java.security.InvalidParameterException;

import javax.annotation.Nullable;

import alec_wam.CrystalMod.compatibility.FluidStackFixed;
import alec_wam.CrystalMod.compatibility.FluidTankFixed;
import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.util.XPUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class ExperienceContainer extends FluidTankFixed {
	// Note: We extend FluidTank instead of implementing IFluidTank because it has
	// some methods we need.

	private int experienceLevel;
	private float experience;
	private int experienceTotal;
	private boolean xpDirty;
	private final int maxXp;

	public ExperienceContainer() {
		this(Integer.MAX_VALUE);
	}

	public ExperienceContainer(int maxStored) {
		super(null, 0);
		maxXp = maxStored;
	}

	public int getMaximumExperiance() {    
		return maxXp;
	}

	public int getExperienceLevel() {
		return experienceLevel;
	}

	public float getExperience() {
		return experience;
	}

	public int getExperienceTotal() {
		return experienceTotal;
	}

	public boolean isDirty() {
		return xpDirty;
	}

	public void setDirty(boolean isDirty) {
		xpDirty = isDirty;
	}

	public void set(ExperienceContainer xpCon) {
		experienceTotal = xpCon.experienceTotal;
		experienceLevel = xpCon.experienceLevel;
		experience = xpCon.experience;    
		onContentsChanged();
	}

	public void setExperience(int xp){
		experienceTotal = xp;
		experienceLevel = XPUtil.getLevelForExperience(experienceTotal);    
		experience = (experienceTotal - XPUtil.getExperienceForLevel(experienceLevel)) / (float)getXpBarCapacity();
		xpDirty = true;
		onContentsChanged();
	}

	public int addExperience(int xpToAdd) {
		int j = maxXp - experienceTotal;
		if(xpToAdd > j) {
			xpToAdd = j;
		}

		experienceTotal += xpToAdd;
		experienceLevel = XPUtil.getLevelForExperience(experienceTotal);    
		experience = (experienceTotal - XPUtil.getExperienceForLevel(experienceLevel)) / (float)getXpBarCapacity();
		xpDirty = true;
		onContentsChanged();
		return xpToAdd;
	}

	public int getXpBarCapacity() {
		return XPUtil.getXpBarCapacity(experienceLevel);
	}

	public int getXpBarScaled(int scale) {
		int result = (int) (experience * scale);
		return result;

	}

	public void givePlayerXp(PlayerEntity player, int levels) {
		for (int i = 0; i < levels && experienceTotal > 0; i++) {
			givePlayerXpLevel(player);
		}
	}

	public void givePlayerXpLevel(PlayerEntity player) {
		int currentXP = XPUtil.getPlayerXP(player);
		int nextLevelXP = XPUtil.getExperienceForLevel(player.experienceLevel + 1);
		int requiredXP = nextLevelXP - currentXP;

		requiredXP = Math.min(experienceTotal, requiredXP);
		XPUtil.addPlayerXP(player, requiredXP);

		int newXp = experienceTotal - requiredXP;
		experience = 0;
		experienceLevel = 0;
		experienceTotal = 0;
		addExperience(newXp);
	}


	public void drainPlayerXpToReachContainerLevel(PlayerEntity player, int level) {    
		int targetXP = XPUtil.getExperienceForLevel(level);
		int requiredXP = targetXP - experienceTotal;
		if(requiredXP <= 0) {
			return;
		}
		int drainXP = Math.min(requiredXP, XPUtil.getPlayerXP(player));
		addExperience(drainXP);
		XPUtil.addPlayerXP(player, -drainXP);    
	}

	public void drainPlayerXpToReachPlayerLevel(PlayerEntity player, int level) {    
		int targetXP = XPUtil.getExperienceForLevel(level);
		int drainXP = XPUtil.getPlayerXP(player) - targetXP;
		if(drainXP <= 0) {
			return;
		}    
		drainXP = addExperience(drainXP);
		if(drainXP > 0) {
			XPUtil.addPlayerXP(player, -drainXP);
		}
	}

	public FluidStackFixed drain(Direction from, FluidStack resource, boolean doDrain) {
		if(resource == null || !canDrain(from, resource.getFluid())) {
			return null;
		}    
		return drain(from, resource.amount, doDrain);
	}


	public FluidStackFixed drain(Direction from, int maxDrain, boolean doDrain) {
		if(FixedFluidRegistry.XP == null) {
			return null;
		}
		int available = getFluidAmount();
		int toDrain = Math.min(available, maxDrain);
		final int xpAskedToExtract = XPUtil.liquidToExperience(toDrain);
		// only return multiples of 1 XP (20mB) to avoid duping XP when being asked
		// for low values (like 10mB/t)
		final int fluidToExtract = XPUtil.experienceToLiquid(xpAskedToExtract);
		final int xpToExtract = XPUtil.liquidToExperience(fluidToExtract);
		if(doDrain) {      
			int newXp = experienceTotal - xpToExtract;
			experience = 0;
			experienceLevel = 0;
			experienceTotal = 0;
			addExperience(newXp);
		}
		return new FluidStackFixed(FixedFluidRegistry.XP, fluidToExtract);
	}

	public boolean canFill(Direction from, Fluid fluidIn) {
		return fluidIn != null && FixedFluidRegistry.XP != null && areFluidsTheSame(fluidIn, FixedFluidRegistry.XP);
	}

	public boolean areFluidsTheSame(Fluid fluid, Fluid fluid2) {
		if (fluid == null) {
			return fluid2 == null;
		}
		if (fluid2 == null) {
			return false;
		}
		return fluid == fluid2 || fluid.getName().equals(fluid2.getName());
	}

	public int fill(Direction from, FluidStack resource, boolean doFill) {
		if(resource == null) {
			return 0;
		}
		if(resource.amount <= 0) {
			return 0;
		}
		if(!canFill(from, resource.getFluid())) {
			return 0;
		}
		//need to do these calcs in XP instead of fluid space to avoid type overflows
		int xp = XPUtil.liquidToExperience(resource.amount);
		int xpSpace = getMaximumExperiance() - getExperienceTotal();
		int canFillXP = Math.min(xp, xpSpace);
		if(canFillXP <= 0) {
			return 0;
		}
		if(doFill) {
			addExperience(canFillXP);
		}
		return XPUtil.experienceToLiquid(canFillXP);
	}

	public boolean canDrain(Direction from, Fluid fluidIn) {
		return fluidIn != null && FixedFluidRegistry.XP != null && areFluidsTheSame(fluidIn, FixedFluidRegistry.XP);
	}

	public FluidTankInfo[] getTankInfo(Direction from) {
		if(FixedFluidRegistry.XP == null) {
			return new FluidTankInfo[0];
		}
		return new FluidTankInfo[] {
				new FluidTankInfo(new FluidStackFixed(FixedFluidRegistry.XP, getFluidAmount()), getCapacity())
		};
	}

	@Override
	public int getCapacity() {
		if(maxXp == Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		}
		return XPUtil.experienceToLiquid(maxXp);
	}

	@Override
	public int getFluidAmount() {
		return XPUtil.experienceToLiquid(experienceTotal);
	}

	@Override
	public FluidTankFixed readFromNBT(CompoundNBT nbtRoot) {
		experienceLevel = nbtRoot.getInt("experienceLevel");
		experienceTotal = nbtRoot.getInt("experienceTotal");
		experience = nbtRoot.getFloat("experience");
		return this;
	}


	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbtRoot) {
		nbtRoot.putInt("experienceLevel", experienceLevel);
		nbtRoot.putInt("experienceTotal", experienceTotal);
		nbtRoot.putFloat("experience", experience);
		return nbtRoot;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(experienceTotal);
		buf.writeInt(experienceLevel);
		buf.writeFloat(experience);    
	}

	public void fromBytes(ByteBuf buf) {
		experienceTotal = buf.readInt();
		experienceLevel = buf.readInt();
		experience = buf.readFloat();
	}


	@Override
	public FluidStackFixed getFluid() {
		return new FluidStackFixed(FixedFluidRegistry.XP, getFluidAmount());
	}

	@Override
	public FluidTankInfo getInfo() {
		return getTankInfo(null)[0];
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		return fill(null, resource, doFill);
	}

	@Override
	public FluidStackFixed drain(int maxDrain, boolean doDrain) {
		return drain(null, maxDrain, doDrain);
	}

	@Override
	public FluidStackFixed drain(FluidStack resource, boolean doDrain) {
		return drain(null, resource, doDrain);
	}

	@Override
	public void setFluid(@Nullable FluidStack fluid) {
		experience = 0;
		experienceLevel = 0;
		experienceTotal = 0;
		if (fluid != null && fluid.getFluid() != null) {
			if (FixedFluidRegistry.XP == fluid.getFluid()) {
				addExperience(XPUtil.liquidToExperience(fluid.amount));
			} else {
				throw new InvalidParameterException(fluid.getFluid() + " is no XP juice");
			}
		}
		xpDirty = true;
	}

	@Override
	public void setCapacity(int capacity) {
		throw new InvalidParameterException();
	}

	@Override
	protected void onContentsChanged() {
		super.onContentsChanged();
		if (tile != null) {
			tile.markDirty();
		}
	}

}
