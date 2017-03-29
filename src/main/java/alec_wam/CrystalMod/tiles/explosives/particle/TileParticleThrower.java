package alec_wam.CrystalMod.tiles.explosives.particle;

import com.google.common.collect.ImmutableList;

import alec_wam.CrystalMod.entities.misc.EntityCustomFallingBlock;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileParticleThrower extends TileEntity {

	public static void throwBlocks(World world, BlockPos pos, int radius){
		ImmutableList<BlockPos> list = BlockUtil.getBlocksInBB(pos, radius, 3, radius);
		for(BlockPos blockPos : list){
			if(!world.isRemote){
				IBlockState state = world.getBlockState(blockPos);
				if(world.isAirBlock(blockPos))continue;
				
				if(state.getBlockHardness(world, blockPos) < 0.0D)continue;
				
				final TileEntity tile = world.getTileEntity(blockPos);
				double explosionX = (double)blockPos.getX() + 0.5D;
				double explosionY = (double)blockPos.getY();
				double explosionZ = (double)blockPos.getZ() + 0.5D;
				
				EntityCustomFallingBlock blockEntity = new EntityCustomFallingBlock(world, explosionX, explosionY, explosionZ, state);
				NBTTagCompound tileNBT = null;
				if(tile !=null){
					tileNBT = tile.writeToNBT(new NBTTagCompound());
					blockEntity.tileEntityData = tileNBT;
					blockEntity.setTile(tile);
					world.removeTileEntity(blockPos);
				}
				
				double motionX = 0, motionY = 0, motionZ = 0;
				double power = 1D;
				if(blockPos.getX() > pos.getX()){
					motionX+=power;
				}
				if(blockPos.getX() < pos.getX()){
					motionX-=power;
				}
				motionY+=power;
				if(blockPos.getZ() > pos.getZ()){
					motionZ+=power;
				}
				if(blockPos.getZ() < pos.getZ()){
					motionZ-=power;
				}
				blockEntity.motionX +=motionX;
				blockEntity.motionY +=motionY;
				blockEntity.motionZ +=motionZ;
				if(!world.isRemote){
					world.spawnEntity(blockEntity);
					NBTTagCompound data = new NBTTagCompound();
					data.setInteger("BlockID", Block.getStateId(state));
					if(tileNBT !=null){
						data.setTag("TileData", tileNBT);
					}
					CrystalModNetwork.sendToAllAround(new PacketEntityMessage(blockEntity, "BlockSync", data), blockEntity);
				}
			}
		}
	}
	
}
