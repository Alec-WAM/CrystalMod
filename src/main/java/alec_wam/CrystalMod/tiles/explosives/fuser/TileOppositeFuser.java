package alec_wam.CrystalMod.tiles.explosives.fuser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class TileOppositeFuser extends TileEntityMod {

	public boolean facingNS;
	public boolean hasPure;
	public boolean hasDark;
	public int fuseTime;
	
	@Override
	public void writeCustomNBT(NBTTagCompound nbt){
		super.writeCustomNBT(nbt);
		nbt.setBoolean("NorthSouth", facingNS);
		nbt.setBoolean("hasPure", hasPure);
		nbt.setBoolean("hasDark", hasDark);
		nbt.setInteger("Fuse", fuseTime);
	}
	
	@Override
	public void readCustomNBT(NBTTagCompound nbt){
		super.readCustomNBT(nbt);
		facingNS = nbt.getBoolean("NorthSouth");
		hasPure = nbt.getBoolean("hasPure");
		hasDark = nbt.getBoolean("hasDark");
		fuseTime = nbt.getInteger("Fuse");
	}
	
	
	public void triggerExplosion(){
		this.fuseTime = 10 * TimeUtil.SECOND;
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	@Override
	public void update(){
		super.update();
		
		if(hasPure && hasDark){
			if(fuseTime > 0){
				fuseTime--;
				if(fuseTime <= 0){
					//if(!getWorld().isRemote){
						explode();
					//}
				}
			}
		}
	}

	public void explode() {
		if(!getWorld().isRemote && getWorld() instanceof WorldServer){
			createExplosion((WorldServer)getWorld(), getPos(), 20);
			getWorld().playSound(null, getPos(), ModSounds.explosion_fusor_tier0, SoundCategory.BLOCKS, 4F, 1F);
			Chunk chunk = getWorld().getChunkFromBlockCoords(pos);
			for (EntityPlayer player : world.playerEntities) {
				// only send to relevant players
				if (!(player instanceof EntityPlayerMP)) {
					continue;
				}
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				if (((WorldServer) getWorld()).getPlayerChunkMap().isPlayerWatchingChunk(playerMP, chunk.xPosition, chunk.zPosition)) {
					CrystalModNetwork.sendTo(new PacketEntityMessage(player, "#FusorFlash#"), playerMP);
				}
			}
		}
	}
	
	public void createExplosion(WorldServer world, BlockPos pos, int size){
		Set<BlockPos> set = Sets.<BlockPos>newHashSet();
        //BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos();

    	for (int j = pos.getX()-size; j <= pos.getX()+size; ++j)
        {
            for (int k = pos.getY()-size; k <= pos.getY()+size; ++k)
            {
                for (int l = pos.getZ()-size; l <= pos.getZ()+size; ++l)
                {
                	BlockPos blockpos = new BlockPos(j, k, l);
                	if(!blockpos.equals(pos)){
                		
                		double dx = pos.getX() - blockpos.getX();
                        double dy = pos.getY() - blockpos.getY();
                        double dz = pos.getZ() - blockpos.getZ();
                		double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
                		
                		float f = (float) ((size-dis) * (0.7F + this.world.rand.nextFloat() * 0.6F));
                		//f = Math.abs(f);
                		IBlockState iblockstate = world.getBlockState(blockpos);
	
	                    if (iblockstate.getMaterial() != Material.AIR)
	                    {
	                        float f2 = iblockstate.getBlock().getExplosionResistance((Entity)null);
	                        f -= (f2 + 0.3F) * 0.3F;
	                    }
	
	                    if(f > 0.0F)
	                    {
	                    	//ModLogger.info(""+blockpos);
	                        //world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 4);
	                        set.add(blockpos);
	                    }
                	}
                }
            }
        }
        LinkedList<BlockPos> list = new LinkedList<BlockPos>(set);
        ModLogger.info("Blowing up "+list.size());
        ExplosionMaker helper = new ExplosionMaker(world);
        helper.setBlocksForRemoval(list);
        helper.addBlocksForUpdate(set);
        
        long time = System.currentTimeMillis();
        helper.finish();
        ModLogger.info("Explosion took "+(System.currentTimeMillis() - time)+"ms");
        
        /*for(BlockPos blPos : set){
        	world.setBlockToAir(blPos);
        }*/
        /*Explosion explosion = new Explosion(world, null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, size, false, true, list);
        if (net.minecraftforge.event.ForgeEventFactory.onExplosionStart(world, explosion)) return;
        explosion.doExplosionA();
        explosion.doExplosionB(true);*/
        //this.affectedBlockPositions.addAll(set);
	}
	
}
