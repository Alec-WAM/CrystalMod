package alec_wam.CrystalMod.tiles.playercube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class FakeChunkClient extends FakeChunk {
    private ChunkRenderer renderer;

    public FakeChunkClient(World world, TileEntityPlayerCubePortal movingWorld) {
        super(world, movingWorld);
        renderer = new ChunkRenderer(this);
    }

    public ChunkRenderer getRenderer() {
        return renderer;
    }

    @Override
    public void onChunkUnload() {
        List<TileEntity> iterator = new ArrayList<TileEntity>(chunkTileEntityMap.values());
        for (TileEntity te : iterator) {
            removeChunkBlockTileEntity(te.getPos());
        }
        super.onChunkUnload();
        renderer.markRemoved();
    }

    @Override
    public void setChunkModified() {
        super.setChunkModified();
        renderer.markDirty();
    }
}
