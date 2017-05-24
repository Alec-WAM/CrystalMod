package alec_wam.CrystalMod.tiles.tooltable;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.enhancements.EnhancementManager;
import alec_wam.CrystalMod.api.enhancements.IEnhancement;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketEnhancementTable extends AbstractPacketThreadsafe {

	public int x;
	public int y;
	public int z;
	public int type;
	public String id;
	
	public PacketEnhancementTable(){}
	
    public PacketEnhancementTable(BlockPos pos, IEnhancement enhancement, int type){
    	x = pos.getX();
    	y = pos.getY();
    	z = pos.getZ();
    	this.type = type;
    	this.id = enhancement.getID().toString();
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		type = buffer.readInt();
		id = buffer.readString(100);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeInt(type);
		buffer.writeString(id);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		World world = CrystalMod.proxy.getClientPlayer() == null ? null : CrystalMod.proxy.getClientPlayer().getEntityWorld();
		if(world == null){
			return;
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayer player = netHandler.playerEntity;
		World world = netHandler.playerEntity.getEntityWorld();
		if(world == null){
			return;
		}
		IEnhancement enhancement = EnhancementManager.getEnhancement(new ResourceLocation(id));
		
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile !=null){
			if(tile instanceof TileEnhancementTable){
				TileEnhancementTable table = (TileEnhancementTable)tile;
				if(type == 0){
					table.applyEnhancement(player, enhancement);
				} else if(type == 1){
					table.removeEnhancement(player, enhancement);
				}
			}
		}
	}

}
