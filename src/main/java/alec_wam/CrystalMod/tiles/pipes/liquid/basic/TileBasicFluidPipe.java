package alec_wam.CrystalMod.tiles.pipes.liquid.basic;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileBasicFluidPipe extends TileEntityMod {

	public static final int CAPACITY = 1 * Fluid.BUCKET_VOLUME;
	public FluidStack pipeFluid;
	
	
	@Override
	public void update(){
		super.update();
	}
	
	public List<EnumFacing> findPath(){
		List<EnumFacing> list = Lists.newArrayList();
		for(EnumFacing dir : EnumFacing.VALUES){
			TileEntity tile = world.getTileEntity(getPos().offset(dir));
			if(tile !=null){
				if(tile instanceof TileBasicFluidPipe){
					TileBasicFluidPipe pipe = (TileBasicFluidPipe)tile;
					if(dir == EnumFacing.UP){
						if(world.getBlockState(getPos().down()) == Blocks.GOLD_BLOCK.getDefaultState()){
							list.clear();
							list.add(EnumFacing.UP);
							return list;
						}
					}
					//Always flow sideways
					if(dir.getAxis().isHorizontal()){
						list.add(dir);
					}
				}
			}
		}
		return list;
	}
	
	public int acceptPayload(FluidPayload payload, int sendAmt, boolean sim){
		if(pipeFluid == null || payload.fluid.isFluidEqual(pipeFluid)){
			return addFluid(payload.fluid, sendAmt, payload.fromDirection, sim);
		}
		return 0;
	}
	
	public int addFluid(FluidStack fluid, int amt, EnumFacing from, boolean sim){
		int send = Math.min(fluid.amount, amt);
		if(pipeFluid == null || pipeFluid.isFluidEqual(fluid) && pipeFluid.amount + send <= CAPACITY){
			int added = Math.min(CAPACITY - pipeFluid.amount, send);
			if(!sim){
				if(pipeFluid == null){
					pipeFluid = fluid.copy();
				} else {
					pipeFluid.amount += added;
				}
			}
			return added;
		}
		return 0;
	}
	
	public class FluidPayload {
		public FluidStack fluid;
		public EnumFacing fromDirection;
	}
}
