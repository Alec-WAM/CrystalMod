package alec_wam.CrystalMod.tiles.machine.power.converter;

import net.minecraft.nbt.NBTTagCompound;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;

public abstract class TileEnergyConveterBase extends TileEntityMod implements IMessageHandler {

	public abstract PowerUnits getUnitType();
	
	protected abstract void setEnergyStored(int power);
	protected abstract int getEnergyStored();
	protected abstract int getMaxEnergyStored();
	
	protected float lastSyncPowerStored = -1;
	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
		    if(powerChanged) {
		      lastSyncPowerStored = getEnergyStored();
		      NBTTagCompound nbt = new NBTTagCompound();
		      nbt.setInteger("Power", getEnergyStored());
		      CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdatePower", nbt), this);
		    }
		}
		
	}
	
	protected final int getPowerNeeded( final PowerUnits externalUnit, final int maxPowerRequired )
	{
		return getUnitType().convertTo( externalUnit, Math.max( 0, getFunnelPowerNeeded( externalUnit.convertTo( getUnitType(), maxPowerRequired ) ) ) );
	}
	
	protected int getFunnelPowerNeeded( final double maxRequired )
	{
		return getMaxEnergyStored() - getEnergyStored();
	}
	
	public final int injectExternalPower( final PowerUnits input, final int amt )
	{
		return getUnitType().convertTo( input, this.funnelPowerIntoStorage( input.convertTo( getUnitType(), amt ), false ) );
	}

	protected int funnelPowerIntoStorage( int amt, final boolean sim )
	{
		if( sim )
		{
			final int fakeBattery = getEnergyStored() + amt;

			if( fakeBattery > getMaxEnergyStored() )
			{
				return fakeBattery - getMaxEnergyStored();
			}

			return 0;
		}
		else
		{
			final int old = getEnergyStored();
			setEnergyStored( getEnergyStored() + amt );
			if( old+amt > getMaxEnergyStored() )
			{
				amt = old - getMaxEnergyStored();
				return amt;
			}

			return 0;
		}
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdatePower")){
			int newPower = messageData.getInteger("Power");
			setEnergyStored(newPower);
		}
	}

}
