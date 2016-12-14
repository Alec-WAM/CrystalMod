package alec_wam.CrystalMod.entities.disguise.render;

import java.util.List;

import com.google.common.collect.Lists;

import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.entities.disguise.DisguiseType;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.UUIDUtils;
import alec_wam.CrystalMod.util.client.DownloadedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderMiniPlayer extends RenderPlayer
{
	
	public final List<LayerRenderer<AbstractClientPlayer>> grabbedLayers = Lists.newArrayList();
	
	public boolean slim;
	
	public RenderMiniPlayer(RenderManager rm, boolean slim) {
		super(rm, slim);
		this.slim = slim;
		this.addLayer(new LayerDisguiseCape(this));
		grabLayers();
	}

	public void grabLayers(){
		if(Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getRenderManager() == null || Minecraft.getMinecraft().getRenderManager().getSkinMap() == null)return;
		grabbedLayers.clear();
        RenderPlayer renderer = Minecraft.getMinecraft().getRenderManager().getSkinMap().get(slim ? "slim" : "default");
        if(renderer !=null){
        	try{
        		List<LayerRenderer<AbstractClientPlayer>> grabbedLayers = ReflectionHelper.getPrivateValue(RenderLivingBase.class, renderer, 4);
        		if(grabbedLayers !=null){
        			for(LayerRenderer<AbstractClientPlayer> layer : grabbedLayers){
        				boolean found = false;
        				search : for(LayerRenderer<AbstractClientPlayer> layer2 : this.layerRenderers){
        					if(layer2.getClass() == layer.getClass()){
        						found = true;
        						break search;
        					}
        				}
        				if(!found){
        					this.grabbedLayers.add(layer);
        				}
        			}
        		}
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        }
	}
	
	@Override
	protected ResourceLocation getEntityTexture(AbstractClientPlayer entity)
	{
		EntityPlayer player = entity;
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
		if(ePlayer.getPlayerDisguiseUUID() !=null){
			return DownloadedTextures.getSkin(ePlayer.getPlayerDisguiseUUID());
		}
		return super.getEntityTexture(entity);
	}

	@Override
	protected void preRenderCallback(AbstractClientPlayer p_77041_1_, float p_77041_2_)
	{
		super.preRenderCallback(p_77041_1_, p_77041_2_);
		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(p_77041_1_);
		if(ePlayer.getCurrentDiguise() == DisguiseType.MINI){
			float f1 = (0.9375F/2)-0.1f;
			GlStateManager.scale(f1, f1, f1);
		}
	}
	
	public void setModelVisibilities(AbstractClientPlayer clientPlayer)
	{
		ModelPlayer modelplayer = this.getMainModel();
		
		if (clientPlayer.isSpectator())
		{
			modelplayer.setInvisible(false);
			modelplayer.bipedHead.showModel = true;
			modelplayer.bipedHeadwear.showModel = true;
		}
		else
		{
			ItemStack itemstack = clientPlayer.getHeldItemMainhand();
			ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
			modelplayer.setInvisible(true);
			modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
			modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
			modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
			modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
			modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
			modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
			modelplayer.isSneak = clientPlayer.isSneaking();
			ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
			ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

			if (itemstack != null)
			{
				modelbiped$armpose = ModelBiped.ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction = itemstack.getItemUseAction();

					if (enumaction == EnumAction.BLOCK)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
					}
					else if (enumaction == EnumAction.BOW)
					{
						modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
					}
				}
			}

			if (itemstack1 != null)
			{
				modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

				if (clientPlayer.getItemInUseCount() > 0)
				{
					EnumAction enumaction1 = itemstack1.getItemUseAction();

					if (enumaction1 == EnumAction.BLOCK)
					{
						modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
					}
					// FORGE: fix MC-88356 allow offhand to use bow and arrow animation
					else if (enumaction1 == EnumAction.BOW)
					{
						modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
					}
				}
			}

			if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
			{
				modelplayer.rightArmPose = modelbiped$armpose;
				modelplayer.leftArmPose = modelbiped$armpose1;
			}
			else
			{
				modelplayer.rightArmPose = modelbiped$armpose1;
				modelplayer.leftArmPose = modelbiped$armpose;
			}
		}
	}
	
	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(entity, this, partialTicks, x, y, z))) return;
        if (!entity.isUser() || Minecraft.getMinecraft().getRenderManager().renderViewEntity == entity)
        {
            double d0 = y;

            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP))
            {
                d0 = y - 0.125D;
            }

            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            superDoRender(entity, x, d0, z, entityYaw, partialTicks);
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(entity, this, partialTicks, x, y, z));
    }
	
	public void superDoRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<AbstractClientPlayer>(entity, this, x, y, z))) return;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
        this.mainModel.isRiding = shouldSit;
        this.mainModel.isChild = entity.isChild();

        try
        {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
            float f2 = f1 - f;

            if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase)
            {
                EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
                f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                f2 = f1 - f;
                float f3 = MathHelper.wrapDegrees(f2);

                if (f3 < -85.0F)
                {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F)
                {
                    f3 = 85.0F;
                }

                f = f1 - f3;

                if (f3 * f3 > 2500.0F)
                {
                    f += f3 * 0.2F;
                }
            }

            float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f8, f, partialTicks);
            float f4 = this.prepareScale(entity, partialTicks);
            float f5 = 0.0F;
            float f6 = 0.0F;

            if (!entity.isRiding())
            {
                f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                if (entity.isChild())
                {
                    f6 *= 3.0F;
                }

                if (f5 > 1.0F)
                {
                    f5 = 1.0F;
                }
            }

            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);

            if (this.renderOutlines)
            {
                boolean flag1 = this.setScoreTeamColor(entity);
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColorOverride(entity));

                if (!this.renderMarker)
                {
                    this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                }

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                {
                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                }

                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();

                if (flag1)
                {
                    this.unsetScoreTeamColor();
                }
            }
            else
            {
                boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f6, f5, f8, f2, f7, f4);

                if (flag)
                {
                    this.unsetBrightness();
                }

                GlStateManager.depthMask(true);

                if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                {
                    this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                }
            }

            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception)
        {
            ModLogger.error((String)"Couldn\'t render entity", (Throwable)exception);
        }

        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!this.renderOutlines)
        {
            this.renderName(entity, x, y, z);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<AbstractClientPlayer>(entity, this, x, y, z));
    }
	
	protected void renderLayers(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn)
    {
        for (LayerRenderer<AbstractClientPlayer> layerrenderer : this.layerRenderers)
        {
            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
            layerrenderer.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);

            if (flag)
            {
                this.unsetBrightness();
            }
        }
        for (LayerRenderer<AbstractClientPlayer> layerrenderer : this.grabbedLayers)
        {
            boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
            layerrenderer.doRenderLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);

            if (flag)
            {
                this.unsetBrightness();
            }
        }
    }
	
	public void renderName(AbstractClientPlayer entity, double x, double y, double z)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Pre<AbstractClientPlayer>(entity, this, x, y, z))) return;
        if (this.canRenderName(entity))
        {
            double d0 = entity.getDistanceSqToEntity(Minecraft.getMinecraft().getRenderManager().renderViewEntity);
            float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

            if (d0 < (double)(f * f))
            {
            	//TODO Use Events to Fix Display Name
                String s = ScorePlayerTeam.formatPlayerName(getFakeTeam(entity), entity.getDisplayNameString());
                GlStateManager.alphaFunc(516, 0.1F);
                this.renderEntityName(entity, x, y, z, s, d0);
            }
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Specials.Post<AbstractClientPlayer>(entity, this, x, y, z));
    }
	
	protected void renderEntityName(AbstractClientPlayer entityIn, double x, double y, double z, String name, double p_188296_9_)
    {
        if (p_188296_9_ < 100.0D)
        {
            Scoreboard scoreboard = entityIn.getWorldScoreboard();
            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);

            if (scoreobjective != null)
            {
                Score score = scoreboard.getOrCreateScore(getFakeUsername(entityIn), scoreobjective);
                this.renderLivingLabel(entityIn, score.getScorePoints() + " " + scoreobjective.getDisplayName(), x, y, z, 64);
                y += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * 0.025F);
            }
        }

        this.renderLivingLabel(entityIn, name, x, y, z, 64);
    }

    protected boolean canRenderName(AbstractClientPlayer entity)
    {
        EntityPlayerSP entityplayersp = Minecraft.getMinecraft().thePlayer;
        boolean flag = !entity.isInvisibleToPlayer(entityplayersp);

        if (entity != entityplayersp)
        {
            Team team = getFakeTeam(entity);
            Team team1 = entityplayersp.getTeam();

            if (team != null)
            {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

                switch (team$enumvisible)
                {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled() /*&& entity != Minecraft.getMinecraft().getRenderManager().renderViewEntity*/ && flag && !entity.isBeingRidden();
    }
    
    public String getFakeUsername(AbstractClientPlayer entityIn){
    	ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(entityIn);
    	String username = entityIn.getName();
    	if(ePlayer.getCurrentDiguise() == DisguiseType.PLAYER && ePlayer.getPlayerDisguiseUUID() !=null){
    		username = ProfileUtil.getUsername(ePlayer.getPlayerDisguiseUUID());
    	} 
    	return username;
    }
    
    public Team getFakeTeam(AbstractClientPlayer entityIn){
    	return entityIn.getWorldScoreboard().getPlayersTeam(getFakeUsername(entityIn));
    }
	
	protected int getTeamColorOverride(AbstractClientPlayer entityIn)
    {
        int i = 16777215;
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam)getFakeTeam(entityIn);

        if (scoreplayerteam != null)
        {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());

            if (s.length() >= 2)
            {
                i = this.getFontRendererFromRenderManager().getColorCode(s.charAt(1));
            }
        }

        return i;
    }
}
