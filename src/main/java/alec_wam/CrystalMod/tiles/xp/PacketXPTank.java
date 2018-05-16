package alec_wam.CrystalMod.tiles.xp;

import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PacketXPTank extends AbstractPacketThreadsafe {

	public BlockPos blockPos;
	public int amount;
	public boolean add;
	
	public PacketXPTank(){}
	
	public PacketXPTank(BlockPos pos, int amount, boolean add){
    	this.blockPos = pos;
    	this.amount = amount;
    	this.add = add;
    }
	
	@Override
	public void fromBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		blockPos = buffer.readBlockPos();
		amount = buffer.readInt();
		add = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		PacketBuffer buffer = new PacketBuffer(buf);
		buffer.writeBlockPos(blockPos);
		buffer.writeInt(amount);
		buffer.writeBoolean(add);
	}

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		EntityPlayerMP player = netHandler.playerEntity;
		World world = player.getEntityWorld();
		if(world == null){
			return;
		}
		TileEntity tile = world.getTileEntity(blockPos);
		if(tile !=null && tile instanceof TileEntityXPTank){
			TileEntityXPTank tank = ((TileEntityXPTank)tile);
			tank.changeXP(player, amount, add);
		}
	}

}
