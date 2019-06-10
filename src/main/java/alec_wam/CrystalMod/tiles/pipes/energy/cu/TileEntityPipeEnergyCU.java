package alec_wam.CrystalMod.tiles.pipes.energy.cu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import alec_wam.CrystalMod.api.energy.CapabilityCrystalEnergy;
import alec_wam.CrystalMod.api.energy.ICEnergyStorage;
import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.init.ModBlocks;
import alec_wam.CrystalMod.tiles.pipes.NetworkType;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.energy.IEnergyNode;
import alec_wam.CrystalMod.tiles.pipes.energy.TileEntityPipeEnergy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class TileEntityPipeEnergyCU extends TileEntityPipeEnergy {
	@SuppressWarnings("unchecked")
	private final LazyOptional<ICEnergyStorage>[] energyHandlers = new LazyOptional[6];	
	
	public TileEntityPipeEnergyCU() {
		this(EnumCrystalColorSpecial.BLUE);
	}
	
	public TileEntityPipeEnergyCU(EnumCrystalColorSpecial color) {
		super(color, ModBlocks.pipeEnergyCUGroup.getTileType(color));
		for(Direction facing : Direction.values()){
			energyHandlers[facing.getIndex()] = LazyOptional.of(() -> new PipeEnergyStorage(this, facing));
    	}
	}
	
	@Override
	public NetworkType getNetworkType() {
		return NetworkType.POWERCU;
	}
	
	@Override
	public IEnergyNode getExternalEnergyNode(Direction facing) {
		World world = getWorld();
	    if(world == null) {
	      return null;
	    }
	    BlockPos loc = getPos().offset(facing);
	    TileEntity te = world.getTileEntity(loc);
	    if(te !=null) {
	    	if(te instanceof TileEntityPipeEnergyCU)return null;
	    	ICEnergyStorage storage = te.getCapability(CapabilityCrystalEnergy.CENERGY, facing.getOpposite()).orElse(null);
	    	if(storage !=null){
	    		return new IEnergyNode(){

					@Override
					public Object getHost() {
						return storage;
					}

					@Override
					public int fillEnergy(int amount, boolean simulate) {
						return storage.fillCEnergy(amount, simulate);
					}

					@Override
					public int drainEnergy(int amount, boolean simulate) {
						return storage.drainCEnergy(amount, simulate);
					}

					@Override
					public int getEnergy() {
						return storage.getCEnergyStored();
					}

					@Override
					public int getMaxEnergy() {
						return storage.getMaxCEnergyStored();
					}
	    			
	    		};
	    	}
	    }
	    
	    return null;
	}

	@Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (side !=null && cap == CapabilityCrystalEnergy.CENERGY && getConnectionSetting(side) != PipeConnectionMode.DISABLED){
            return energyHandlers[side.getIndex()].cast();
        }
        return super.getCapability(cap, side);
    }
	
	private class PipeEnergyStorage implements ICEnergyStorage {
		private Direction facing;
		private TileEntityPipeEnergyCU pipe;
		
		public PipeEnergyStorage(TileEntityPipeEnergyCU pipe, Direction facing){
			this.facing = facing;
			this.pipe = pipe;
		}
		
		@Override
		public int fillCEnergy(int maxReceive, boolean simulate) {
			if(!canReceive())return 0;
			return pipe.fillEnergy(facing, maxReceive, simulate);
		}

		@Override
		public int drainCEnergy(int maxExtract, boolean simulate) {
			return 0;
		}

		@Override
		public int getCEnergyStored() {
			return pipe.getEnergyStored();
		}

		@Override
		public int getMaxCEnergyStored() {
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
