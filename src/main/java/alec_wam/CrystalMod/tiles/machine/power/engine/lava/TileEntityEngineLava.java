package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import java.util.Random;

import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineFluid;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityEngineLava extends TileEntityEngineFluid {

	public TileEntityEngineLava(){
		super(4);
	}
	
	@Override
	public void update() {
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
					this.world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0, new int[0]);
				}
			}
		}
	}
	
	@Override
	public int getFuelEnergyValue(FluidStack stack){
		if(stack.getFluid() == FluidRegistry.LAVA){
			return 50;
		}
		return 0;
	}
	
	@Override
	public int getFuelUsage(){
		return 50;
	}
	
	@Override
	public int getFuelValue(){
		return 30;
	}

}
