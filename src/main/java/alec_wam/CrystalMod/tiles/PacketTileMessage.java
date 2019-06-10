package alec_wam.CrystalMod.tiles;

import alec_wam.CrystalMod.network.AbstractPacket;
import alec_wam.CrystalMod.network.IMessageHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketTileMessage extends AbstractPacket {

	public BlockPos pos;
	private String type;
	private CompoundNBT data;
	
	public PacketTileMessage(){}
	
	public PacketTileMessage(BlockPos pos, String type){
		this(pos, type, new CompoundNBT());
	}
	
    public PacketTileMessage(BlockPos pos, String type, CompoundNBT data){
    	this.pos = pos;
    	this.type = type;
    	this.data = data;
    }
	
	public static PacketTileMessage decode(PacketBuffer buffer) {
		BlockPos pos = buffer.readBlockPos();
		String type = buffer.readString(100);
		CompoundNBT data = buffer.readCompoundTag();
		return new PacketTileMessage(pos, type, data);
	}

	@Override
	public void writeToBuffer(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeString(type);
		buffer.writeCompoundTag(data);
	}
	
	@Override
	public void handleClient(PlayerEntity player) {
		TileEntity tile = player.getEntityWorld().getTileEntity(pos);
		if(tile !=null){
			if(tile instanceof IMessageHandler)((IMessageHandler)tile).handleMessage(type, data, true);
		}
	}

	@Override
	public void handleServer(ServerPlayerEntity player) {
		TileEntity tile = player.getEntityWorld().getTileEntity(pos);
		if(tile !=null){
			if(tile instanceof IMessageHandler)((IMessageHandler)tile).handleMessage(type, data, false);
		}
	}

}
