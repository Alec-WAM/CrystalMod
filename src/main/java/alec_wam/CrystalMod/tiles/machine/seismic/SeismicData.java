package alec_wam.CrystalMod.tiles.machine.seismic;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.Constants.NBT;

public class SeismicData{
	public static enum SeismicShape {
		SQUARE, CIRCLE;
	}
	
	public BlockPos sourcePos;
	public int radius;
	public SeismicShape shape;
	private final Map<BlockPos, IBlockState>[] layers;
	
	public SeismicData(BlockPos source, SeismicShape shape, int radius, Map<BlockPos, IBlockState>[] layers){
		this.sourcePos = source;
		this.shape = shape;
		this.radius = radius;
		this.layers = layers;
	}
	
	/**
	 * @return the layers
	 */
	public Map<BlockPos, IBlockState>[] getLayers() {
		return layers;
	}
	
	public IBlockState getStateForPos(BlockPos pos){
		for(int l = 0; l < layers.length; l++){
			Map<BlockPos, IBlockState> layer = layers[l];
			if(layer.containsKey(pos)){
				return layer.get(pos);
			}
		}
		return Blocks.AIR.getDefaultState();
	}

	public static SeismicData collectData(IBlockAccess world, BlockPos source, int depth, int radius, SeismicShape shape){
		@SuppressWarnings("unchecked")
		Map<BlockPos, IBlockState>[] layers = new Map[depth];
		for(int d = 0; d < depth; d++){
			BlockPos center = source.down(d);
			List<BlockPos> blocks = shape == SeismicShape.SQUARE ? BlockUtil.getBlocksInBB(center, radius*2, 1, radius*2) : BlockUtil.createCircleList(center, radius*2);
			Map<BlockPos, IBlockState> layerMap = Maps.newHashMap();
			for(BlockPos pos : blocks){
				layerMap.put(pos, world.getBlockState(pos));
			}
			layers[d] = layerMap;
		}
		return new SeismicData(source, shape, radius, layers);
	}
	
	public NBTTagCompound saveToNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("SourcePos", NBTUtil.createPosTag(sourcePos));
		nbt.setByte("Shape", (byte)shape.ordinal());
		nbt.setInteger("Radius", radius);
		NBTTagList layerList = new NBTTagList();
		for(int l = 0; l < layers.length; l++){
			NBTTagCompound nbtHolder = new NBTTagCompound();
			NBTTagList layerNBTList = new NBTTagList();
			Map<BlockPos, IBlockState> currentLayer = layers[l];
			for(Entry<BlockPos, IBlockState> entry : currentLayer.entrySet()){
				NBTTagCompound blockData = new NBTTagCompound();
				blockData.setTag("Pos", NBTUtil.createPosTag(entry.getKey()));
				blockData.setInteger("State", Block.getStateId(entry.getValue()));
				layerNBTList.appendTag(blockData);
			}
			nbtHolder.setTag("Data", layerNBTList);
			layerList.appendTag(nbtHolder);
		}
		nbt.setTag("LayerData", layerList);
		return nbt;
	}
	
	public static SeismicData loadFromNBT(NBTTagCompound nbt){
		BlockPos sourcePos = NBTUtil.getPosFromTag(nbt.getCompoundTag("SourcePos"));
		SeismicShape shape = nbt.getByte("Shape") == 0 ? SeismicShape.SQUARE : SeismicShape.CIRCLE;
		int radius = nbt.getInteger("Radius");
		NBTTagList layerList = nbt.getTagList("LayerData", NBT.TAG_COMPOUND);
		int depth = layerList.tagCount();
		@SuppressWarnings("unchecked")
		Map<BlockPos, IBlockState>[] layers = new Map[depth];
		ModLogger.info("Depth: "+depth);
		for(int l = 0; l < depth; l++){
			Map<BlockPos, IBlockState> layer = Maps.newHashMap();
			NBTTagList layerNBTList = layerList.getCompoundTagAt(l).getTagList("Data", NBT.TAG_COMPOUND);
			for(int i = 0; i < layerNBTList.tagCount(); i++){
				NBTTagCompound blockData = layerNBTList.getCompoundTagAt(i);
				BlockPos dataPos = NBTUtil.getPosFromTag(blockData.getCompoundTag("Pos"));
				IBlockState dataState = Block.getStateById(blockData.getInteger("State"));
				layer.put(dataPos, dataState);
			}
			layers[l] = layer;
		}
		return new SeismicData(sourcePos, shape, radius, layers);
	}
}
