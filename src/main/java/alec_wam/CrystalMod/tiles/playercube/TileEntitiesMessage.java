package alec_wam.CrystalMod.tiles.playercube;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.network.packets.PacketTileMessage;

public class TileEntitiesMessage extends PacketTileMessage {
    private NBTTagCompound tagCompound;

    public TileEntitiesMessage() {
        super();
        tagCompound = null;
    }

    private FakeChunk chunk;
    
    public TileEntitiesMessage(FakeChunk chunk) {
    	super(chunk.portal.getPos(), "NULL");
		this.chunk = chunk;
        tagCompound = null;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        tagCompound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (TileEntity te : chunk.chunkTileEntityMap.values()) {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            list.appendTag(nbt);
        }
        tagCompound.setTag("list", list);
        DataOutputStream out = new DataOutputStream(new ByteBufOutputStream(buf));
        // oh this is why there was an ioexception.... whatever
        try {
            CompressedStreamTools.write(tagCompound, out);
            out.flush();
        } catch (IOException e) {
            try {
                throw e;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    	super.fromBytes(buf);
    	World world = CrystalMod.proxy.getClientWorld();//DimensionManager.getWorld(dim);
		if(world !=null){
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if (tile != null && tile instanceof TileEntityPlayerCubePortal) {
				chunk = ((TileEntityPlayerCubePortal)tile).mobileChunk;
				if (chunk != null) {
		            DataInputStream in = new DataInputStream(new ByteBufInputStream(buf));
		            try {
		                tagCompound = CompressedStreamTools.read(in);
		            } catch (IOException e) {
		                try {
		                    throw e;
		                } catch (IOException e1) {
		                    e1.printStackTrace();
		                }
		            } finally {
		                try {
		                    in.close();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }
			}
		}
    }

    @Override
    public void handleClientSafe(NetHandlerPlayClient netHandler) {
        if (chunk != null && tagCompound != null && chunk instanceof FakeChunkClient) {
            NBTTagList list = tagCompound.getTagList("list", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound nbt = list.getCompoundTagAt(i);
                if (nbt == null) continue;
                int x = nbt.getInteger("x");
                int y = nbt.getInteger("y");
                int z = nbt.getInteger("z");
                BlockPos pos = new BlockPos(x, y, z);
                try {
                    TileEntity te = chunk.getTileEntity(pos);
                    if (te != null)
                        te.readFromNBT(nbt);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ((FakeChunkClient) chunk).getRenderer().markDirty();
        }
    }
}
