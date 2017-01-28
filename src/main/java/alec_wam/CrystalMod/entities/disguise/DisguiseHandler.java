package alec_wam.CrystalMod.entities.disguise;

import java.util.UUID;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.entities.disguise.render.RenderMiniPlayer;
import alec_wam.CrystalMod.entities.disguise.render.RenderPlayerHand;
import alec_wam.CrystalMod.util.EntityUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.client.DownloadedTextures;
import alec_wam.CrystalMod.util.client.DownloadedTextures.PlayerSkin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DisguiseHandler {

	public static void updateSize(EntityPlayer player, DisguiseType type)
	{
		if (!player.getEntityWorld().isRemote)
		{
			float width = 0.6F;
			float height = 1.8F;
			float stepSize = 0.5F;
			float eyeHeight = player.getDefaultEyeHeight();

			if(type == DisguiseType.MINI){
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
			DisguiseType type = playerEx.getCurrentDiguise();
			float width = 0.6F;
			float height = 1.8F;
			float stepSize = 0.5F;
			float eyeHeight = player.getDefaultEyeHeight();
			boolean doFunc = false;
			if(type == DisguiseType.MINI){
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
		if(ePlayer !=null && ePlayer.getCurrentDiguise() !=DisguiseType.NONE)
        {
			if(ePlayer.getPlayerDisguiseUUID() !=null)event.setDisplayname(ProfileUtil.getUsername(ePlayer.getPlayerDisguiseUUID()));
        }
	}
	
}
