package alec_wam.CrystalMod.tiles.machine.seismic;

import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.IMessageHandler;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.tiles.machine.seismic.SeismicData.SeismicShape;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySeismicScanner extends TileEntityMod implements IMessageHandler {

	public SeismicData seismicData;
	public int radius = 8;
	public SeismicData.SeismicShape scanShape = SeismicShape.SQUARE;
	
	@SideOnly(Side.CLIENT)
	public void clientScan(){
		CrystalModNetwork.sendToServer(new PacketTileMessage(getPos(), MESSAGE_SCAN));
	}
	
	public void scan(){
		ModLogger.info("Seismic Scanning....");
		seismicData = SeismicData.collectData(getWorld(), getPos().down(), getPos().down().getY(), radius, scanShape);
	}
	
	public void clear(){
		seismicData = null;
	}
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		if(seismicData !=null){
			nbt.setTag("ScanData", seismicData.saveToNBT());
		}
		nbt.setInteger("Radius", radius);
		nbt.setByte("Shape", (byte)scanShape.ordinal());
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		if(nbt.hasKey("ScanData")){
			this.seismicData = SeismicData.loadFromNBT(nbt.getCompoundTag("ScanData"));
			ModLogger.info("Loaded "+seismicData.getLayers().length+" layers");
		}
		this.radius = nbt.getInteger("Radius");
		this.scanShape = SeismicShape.values()[nbt.getByte("Shape")];
	}
	
	public static final String MESSAGE_SCAN = "Scan";
	public static final String MESSAGE_CLEAR = "Clear";
	public static final String MESSAGE_RADIUS = "Radius";
	@Override
	public void handleMessage(String messageId, NBTTagCompound messageData, boolean client) {
		if(messageId.equalsIgnoreCase(MESSAGE_SCAN)){
			ModLogger.info("scan");
			scan();
			BlockUtil.markBlockForUpdate(getWorld(), getPos());
		}
		if(messageId.equalsIgnoreCase(MESSAGE_CLEAR)){
			clear();
		}
		if(messageId.equalsIgnoreCase(MESSAGE_RADIUS)){
			radius = messageData.getInteger("Value");
		}
	}
	
}
