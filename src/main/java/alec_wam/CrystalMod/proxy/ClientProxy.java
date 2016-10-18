package alec_wam.CrystalMod.proxy;

import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.glu.Project;

import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.glass.ModelGlass;
import alec_wam.CrystalMod.blocks.glass.BlockCrystalGlass.GlassType;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.client.model.BakedCustomItemModel;
import alec_wam.CrystalMod.client.model.LayerDragonWings;
import alec_wam.CrystalMod.entities.ModEntites;
import alec_wam.CrystalMod.fluids.FluidColored;
import alec_wam.CrystalMod.fluids.Fluids;
import alec_wam.CrystalMod.items.ItemDragonWings;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.tiles.machine.power.battery.BlockBattery.BatteryType;
import alec_wam.CrystalMod.tiles.machine.power.battery.ModelBattery;
import alec_wam.CrystalMod.tiles.pipes.attachments.ModelAttachment;
import alec_wam.CrystalMod.tiles.pipes.covers.ModelCover;
import alec_wam.CrystalMod.tiles.pipes.estorage.panel.GuiPanel;
import alec_wam.CrystalMod.tiles.pipes.estorage.storage.hdd.GuiHDDInterface;
import alec_wam.CrystalMod.tiles.pipes.item.GhostItemHelper;
import alec_wam.CrystalMod.tiles.pipes.render.BakedModelLoader;
import alec_wam.CrystalMod.tiles.tank.ModelTank;
import alec_wam.CrystalMod.tiles.tank.BlockTank.TankType;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.world.game.tag.TagManager;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ModItems.initClient();
        ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        ModBlocks.initClient();
        ModEntites.initClient();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
        LayerDragonWings dragonWingsRenderer = new LayerDragonWings();
		for (RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()){
			renderer.addLayer(dragonWingsRenderer);
		}
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    
    public static final List<String> CUSTOM_RENDERS = Lists.newArrayList();//new String[]{"flagItem", "dragonWings", "mobEssence"};
    
    @SubscribeEvent
    public void onBakeModel(final ModelBakeEvent event) {
    	
        for(GlassType type : GlassType.values()){
        	event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.crystalGlass.getRegistryName(), "type="+type.getName()), ModelGlass.INSTANCE);
        }
        event.getModelRegistry().putObject(new ModelResourceLocation(ModBlocks.crystalGlass.getRegistryName(), "inventory"), ModelGlass.INSTANCE);
        
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:battery", "normal"), ModelBattery.INSTANCE);
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:battery", "inventory"), ModelBattery.INSTANCE);
        
        ModelCover.map.clear();
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:pipecover", "inventory"), ModelCover.INSTANCE);
        
        ModelAttachment.map.clear();
        event.getModelRegistry().putObject(new ModelResourceLocation(ModItems.pipeAttachmant.getRegistryName(), "inventory"), ModelAttachment.INSTANCE);
        
        for(TankType type : TankType.values()){
        	event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:crystaltank", "type="+type.getName()), ModelTank.INSTANCE);
        }
        event.getModelRegistry().putObject(new ModelResourceLocation("crystalmod:crystaltank", "inventory"), ModelTank.INSTANCE);
        
        for(String s : CUSTOM_RENDERS)
		{
			ModelResourceLocation model = new ModelResourceLocation("crystalmod:" + s, "inventory");
	        Object obj = event.getModelRegistry().getObject(model);
	        
	        if(obj instanceof IBakedModel)
	        {
	        	event.getModelRegistry().putObject(model, new BakedCustomItemModel((IBakedModel)obj));
	        }else{
	        	event.getModelRegistry().putObject(model, new BakedCustomItemModel(null));
	        }
		}
    }
    
    public boolean isPaused()
	{
		if(FMLClientHandler.instance().getClient().isSingleplayer() && !FMLClientHandler.instance().getClient().getIntegratedServer().getPublic())
		{
			GuiScreen screen = FMLClientHandler.instance().getClient().currentScreen;

			if(screen != null && screen.doesGuiPauseGame())
			{
				return true;
			}
		}

		return false;
	}
    
    @SubscribeEvent
    public void onRender(final RenderPlayerEvent.Pre event){
    	if(event.getEntityPlayer() ==null)return;
		try{
			@SuppressWarnings("unchecked")
			List<LayerRenderer<EntityLivingBase>> layers = (List<LayerRenderer<EntityLivingBase>>) ReflectionUtils.getPrivateValue(event.getRenderer(), RenderLivingBase.class, ObfuscatedNames.RenderLivingBase_layerRenderers);
			for(LayerRenderer<EntityLivingBase> layer : layers){
				if(layer instanceof LayerBipedArmor){
					LayerBipedArmor armor = (LayerBipedArmor)layer;
					ItemStack helmet = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					ModelBiped modelHelmet = armor.getModelFromSlot(EntityEquipmentSlot.HEAD);
					ItemStack chest = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					ModelBiped modelChest = armor.getModelFromSlot(EntityEquipmentSlot.CHEST);
					ItemStack legs = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
					ModelBiped modelLegs = armor.getModelFromSlot(EntityEquipmentSlot.LEGS);
					ItemStack boots = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.FEET);
					ModelBiped modelBoots = armor.getModelFromSlot(EntityEquipmentSlot.FEET);
					if(modelHelmet !=null){
						if(helmet !=null && ItemNBTHelper.verifyExistance(helmet, "CrystalMod.InvisArmor")){
							modelHelmet.bipedHead.isHidden = true;
							modelHelmet.bipedHeadwear.isHidden = true;
						}
					}
					if(modelChest !=null){
						if(chest !=null && ItemNBTHelper.verifyExistance(chest, "CrystalMod.InvisArmor")){
							modelChest.bipedBody.isHidden = true;
							modelChest.bipedRightArm.isHidden = true;
							modelChest.bipedLeftArm.isHidden = true;
						}
					}
					if(modelLegs !=null){
						if(legs !=null && ItemNBTHelper.verifyExistance(legs, "CrystalMod.InvisArmor")){
							modelLegs.bipedBody.isHidden = true;
							modelLegs.bipedLeftLeg.isHidden = true;
							modelLegs.bipedRightLeg.isHidden = true;
						}
					}
					if(modelBoots !=null){
						if(boots !=null && ItemNBTHelper.verifyExistance(boots, "CrystalMod.InvisArmor")){
							modelBoots.bipedLeftLeg.isHidden = true;
							modelBoots.bipedRightLeg.isHidden = true;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
    }
    
    @SubscribeEvent
    public void renderSpecials(RenderPlayerEvent.Post event){

    	try{
    		@SuppressWarnings("unchecked")
			List<LayerRenderer<EntityLivingBase>> layers = (List<LayerRenderer<EntityLivingBase>>) ReflectionUtils.getPrivateValue(event.getRenderer(), RenderLivingBase.class, ObfuscatedNames.RenderLivingBase_layerRenderers);
			for(LayerRenderer<EntityLivingBase> layer : layers){
				if(layer instanceof LayerBipedArmor){
					LayerBipedArmor armor = (LayerBipedArmor)layer;
					ItemStack helmet = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
					ModelBiped modelHelmet = armor.getModelFromSlot(EntityEquipmentSlot.HEAD);
					ItemStack chest = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					ModelBiped modelChest = armor.getModelFromSlot(EntityEquipmentSlot.CHEST);
					ItemStack legs = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
					ModelBiped modelLegs = armor.getModelFromSlot(EntityEquipmentSlot.LEGS);
					ItemStack boots = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.FEET);
					ModelBiped modelBoots = armor.getModelFromSlot(EntityEquipmentSlot.FEET);
					if(modelHelmet !=null){
						if(helmet !=null && ItemNBTHelper.verifyExistance(helmet, "CrystalMod.InvisArmor")){
							modelHelmet.bipedHead.isHidden = false;
							modelHelmet.bipedHeadwear.isHidden = false;
						}
					}
					if(modelChest !=null){
						if(chest !=null && ItemNBTHelper.verifyExistance(chest, "CrystalMod.InvisArmor")){
							modelChest.bipedBody.isHidden = false;
							modelChest.bipedRightArm.isHidden = false;
							modelChest.bipedLeftArm.isHidden = false;
						}
					}
					if(modelLegs !=null){
						if(legs !=null && ItemNBTHelper.verifyExistance(legs, "CrystalMod.InvisArmor")){
							modelLegs.bipedBody.isHidden = false;
							modelLegs.bipedLeftLeg.isHidden = false;
							modelLegs.bipedRightLeg.isHidden = false;
						}
					}
					if(modelBoots !=null){
						if(boots !=null && ItemNBTHelper.verifyExistance(boots, "CrystalMod.InvisArmor")){
							modelBoots.bipedLeftLeg.isHidden = false;
							modelBoots.bipedRightLeg.isHidden = false;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    	
    	//renderDragonWings(event.getEntityPlayer(), event.partialRenderTick);
    	
    	
    	
    }
    
    @SubscribeEvent
    public void onStitch(final TextureStitchEvent.Pre event) {
    	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/blank"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/power_plus"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/item_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/fluid_square"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/storage_square"));
        for(int i = 0; i < 4; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/power_square_"+i));
        }
        for(int i = 0; i < 4; i++){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/rfpower_square_"+i));
        }
        
        for(GlassType type : GlassType.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/crystal_"+type.getName()+"_glass"));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/attachment/import"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/iron_cap"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_inout"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_in"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/pipe/io_out"));
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/glass"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/tank_creative"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/tank/tank_creative_top"));
        
        for(BatteryType type : BatteryType.values()){
        	event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/battery_"+type.getName().toLowerCase()));
        }
        
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_blocked"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_in"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/io_out"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/uncharged"));
        event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/charged"));
        for(int i = 0; i < 9; i++){
            event.getMap().registerSprite(new ResourceLocation("crystalmod:blocks/machine/battery/meter/"+i));
        }
        if (Fluids.fluidXpJuice != null) {
            event.getMap().registerSprite(Fluids.fluidXpJuice.getStill());
            event.getMap().registerSprite(Fluids.fluidXpJuice.getFlowing());
        }
        
        event.getMap().registerSprite(FluidColored.LiquidStill);
        event.getMap().registerSprite(FluidColored.LiquidFlowing);
    }
    
    public EntityPlayer getPlayerForUsername(String playerName) {
    	return null;
    }
    
    public boolean isOp(GameProfile profile){
    	return getClientWorld().getWorldInfo().areCommandsAllowed();
    }
    
    @SuppressWarnings("deprecation")
	@SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
    	boolean test = true;
    	if(!test)return;
    	
    	EntityPlayer player = getClientPlayer();
    	if(player == null)return;
    	
    	ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
    	if(extPlayer == null)return;
    	
    	if(!extPlayer.hasFlag())return;
    	
    	GlStateManager.clear(256);
    	GlStateManager.pushMatrix();
    	GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        //float f = 0.07F;
        
        
        Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
        float fov = 70.0F;

        IBlockState blockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(Minecraft.getMinecraft().theWorld, entity, event.getPartialTicks());

        if (blockState.getBlock().getMaterial(blockState) == Material.WATER)
        {
        	fov = fov * 60.0F / 70.0F;
        }

        fov = net.minecraftforge.client.ForgeHooksClient.getFOVModifier(Minecraft.getMinecraft().entityRenderer, entity, blockState, event.getPartialTicks(), fov);
        
        Project.gluPerspective(fov, (float)Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight, 0.05F, (float)(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16) * 2.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();

        GlStateManager.pushMatrix();
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().playerController.isSpectator())
        {
        	Minecraft.getMinecraft().entityRenderer.enableLightmap();
            
        	if(Minecraft.getMinecraft().gameSettings.viewBobbing){
	        	if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer)
	            {
	                EntityPlayer entityplayer = (EntityPlayer)Minecraft.getMinecraft().getRenderViewEntity();
	                float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
	                float f1 = -(entityplayer.distanceWalkedModified + f * event.getPartialTicks());
	                float f2 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * event.getPartialTicks();
	                float f3 = entityplayer.prevCameraPitch + (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * event.getPartialTicks();
	                GlStateManager.translate(MathHelper.sin(f1 * (float)Math.PI) * f2 * 0.5F, -Math.abs(MathHelper.cos(f1 * (float)Math.PI) * f2), 0.0F);
	                GlStateManager.rotate(MathHelper.sin(f1 * (float)Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
	                GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * (float)Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
	                GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
	            }
        	}
        	
        	AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().thePlayer;
            float fPitch = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * event.getPartialTicks();
            float fYaw = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * event.getPartialTicks();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(fPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(fYaw, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.popMatrix();
            
            int i = Minecraft.getMinecraft().theWorld.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
            float f = (float)(i & 65535);
            float f1 = (float)(i >> 16);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
            
            EntityPlayerSP entityplayerspIn = (EntityPlayerSP)abstractclientplayer;
            float f23 = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * event.getPartialTicks();
            float f24 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * event.getPartialTicks();
            GlStateManager.rotate((entityplayerspIn.rotationPitch - f23) * 0.1F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate((entityplayerspIn.rotationYaw - f24) * 0.1F, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.35f, -0.9, -0.5);
            GlStateManager.rotate(-75.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-45.0F-85, 0.0F, 1.0F, 0.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(abstractclientplayer.getLocationSkin());
            Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().<AbstractClientPlayer>getEntityRenderObject(abstractclientplayer);
            GlStateManager.disableCull();
            RenderPlayer renderplayer = (RenderPlayer)render;
            renderplayer.renderLeftArm(abstractclientplayer);
            
            
            //FLAG
            GlStateManager.pushMatrix();
            GlStateManager.rotate(45, 0, 1, 1);
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(80, 0, 0, 1);
            GlStateManager.rotate(85, 0, 1, 0);
            GlStateManager.translate(-0.5, -0.5, -0.5);
            
            //FLAG RENDER
            double height = 1.8;
            GlStateManager.translate(0.3, -height/2, -0.1);
            
            AbstractClientPlayer entitylivingbaseIn = Minecraft.getMinecraft().thePlayer;
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double)event.getPartialTicks() - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double)event.getPartialTicks());
            double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double)event.getPartialTicks() - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double)event.getPartialTicks());
            float fYaw2 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * event.getPartialTicks();
            double d3 = (double)MathHelper.sin(fYaw2 * (float)Math.PI / 180.0F);
            double d4 = (double)(-MathHelper.cos(fYaw2 * (float)Math.PI / 180.0F));
            float f3 = (float)(d0 * d4 - d2 * d3) * (120.0F);
            
            float angle = -f3 / 2.0F;
            TagManager.getInstance().renderFlag(extPlayer.getFlagColor(), angle);
            GlStateManager.popMatrix();
            //FLAG RENDER END
            
            GlStateManager.enableCull();
        	GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            Minecraft.getMinecraft().entityRenderer.disableLightmap();
        }

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }
    
    @SubscribeEvent
    public void onTooltipEvent(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (stack == null)
        {
            return;
        }

        
        if(Minecraft.getMinecraft().currentScreen !=null && (Minecraft.getMinecraft().currentScreen instanceof GuiHDDInterface || Minecraft.getMinecraft().currentScreen instanceof GuiPanel)){
        	if(ItemNBTHelper.getBoolean(stack, "DummyItem", false)){
        		if(stack.stackSize > 999){
        			event.getToolTip().add("Stack Size: "+stack.stackSize);
        		}
        	}
        }
        
        if (GhostItemHelper.hasGhostAmount(stack))
        {
            int amount = GhostItemHelper.getItemGhostAmount(stack);
            if (amount == 0)
            {
                event.getToolTip().add("Everything");
            } else
            {
                event.getToolTip().add("Ghost item amount: " + amount);
            }
        }
        
        if(ItemNBTHelper.verifyExistance(stack, ItemDragonWings.UPGRADE_NBT)){
        	event.getToolTip().add(TextFormatting.DARK_PURPLE+""+TextFormatting.UNDERLINE+"Dragon Wings");
        }
        
        if(ItemNBTHelper.verifyExistance(stack, "CrystalMod.InvisArmor")){
        	event.getToolTip().add(TextFormatting.WHITE+""+TextFormatting.UNDERLINE+"Invisible");
        }
    }
    
    @Override
    public EntityPlayer getClientPlayer() {
      return Minecraft.getMinecraft().thePlayer;
    }
    
    @Override
    public World getClientWorld() {
      return Minecraft.getMinecraft().theWorld;
    }
    
    @Override
    public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
      if (entityPlayer instanceof EntityPlayerMP) {
        return ((EntityPlayerMP) entityPlayer).interactionManager.getBlockReachDistance();
      }
      return Minecraft.getMinecraft().playerController.getBlockReachDistance();
    }
    
    public World getWorld(int dim) {
		return null;
	}
    
    public boolean isShiftKeyDown()
    {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }
}
