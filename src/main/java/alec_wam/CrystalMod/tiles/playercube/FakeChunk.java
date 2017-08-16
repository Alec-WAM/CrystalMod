package alec_wam.CrystalMod.tiles.playercube;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.blocks.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FakeChunk implements IBlockAccess {

	public World worldObj;
    protected final TileEntityPlayerCubePortal portal;
    public Map<BlockPos, TileEntity> chunkTileEntityMap;
    
    private int blockCount;
    
    public Map<BlockPos, IBlockState> cubeBlocks = Maps.newHashMap();
    
    public boolean isChunkLoaded;
    public boolean isModified;
	
	public FakeChunk(World world, TileEntityPlayerCubePortal portal){
		this.worldObj = world;	
		this.portal = portal;
		chunkTileEntityMap = new HashMap<BlockPos, TileEntity>();
		
		blockCount = 0;
		
		isChunkLoaded = false;
        isModified = false;
	}
	
	public void setWorld(World world){
		this.worldObj = world;
	}
	
	public PlayerCube getCube(){
		return portal == null ? null : portal.getCube();
	}
	
	public void clear(){
		this.onChunkLoad();
		/*for(int y = 1; y < 15; y ++){
			for(int x = 1; x < 15; x ++){
				for(int z = 1; z < 15; z ++){
					BlockPos fix = getCube().minBlock.add(x, y, z);*/
					for(BlockPos pos : this.cubeBlocks.keySet()){
						setBlockState(pos, Blocks.AIR.getDefaultState());
						removeChunkBlockTileEntity(pos);
					}
					cubeBlocks.clear();
					this.blockCount = 0;
				/*}
			}
		}*/
	}
	
	public boolean addBlockWithState(BlockPos pos, IBlockState state) {
        if (state == null) return false;

        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);

        if (block == null || block == ModBlocks.cubeBlock) return false;
        
        IBlockState currentState = getBlockState(pos);
        Block currentBlock = currentState.getBlock();
        int currentMeta = currentBlock.getMetaFromState(currentState);
        if (currentBlock == block && currentMeta == meta) {
            return false;
        }

        setBlockArray(pos, state);
        blockCount++;
        setChunkModified();

        TileEntity tileentity;
        if (block.hasTileEntity(state)) {
            tileentity = getTileEntity(pos);

            if (tileentity == null) {
                setTileEntity(pos, tileentity);
            }

            if (tileentity != null) {
                //tileentity.updateContainingBlockInfo();
                /*tileentity.blockType = block;
                tileentity.blockMetadata = meta;*/
            }
        }


        return true;
    }
	
	public boolean setBlockState(BlockPos pos, IBlockState state) {
        IBlockState checkState = getBlockState(pos);
        if (checkState.getBlock().equals(state.getBlock()) && checkState.getBlock().getMetaFromState(checkState) == state.getBlock().getMetaFromState(state) || state.getBlock() == ModBlocks.cubePortal) {
            return false;
        }

        setChunkModified();
        setBlockArray(pos, state);
        state = getBlockState(pos);
        Block block = state.getBlock();

        if (block != null && block.hasTileEntity(state)) {
            TileEntity tileentity = getTileEntity(pos);

            if (tileentity != null) {
                //tileentity.updateContainingBlockInfo();
                //tileentity.blockMetadata = block.getMetaFromState(state);
            }
        }

        return true;
    }

	public void onChunkLoad() {
        isChunkLoaded = true;
        //worldObj.addTileEntities(chunkTileEntityMap.values());
    }
	
	public void onChunkUnload() {
        isChunkLoaded = false;
    }

    public void setChunkModified() {
        isModified = true;
    }
	
	@Override
	public TileEntity getTileEntity(BlockPos pos) {
		BlockPos chunkPosition = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
		TileEntity tileentity = chunkTileEntityMap.get(chunkPosition);

        if (tileentity == null) {
            IBlockState blockState = getBlockState(pos);
            Block block = blockState.getBlock();

            if (block == null || !block.hasTileEntity(blockState) || block == ModBlocks.cubePortal || worldObj == null) {
                return null;
            }

            tileentity = block.createTileEntity(worldObj, blockState);
            setTileEntity(pos, tileentity);

            tileentity = chunkTileEntityMap.get(chunkPosition);
        }

        return tileentity;
	}
	
	public void setTileEntity(BlockPos pos, TileEntity tileentity) {
        if (tileentity == null || tileentity instanceof TileEntityPlayerCubePortal) {
            return;
        }

        setChunkBlockTileEntity(pos, tileentity);
    }

    /**
     * Sets the TileEntity for a given block in this chunk
     */
    private void setChunkBlockTileEntity(BlockPos pos, TileEntity tileentity) {
    	if(worldObj == null)return;
    	BlockPos chunkPosition = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        tileentity.setWorld(worldObj);
        tileentity.setPos(pos);

        IBlockState blockState = getBlockState(pos);
        Block block = blockState.getBlock();
        if (block != null && block.hasTileEntity(blockState)) {
            chunkTileEntityMap.put(chunkPosition, tileentity);
        }
    }

    /**
     * Adds a TileEntity to a chunk
     */
    public void addTileEntity(TileEntity tileentity) {
        setChunkBlockTileEntity(tileentity.getPos(), tileentity);
    }

    /**
     * Removes the TileEntity for a given block in this chunk
     */
    public void removeChunkBlockTileEntity(BlockPos pos) {
    	BlockPos chunkPosition = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        TileEntity tileentity = chunkTileEntityMap.remove(chunkPosition);
        if (tileentity != null) {
            //tileentity.invalidate();
        }
    }

	@Override
    @SideOnly(Side.CLIENT)
    public int getCombinedLight(BlockPos pos, int l) {
        int lv = EnumSkyBlock.SKY.defaultLightValue;
        return lv << 20 | l << 4;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean isAirBlock(BlockPos pos) {
    	IBlockState state = getBlockState(pos);
        Block block = state.getBlock();
        return block == null || (worldObj == null ? block.isAir(state, this, pos) : block.isAir(state, worldObj, pos)) || block.getMaterial(state) == Material.AIR;
    }

	@Override
	public IBlockState getBlockState(BlockPos pos) {
		IBlockState state = getBlockStateFromArray(pos);
        @SuppressWarnings("deprecation")
		IBlockState state2 = worldObj == null ? state : state.getBlock().getActualState(state, worldObj, pos);
        return state2;
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction) {
		return 0;
	}

	@Override
	public WorldType getWorldType() {
		return worldObj == null ? WorldType.DEFAULT : worldObj.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
		 int x = pos.getX();
		 int z = pos.getZ();
		 if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
			 return _default;
		 }
		 IBlockState state = getBlockState(pos);
		 Block block = state.getBlock();
		 return block.isSideSolid(state, this, pos, side);
	}
	
	//PORTAL FUNTIONS
	public void setBlockArray(BlockPos pos, IBlockState state){
		/*if(getCube() == null)return;
		int fixX = pos.getX()-getCube().minBlock.getX();
		int fixZ = pos.getZ()-getCube().minBlock.getZ();
		if(fixX >= 0 && fixX < 16){
			if(pos.getY() >= 0 && pos.getY() < 16){
				if(fixZ >= 0 && fixZ < 16){
					if(state == null)state = Blocks.air.getDefaultState();
					cubeBlocks[fixX][pos.getY()][fixZ] = state;
				}
			}
		}*/
		if(getCube() == null){
			if(state == null)state = Blocks.AIR.getDefaultState();
			this.cubeBlocks.put(pos, state);
			return;
		}
		int fixX = pos.getX()-getCube().minBlock.getX();
		int fixZ = pos.getZ()-getCube().minBlock.getZ();
		if(fixX >= 0 && fixX < 16){
			if(pos.getY() >= 0 && pos.getY() < 16){
				if(fixZ >= 0 && fixZ < 16){
					if(state == null)state = Blocks.AIR.getDefaultState();
					this.cubeBlocks.put(pos, state);
				}
			}
		}
	}
	


	public IBlockState getBlockStateFromArray(BlockPos pos) {
		/*if(getCube() == null)return Blocks.air.getDefaultState();
		int fixX = pos.getX()-getCube().minBlock.getX();
		int fixZ = pos.getZ()-getCube().minBlock.getZ();
		if(fixX >= 0 && fixX < 16){
			if(pos.getY() >= 0 && pos.getY() < 16){
				if(fixZ >= 0 && fixZ < 16){
					IBlockState state = cubeBlocks[fixX][pos.getY()][fixZ];
					if(state == null)return Blocks.air.getDefaultState();
					return state;
				}
			}
		}
		return Blocks.air.getDefaultState();*/
		IBlockState state = cubeBlocks.get(pos);
		if(state == null)return Blocks.AIR.getDefaultState();
		return state;
	}
	
	public Block getBlock(BlockPos pos){
		IBlockState state = getBlockStateFromArray(pos);
		return state.getBlock();
	}
	
	public int getBlockMetadata(BlockPos pos){
		IBlockState state = getBlockStateFromArray(pos);
		return state.getBlock().getMetaFromState(state);
	}

	public FakeWorld getFakeWorld() {
		return FakeWorld.getFakeWorld(this);
	}

	public final int getMemoryUsage() {
        return 2 + blockCount * 9; // (3 bytes + 2 bytes (short) + 4 bytes (int) = 9 bytes per block) + 2 bytes (short)
    }

	@Override
	public Biome getBiome(BlockPos pos) {
		return Biomes.SKY;
	}

}
