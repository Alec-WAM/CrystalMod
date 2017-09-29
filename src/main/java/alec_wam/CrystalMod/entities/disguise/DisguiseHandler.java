package alec_wam.CrystalMod.entities.disguise;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ProfileUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class DisguiseHandler {

	public static void updateSize(EntityPlayer player, boolean mini)
	{
		if (!player.getEntityWorld().isRemote)
		{
			float width = 0.6F;
			float height = 1.8F;
			float stepSize = 0.5F;
			float eyeHeight = player.getDefaultEyeHeight();

			if(mini){
				width /= 2F;
				height /= 3F;
				eyeHeight/=3;
				stepSize/=2;
			}

			EntityUtil.setSize(player, width, height, stepSize, eyeHeight, true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event){
		if(event.phase == Phase.END){
			EntityPlayer player = event.player;
			
			ExtendedPlayer playerEx = ExtendedPlayerProvider.getExtendedPlayer(player);
			float width = 0.6F;
			float height = 1.8F;
			float stepSize = 0.5F;
			float eyeHeight = player.getDefaultEyeHeight();
			boolean doFunc = false;
			if(playerEx.isMini()){
				width /= 2F;
				height /= 3F;
				eyeHeight/=3;
				stepSize/=2;
				doFunc = true;
			}
			if(doFunc && (player.width !=width || player.height !=height)){
				EntityUtil.setSize(player, width, height, stepSize, eyeHeight, false);
			}
		}
	}
	
	@SubscribeEvent
	public void overrideDisplayName(PlayerEvent.NameFormat event){
		EntityPlayer player = event.getEntityPlayer();
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		if(ePlayer !=null && ePlayer.getPlayerDisguiseUUID() !=null)
        {
			event.setDisplayname(ProfileUtil.getUsername(ePlayer.getPlayerDisguiseUUID()));
        }
	}
	
}
