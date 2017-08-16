package alec_wam.CrystalMod.tiles.playercube;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public abstract class ChunkIO {
    public static void write(DataOutput out, FakeChunk chunk, Collection<BlockPos> blocks) throws IOException {
        out.writeShort(blocks.size());
        for (BlockPos p : blocks) {
            writeBlock(out, chunk, p);
        }
    }

    public static int writeAll(DataOutput out, FakeChunk chunk) throws IOException {
        int count = 0;
        for (int i = 1; i < 15; i++) {
            for (int j = 1; j < 15; j++) {
                for (int k = 1; k < 15; k++) {
                    Block block = chunk.getBlockState(new BlockPos(i, j, k)).getBlock();
                    if (block != Blocks.AIR) {
                        count++;
                    }
                }
            }
        }
        //MovingWorld.logger.debug("Writing mobile chunk data: " + count + " blocks");

        out.writeShort(count);
        for (int i = 1; i < 15; i++) {
            for (int j = 1; j < 15; j++) {
                for (int k = 1; k < 15; k++) {
                    Block block = chunk.getBlockState(new BlockPos(i, j, k)).getBlock();
                    if (block != Blocks.AIR) {
                        writeBlock(out, chunk.getBlockState(new BlockPos(i, j, k)), new BlockPos(i, j, k));
                    }
                }
            }
        }

        return count;

    }

    public static void writeBlock(DataOutput out, FakeChunk chunk, BlockPos pos) throws IOException {
        writeBlock(out, chunk.getBlockState(pos), pos);
    }

    public static void writeBlock(DataOutput out, IBlockState state, BlockPos pos) throws IOException {
        out.writeByte(pos.getX());
        out.writeByte(pos.getY());
        out.writeByte(pos.getZ());
        out.writeShort(Block.getIdFromBlock(state.getBlock()));
        out.writeInt(state.getBlock().getMetaFromState(state));
    }

    @SuppressWarnings("deprecation")
	public static void read(DataInput in, FakeChunk chunk) throws IOException {
        int count = in.readShort();

        //MovingWorld.logger.debug("Reading mobile chunk data: " + count + " blocks");
        chunk.chunkTileEntityMap.clear();
        int x, y, z;
        int id;
        IBlockState state;
        for (int i = 0; i < count; i++) {
            x = in.readByte();
            y = in.readByte();
            z = in.readByte();
            id = in.readShort();
            state = Block.getBlockById(id).getStateFromMeta(in.readInt());
            chunk.addBlockWithState(new BlockPos(x, y, z), state);
        }
    }

    public static void writeCompressed(ByteBuf buf, FakeChunk chunk, Collection<BlockPos> blocks) throws IOException {
        DataOutputStream out = preCompress(buf);
        write(out, chunk, blocks);
        postCompress(buf, out, blocks.size());
    }

    public static void writeAllCompressed(ByteBuf buf, FakeChunk chunk) throws IOException {
        DataOutputStream out = preCompress(buf);
        int count = writeAll(out, chunk);
        postCompress(buf, out, count);
    }

    private static DataOutputStream preCompress(ByteBuf data) throws IOException {
        ByteBufOutputStream bbos = new ByteBufOutputStream(data);
        DataOutputStream out = new DataOutputStream(new GZIPOutputStream(bbos));
        return out;
    }

    private static void postCompress(ByteBuf data, DataOutputStream out, int count) throws IOException {
        out.flush();
        out.close();

        int byteswritten = data.writerIndex();
        //float f = (float) byteswritten / (count * 9);
        //MovingWorld.logger.debug(String.format(Locale.ENGLISH, "%d blocks written. Efficiency: %d/%d = %.2f", count, byteswritten, count * 9, f));

        if (byteswritten > 32000) {
             //MovingWorld.logger.warn("Ship probably contains too many blocks");
        }
    }

    public static void readCompressed(ByteBuf data, FakeChunk chunk) throws IOException {
        DataInputStream in = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(data)));
        read(in, chunk);
        in.close();
    }
}
