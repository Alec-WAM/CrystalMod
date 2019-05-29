package alec_wam.CrystalMod.tiles.pipes.energy;

import alec_wam.CrystalMod.core.color.EnumCrystalColorSpecial;
import alec_wam.CrystalMod.tiles.pipes.BlockPipe.PipeHitData;
import alec_wam.CrystalMod.tiles.pipes.PipeConnectionMode;
import alec_wam.CrystalMod.tiles.pipes.PipeNetworkBuilder.PipeChecker;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipeBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public abstract class TileEntityPipeEnergy extends TileEntityPipeBase {
	public static final int[] MAX_STORAGE = { 640, 5120, 20480, 40960, 40960};
	public static final int[] MAX_IO = new int[]{640, 5120, 20480, 40960, Integer.MAX_VALUE};
	
	private int energyStored;
	private int tier;
	
	public TileEntityPipeEnergy(EnumCrystalColorSpecial color, TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
		this.tier = color.ordinal();
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setInt("Tier", tier);
		nbt.setInt("Energy", energyStored);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		this.tier = nbt.getInt("Tier");
		this.energyStored = nbt.getInt("Energy");
	}

	@Override
	public PipeNetworkEnergy createNewNetwork() {
		return new PipeNetworkEnergy();
	}
	
	@Override
	public boolean canConnectToExternal(EnumFacing facing, boolean ignore){
		return getExternalEnergyNode(facing) !=null;
	}
	
	public static class PipeCheckerEnergy extends PipeChecker {
		private int tier;
		public PipeCheckerEnergy(int tier){
			this.tier = tier;
		}
		
		@Override
		public boolean canConnect(TileEntityPipeBase otherPipe) {
			if(otherPipe instanceof TileEntityPipeEnergy){
				int otherTier = ((TileEntityPipeEnergy)otherPipe).tier;
				return otherTier == tier;
			}
			return false;
		}
		
	};
	
	public static final PipeCheckerEnergy[] CHECKERS = new PipeCheckerEnergy[] {new PipeCheckerEnergy(0), new PipeCheckerEnergy(1), new PipeCheckerEnergy(2), new PipeCheckerEnergy(3), new PipeCheckerEnergy(4)};
	
	@Override
	public PipeChecker getPipeChecker(){
		return CHECKERS[tier];
	}

	@Override
	public void externalConnectionAdded(EnumFacing direction) {
		super.externalConnectionAdded(direction);
		if(network != null && network instanceof PipeNetworkEnergy) {
			PipeNetworkEnergy net = (PipeNetworkEnergy)network;
			IEnergyNode node = getExternalEnergyNode(direction);
			if(node !=null && !(node.getHost() instanceof TileEntityPipeEnergy)){
				net.powerNodeAdded(this, direction, node);
			}
		}
	}

	@Override
	public void externalConnectionRemoved(EnumFacing direction) {
		externalConnections.remove(direction);
		if(network != null && network instanceof PipeNetworkEnergy) {
			((PipeNetworkEnergy)network).powerNodeRemoved(getNetworkPos().offset(direction));
		}
	}
	
	@Override
	public boolean onActivated(World world, EntityPlayer player, EnumHand hand, PipeHitData hitData) {
		return false;
	}
	
	public abstract IEnergyNode getExternalEnergyNode(EnumFacing facing);
	
	@Override
	public boolean openConnector(EntityPlayer player, EnumHand hand, EnumFacing side){
		return false;
	}

	public int getMaxEnergyRecieved(EnumFacing dir) {
		PipeConnectionMode mode = getConnectionSetting(dir);
		if((mode == PipeConnectionMode.OUT || mode == PipeConnectionMode.DISABLED)) {
			return 0;
		}
		return MAX_IO[tier];
	}
	
	public int getMaxEnergyExtracted(EnumFacing direction) {
		PipeConnectionMode mode = getConnectionSetting(direction);
		if(mode == PipeConnectionMode.IN || mode == PipeConnectionMode.DISABLED) {
			return 0;
		}
		return MAX_IO[tier];
	}

	public int getEnergyStored() {
		return energyStored;
	}

	public int getMaxEnergyStored() {
		return MAX_STORAGE[tier];
	}

	public void setEnergyStored(int energy) {
		this.energyStored = energy;
	}
	
	public int fillEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(getMaxEnergyRecieved(from) == 0 || maxExtract <= 0) {
			return 0;
		}
		if(tier == EnumCrystalColorSpecial.PURE.ordinal()){
			if(network == null)return 0;
			if(network != null && network instanceof PipeNetworkEnergy) {
				PipeNetworkEnergy energyNet = (PipeNetworkEnergy)network;
				return energyNet.getPowerDistributor().freeSend(maxExtract, simulate);
			}
		} 
		int freeSpace = getMaxEnergyStored() - getEnergyStored();
		int result = Math.min(maxExtract, freeSpace);
		if(!simulate && result > 0) {
			setEnergyStored(getEnergyStored() + result);      
		}
		return result;
	}

}
