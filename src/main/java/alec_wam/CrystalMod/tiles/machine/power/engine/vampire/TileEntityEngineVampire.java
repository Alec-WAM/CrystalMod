package alec_wam.CrystalMod.tiles.machine.power.engine.vampire;

import java.util.List;
import java.util.Random;

import alec_wam.CrystalMod.Config;
import alec_wam.CrystalMod.api.energy.CEnergyStorage;
import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityEngineVampire extends TileEntityEngineBase {

	@Override
	public CEnergyStorage createStorage(int multi) {
		return new CEnergyStorage(120000*multi, 60*multi) {
			@Override
			public boolean canReceive(){
				return false;
			}
		};
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
					if(entity.attackEntityFrom(DamageSource.WITHER, 1)){
						
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
	
	@Override
	public void update(){
		super.update();
		if (getWorld().isRemote) {
			if (this.isActive()) {
				Random rand = getWorld().rand;
				if (this.shouldDoWorkThisTick(10)) {
					EnumFacing enumfacing = EnumFacing.getHorizontal(facing);
					double d0 = (double) pos.getX() + 0.5D;
					double d1 = (double) pos.getY() + 0.6D;
					double d2 = (double) pos.getZ() + 0.5D;
					double d3 = 0.52D;
					double d4 = rand.nextDouble() * 0.4D - 0.2D;

					if (rand.nextDouble() < 0.1D) {
						getWorld().playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
					}

					double x = d0;
					double y = d1;
					double z = d2;
					switch (enumfacing)
					{
						case WEST:
							x = d0 - d3;
							y = d1;
							z = d2 + d4;
							break;
						case EAST:
							x = d0 + d3;
							y = d1;
							z = d2 + d4;
							break;
						case NORTH:
							x = d0 + d4;
							y = d1;
							z = d2 - d3;
							break;
						case SOUTH:
							x = d0 + d4;
							y = d1;
							z = d2 + d3;
					}
					this.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0, 0, 0, new int[0]);
				}
			}
		}
	}

}
