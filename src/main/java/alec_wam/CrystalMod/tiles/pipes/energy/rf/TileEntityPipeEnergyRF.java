package alec_wam.CrystalMod.tiles.pipes.energy.rf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.energy.IEnergyNode;
import alec_wam.CrystalMod.tiles.pipes.energy.TileEntityPipeEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityPipeEnergyRF extends TileEntityPipeEnergy {
	@SuppressWarnings("unchecked")
	private final LazyOptional<IEnergyStorage>[] energyHandlers = new LazyOptional[6];	
	
	public TileEntityPipeEnergyRF() {
		this(EnumCrystalColorSpecial.BLUE);
	}
	
	public TileEntityPipeEnergyRF(EnumCrystalColorSpecial color) {
		super(color, ModBlocks.TILE_PIPE_ENERGY_RF);
		for(EnumFacing facing : EnumFacing.values()){
			energyHandlers[facing.getIndex()] = LazyOptional.of(() -> new PipeEnergyStorage(this, facing));
    	}
	}
	
	@Override
	public NetworkType getNetworkType() {
		return NetworkType.POWERRF;
	}
	
	@Override
	public IEnergyNode getExternalEnergyNode(EnumFacing facing) {
		World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(facing);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null) {
	    	if(te instanceof TileEntityPipeEnergyRF)return null;
	    	IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite()).orElse(null);
	    	if(storage !=null){
	    		return new IEnergyNode(){

					@Override
					public Object getHost() {
						return storage;
					}

					@Override
					public int fillEnergy(int amount, boolean simulate) {
						return storage.receiveEnergy(amount, simulate);
					}

					@Override
					public int drainEnergy(int amount, boolean simulate) {
						return storage.extractEnergy(amount, simulate);
					}

					@Override
					public int getEnergy() {
						return storage.getEnergyStored();
					}

					@Override
					public int getMaxEnergy() {
						return storage.getMaxEnergyStored();
					}
	    			
	    		};
	    	}
	    }
	    
	    return null;
	}

	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side)
    {
        if (side !=null && cap == CapabilityEnergy.ENERGY && getConnectionSetting(side) != PipeConnectionMode.DISABLED){
            return energyHandlers[side.getIndex()].cast();
        }
        return super.getCapability(cap, side);
    }
	
	private class PipeEnergyStorage implements IEnergyStorage {
		private EnumFacing facing;
		private TileEntityPipeEnergyRF pipe;
		
		public PipeEnergyStorage(TileEntityPipeEnergyRF pipe, EnumFacing facing){
			this.facing = facing;
			this.pipe = pipe;
		}
		
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			if(!canReceive())return 0;
			return pipe.fillEnergy(facing, maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getEnergyStored() {
			return pipe.getEnergyStored();
		}

		@Override
		public int getMaxEnergyStored() {
			return pipe.getMaxEnergyStored();
		}

		@Override
		public boolean canExtract() {
			return false;
		}

		@Override
		public boolean canReceive() {
			PipeConnectionMode mode = getConnectionSetting(facing); 
			return mode == PipeConnectionMode.BOTH || mode == PipeConnectionMode.IN;
		}
    	
    };
	
}
