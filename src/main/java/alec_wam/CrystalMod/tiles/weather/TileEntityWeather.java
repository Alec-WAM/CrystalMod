package alec_wam.CrystalMod.tiles.weather;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityWeather extends TileEntityMod implements IMessageHandler {

	//Time until next rain (World.getRainTime())
	//Remaining Strength (World.getRainStrength(1.0F))
	public int clearTime, rainTime, thunderTime;

	@Override
	public void update(){
		super.update();
		if(!getWorld().isRemote){
			int worldClearTime = getWorld().getWorldInfo().getCleanWeatherTime();
			int worldRainTime = getWorld().getWorldInfo().getRainTime();
			int worldThunderTime = getWorld().getWorldInfo().getThunderTime();

			if(clearTime != worldClearTime && shouldDoWorkThisTick(15)) {
				clearTime = worldClearTime;
		    	NBTTagCompound nbt = new NBTTagCompound();
		    	nbt.setInteger("Time", clearTime);
		    	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateClearTime", nbt), this);
		    }
			if(rainTime != worldRainTime && shouldDoWorkThisTick(15)) {
		    	rainTime = worldRainTime;
		    	NBTTagCompound nbt = new NBTTagCompound();
		    	nbt.setInteger("Time", rainTime);
		    	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateRainTime", nbt), this);
		    }
			if(thunderTime != worldThunderTime && shouldDoWorkThisTick(15)) {
				thunderTime = worldThunderTime;
		    	NBTTagCompound nbt = new NBTTagCompound();
		    	nbt.setInteger("Time", thunderTime);
		    	CrystalModNetwork.sendToAllAround(new PacketTileMessage(getPos(), "UpdateThunderTime", nbt), this);
		    }
		}
	}
	
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData,	boolean client) {
		if(messageId.equalsIgnoreCase("UpdateClearTime")){
			clearTime = messageData.getInteger("Time");
		}
		if(messageId.equalsIgnoreCase("UpdateRainTime")){
			rainTime = messageData.getInteger("Time");
		}
		if(messageId.equalsIgnoreCase("UpdateThunderTime")){
			thunderTime = messageData.getInteger("Time");
		}
	}
	
}
