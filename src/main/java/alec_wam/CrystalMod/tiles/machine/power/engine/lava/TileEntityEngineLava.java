package alec_wam.CrystalMod.tiles.machine.power.engine.lava;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import alec_wam.CrystalMod.tiles.machine.power.engine.TileEntityEngineFluid;

public class TileEntityEngineLava extends TileEntityEngineFluid {

	public TileEntityEngineLava(){
		super(4);
	}
	
	public int getFuelEnergyValue(FluidStack stack){
		if(stack.getFluid() == FluidRegistry.LAVA){
			return 50;
		}
		return 0;
	}
	
	public int getFuelUsage(){
		return 50;
	}
	
	public int getFuelValue(){
		return 30;
	}

}
