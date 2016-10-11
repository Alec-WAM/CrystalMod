package alec_wam.CrystalMod.network.packets;

import java.io.IOException;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.items.backpack.container.ContainerBackpackBase;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import io.netty.buffer.ByteBuf;

public class MessageSyncSlot extends AbstractPacketThreadsafe
{
    private int windowId;
    private int slotNum;
    private ItemStack stack;

    public MessageSyncSlot()
    {
    }

    public MessageSyncSlot(int windowId, int slotNum, ItemStack stack)
    {
        this.windowId = windowId;
        this.slotNum = slotNum;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        try
        {
            this.windowId = buf.readByte();
            this.slotNum = buf.readShort();
            this.stack = ByteBufUtils.readItemStackFromBuffer(buf);
        }
        catch (IOException e)
        {
            ModLogger.warning("MessageSyncSlot: Exception while reading data from buffer");
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(this.windowId);
        buf.writeShort(this.slotNum);
        ByteBufUtils.writeItemStackToBuffer(buf, this.stack);
    }

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if(player !=null){
			if (player.openContainer instanceof ContainerBackpackBase && windowId == player.openContainer.windowId)
            {
                player.openContainer.putStackInSlot(slotNum, stack);
            }
		}
	}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {
		
	}
}