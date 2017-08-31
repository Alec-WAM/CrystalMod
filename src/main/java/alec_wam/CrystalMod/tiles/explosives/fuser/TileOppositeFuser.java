package alec_wam.CrystalMod.tiles.explosives.fuser;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.tiles.TileEntityMod;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

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
					hasPure = hasDark = false;
					explode();

					getWorld().setBlockToAir(getPos());
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
	
	@SuppressWarnings("deprecation")
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

                		IBlockState iblockstate = world.getBlockState(blockpos);
                		float resistance = (float) (size - dis);
                		
                		resistance-=iblockstate.getBlock().getExplosionResistance(null);
	
	                    if(resistance > 0.0F)
	                    {
	                    	if(!world.isAirBlock(blockpos))set.add(blockpos);
	                    }
                	}
                }
            }
        }
        LinkedList<BlockPos> list = new LinkedList<BlockPos>(set);
        ExplosionMaker helper = new ExplosionMaker(world);
        helper.setBlocksForRemoval(list);
        helper.addBlocksForUpdate(set);
        
        helper.finish();
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(getPos(), getPos().add(1, 1, 1)).expandXyz(size));
        for(Entity entity : entities){
        	if(!entity.isImmuneToExplosions()){
        		double dx = pos.getX() - entity.posX;
                double dy = pos.getY() - entity.posY;
                double dz = pos.getZ() - entity.posZ;
                double dis = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double power = ((size - dis) + 1.0F) / size;
                entity.attackEntityFrom((new DamageSource("explosion")).setDifficultyScaled().setExplosion(), (float)(20.0F * power));
        	}
        }
	}
	
}
