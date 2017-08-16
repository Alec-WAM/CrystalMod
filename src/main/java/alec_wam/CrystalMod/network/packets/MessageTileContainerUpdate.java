package alec_wam.CrystalMod.network.packets;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.AbstractPacketThreadsafe;
import alec_wam.CrystalMod.tiles.ISynchronizedContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class MessageTileContainerUpdate extends AbstractPacketThreadsafe {
    private TileEntity tile;
    private int x;
    private int y;
    private int z;

    public MessageTileContainerUpdate() {
    }

    public MessageTileContainerUpdate(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        if (CrystalMod.proxy.getClientWorld() != null) {
            tile = CrystalMod.proxy.getClientWorld().getTileEntity(new BlockPos(x, y, z));

            if (tile instanceof ISynchronizedContainer) {
                ((ISynchronizedContainer) tile).readContainerData(buf);
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(tile.getPos().getX());
        buf.writeInt(tile.getPos().getY());
        buf.writeInt(tile.getPos().getZ());

        if (tile instanceof ISynchronizedContainer) {
            ((ISynchronizedContainer) tile).writeContainerData(buf);
        }
    }

	@Override
	public void handleClientSafe(NetHandlerPlayClient netHandler) {}

	@Override
	public void handleServerSafe(NetHandlerPlayServer netHandler) {}
}
