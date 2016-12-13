package alec_wam.CrystalMod.tiles.machine.power.engine.vampire;

import java.util.List;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityEngineVampire extends TileEntityEngineBase {

	@Override
	public int drainCEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(120000*multi, 60*multi);
	}

	@Override
	public void refuel() {
		BlockPos min = getPos();
		BlockPos max = getPos().up();
		List<EntityLivingBase> entites = getWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(min, max).expand(2, 1, 2));
		int mobsAttacked = 0;
		int fuelAmt = 0;
		if(!entites.isEmpty()){
			for(EntityLivingBase entity : entites){
				if(entity.isEntityAlive() && !(entity instanceof EntityPlayer)){
					if(entity.attackEntityFrom(DamageSource.wither, 1)){
						
						//One pig is worth 1/4 of a piece of coal in a furnace generator. At 40 ticks * 30 CU per tick
						
						int value = 1;
						
						if(entity.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD){
							value = 2;
						}
						
						fuelAmt+=80*value;
						
						mobsAttacked++;
						if(mobsAttacked >= Config.engine_vampire_maxattack){
							break;
						}
					}
				}
			}
		}
		if(fuelAmt > 0){
			fuel.add(fuelAmt);
			maxFuel.setValue(fuel.getValue());
		}
	}

	@Override
	public int getFuelValue() {
		return 15;
	}

}
