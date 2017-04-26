package alec_wam.CrystalMod.tiles.explosives.fuser;

import alec_wam.CrystalMod.client.sound.ModSounds;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketEntityMessage;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.TimeUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;

public class TileOppositeFuserTier3 extends TileOppositeFuser {

	public void triggerExplosion(){
		this.fuseTime = 5 * TimeUtil.SECOND;
		BlockUtil.markBlockForUpdate(getWorld(), getPos());
	}
	
	public void explode() {
		if(!getWorld().isRemote && getWorld() instanceof WorldServer){
			createExplosion((WorldServer)getWorld(), getPos(), 40);
			getWorld().playSound(null, getPos(), ModSounds.explosion_fusor_tier2, SoundCategory.BLOCKS, 10F, 1F);
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
	
}
