package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineFluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityEngineLava extends TileEntityEngineFluid {

	public TileEntityEngineLava(){
		super(4);
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
