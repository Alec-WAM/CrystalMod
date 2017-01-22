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

	public static DisguiseMini miniPlayer = new DisguiseMini();
	
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
    @SideOnly(Side.CLIENT)
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
		if(event.getRenderer() !=null && event.getRenderer() instanceof RenderMiniPlayer){
			return;
		}
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(event.getEntityPlayer());
		if(ePlayer !=null && ePlayer.getCurrentDiguise() !=DisguiseType.NONE)
        {
            event.setCanceled(true);
            float f1 = event.getEntityPlayer().prevRotationYaw + (event.getEntityPlayer().rotationYaw - event.getEntityPlayer().prevRotationYaw) * event.getPartialRenderTick();
            if(Minecraft.getMinecraft().getRenderManager().renderEngine != null && Minecraft.getMinecraft().getRenderManager().renderViewEntity != null && Minecraft.getMinecraft().getRenderManager() !=null)
            {
            	if(event.getEntityPlayer() instanceof AbstractClientPlayer){
            		if(miniPlayer.renderNormal.getRenderManager() == null){
            			miniPlayer.renderNormal = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), false);
            		}
            		if(miniPlayer.renderSlim.getRenderManager() == null){
            			miniPlayer.renderSlim = new RenderMiniPlayer(Minecraft.getMinecraft().getRenderManager(), true);
            		}

            		UUID renderUUID = event.getEntityPlayer().getUniqueID();
            		PlayerSkin skin = DownloadedTextures.getPlayerSkin(renderUUID);
            		String type = skin.getSkinType();
            		if(type == "slim")miniPlayer.renderSlim.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(), event.getY(), event.getZ(), f1, event.getPartialRenderTick());
            		else miniPlayer.renderNormal.doRender((AbstractClientPlayer) event.getEntityPlayer(), event.getX(), event.getY(), event.getZ(), f1, event.getPartialRenderTick());
            	}
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
	
	private final static RenderPlayerHand renderHandOverride = new RenderPlayerHand();
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHand(RenderSpecificHandEvent event)
    {
		Minecraft mc = Minecraft.getMinecraft();
		AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) CrystalMod.proxy.getClientPlayer();
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(abstractclientplayer);
		if(ePlayer !=null && ePlayer.getCurrentDiguise() !=DisguiseType.NONE)
        {
			boolean flag = event.getHand() == EnumHand.MAIN_HAND;
	        EnumHandSide enumhandside = flag ? abstractclientplayer.getPrimaryHand() : abstractclientplayer.getPrimaryHand().opposite();
	       

	        if (ItemStackTools.isNullStack(event.getItemStack()))
	        {
	            if (flag && !abstractclientplayer.isInvisible())
	            {
	            	event.setCanceled(true);
	            	GlStateManager.pushMatrix();
	                renderArmFirstPerson(event.getEquipProgress(), event.getSwingProgress(), enumhandside);
	                GlStateManager.popMatrix();
	            }
	        }
        }
    }
	
	public static void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_)
    {
        boolean flag = p_187456_3_ != EnumHandSide.LEFT;
        float f = flag ? 1.0F : -1.0F;
        float f1 = MathHelper.sqrt(p_187456_2_);
        float f2 = -0.3F * MathHelper.sin(f1 * (float)Math.PI);
        float f3 = 0.4F * MathHelper.sin(f1 * ((float)Math.PI * 2F));
        float f4 = -0.4F * MathHelper.sin(p_187456_2_ * (float)Math.PI);
        GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
        GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
        float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * (float)Math.PI);
        float f6 = MathHelper.sin(f1 * (float)Math.PI);
        GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
        AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) CrystalMod.proxy.getClientPlayer();
        Minecraft.getMinecraft().getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
        GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
        GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
        RenderPlayer renderplayer = renderHandOverride;
        GlStateManager.disableCull();

        if (flag)
        {
            renderplayer.renderRightArm(abstractclientplayer);
        }
        else
        {
            renderplayer.renderLeftArm(abstractclientplayer);
        }

        GlStateManager.enableCull();
    }

	
}
