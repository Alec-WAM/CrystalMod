package alec_wam.CrystalMod.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.google.common.collect.ImmutableList;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.CrystalModAPI;
import alec_wam.CrystalMod.api.tools.IMegaTool;
import alec_wam.CrystalMod.asm.ObfuscatedNames;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.blocks.crops.material.TileMaterialCrop;
import alec_wam.CrystalMod.capability.ExtendedPlayer;
import alec_wam.CrystalMod.capability.ExtendedPlayerProvider;
import alec_wam.CrystalMod.entities.accessories.GuiHorseEnderChest;
import alec_wam.CrystalMod.entities.accessories.HorseAccessories;
import alec_wam.CrystalMod.handler.ClientEventHandler.WrappedSound;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.network.PacketToolSwap;
import alec_wam.CrystalMod.items.tools.grapple.GrappleControllerBase;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.soundmuffler.TileSoundMuffler;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.client.RenderUtil;
import alec_wam.CrystalMod.world.game.tag.TagManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ClientEventHandler {
    //Invisible Armor
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
						if(!ItemStackTools.isNullStack(helmet) && ItemNBTHelper.verifyExistance(helmet, "CrystalMod.InvisArmor")){
							modelHelmet.bipedHead.isHidden = true;
							modelHelmet.bipedHeadwear.isHidden = true;
						}
					}
					if(modelChest !=null){
						if(!ItemStackTools.isNullStack(chest) && ItemNBTHelper.verifyExistance(chest, "CrystalMod.InvisArmor")){
							modelChest.bipedBody.isHidden = true;
							modelChest.bipedRightArm.isHidden = true;
							modelChest.bipedLeftArm.isHidden = true;
						}
					}
					if(modelLegs !=null){
						if(!ItemStackTools.isNullStack(legs) && ItemNBTHelper.verifyExistance(legs, "CrystalMod.InvisArmor")){
							modelLegs.bipedBody.isHidden = true;
							modelLegs.bipedLeftLeg.isHidden = true;
							modelLegs.bipedRightLeg.isHidden = true;
						}
					}
					if(modelBoots !=null){
						if(!ItemStackTools.isNullStack(boots) && ItemNBTHelper.verifyExistance(boots, "CrystalMod.InvisArmor")){
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
						if(!ItemStackTools.isNullStack(helmet) && ItemNBTHelper.verifyExistance(helmet, "CrystalMod.InvisArmor")){
							modelHelmet.bipedHead.isHidden = false;
							modelHelmet.bipedHeadwear.isHidden = false;
						}
					}
					if(modelChest !=null){
						if(!ItemStackTools.isNullStack(chest) && ItemNBTHelper.verifyExistance(chest, "CrystalMod.InvisArmor")){
							modelChest.bipedBody.isHidden = false;
							modelChest.bipedRightArm.isHidden = false;
							modelChest.bipedLeftArm.isHidden = false;
						}
					}
					if(modelLegs !=null){
						if(!ItemStackTools.isNullStack(legs) && ItemNBTHelper.verifyExistance(legs, "CrystalMod.InvisArmor")){
							modelLegs.bipedBody.isHidden = false;
							modelLegs.bipedLeftLeg.isHidden = false;
							modelLegs.bipedRightLeg.isHidden = false;
						}
					}
					if(modelBoots !=null){
						if(!ItemStackTools.isNullStack(boots) && ItemNBTHelper.verifyExistance(boots, "CrystalMod.InvisArmor")){
							modelBoots.bipedLeftLeg.isHidden = false;
							modelBoots.bipedRightLeg.isHidden = false;
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
    	GuiScreen gui = event.getGui();
    	if(gui !=null && gui instanceof GuiScreenHorseInventory){
    		GuiScreenHorseInventory horseGui = (GuiScreenHorseInventory)gui;
    		EntityHorse horse = (EntityHorse)ReflectionUtils.getPrivateValue(horseGui, GuiScreenHorseInventory.class, ObfuscatedNames.GuiScreenHorseInventory_horseEntity);
    		if(horse !=null && HorseAccessories.hasEnderChest(horse)){
    			ContainerHorseChest animalchest = new ContainerHorseChest("HorseChest", 2);
    			animalchest.setCustomName(horse.getName());
    			event.setGui(new GuiHorseEnderChest(CrystalMod.proxy.getClientPlayer().inventory, animalchest, horse));
    			PacketGuiMessage pkt = new PacketGuiMessage("Gui");
    			pkt.setOpenGui(GuiHandler.GUI_ID_ENTITY, horse.getEntityId(), 0, 0);
    			CrystalModNetwork.sendToServer(pkt);
    		}
    	}
    }
    
    //Flag
    @SuppressWarnings("deprecation")
	@SubscribeEvent
    public void onRenderHand(RenderHandEvent event)
    {
    	boolean test = true;
    	if(!test)return;
    	
    	EntityPlayer player = CrystalMod.proxy.getClientPlayer();
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

        IBlockState blockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(CrystalMod.proxy.getClientWorld(), entity, event.getPartialTicks());

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
        	
        	AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) CrystalMod.proxy.getClientPlayer();
            float fPitch = abstractclientplayer.prevRotationPitch + (abstractclientplayer.rotationPitch - abstractclientplayer.prevRotationPitch) * event.getPartialTicks();
            float fYaw = abstractclientplayer.prevRotationYaw + (abstractclientplayer.rotationYaw - abstractclientplayer.prevRotationYaw) * event.getPartialTicks();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(fPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(fYaw, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.popMatrix();
            
            int i = CrystalMod.proxy.getClientWorld().getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
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
            
            AbstractClientPlayer entitylivingbaseIn = (AbstractClientPlayer) CrystalMod.proxy.getClientPlayer();
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
    
    public static volatile int elapsedTicks;
    
    @SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
    	
    	if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT && event.side == Side.CLIENT) {
    		elapsedTicks++;
        }
    	
		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
		if (player != null) {
			if (!Minecraft.getMinecraft().isGamePaused() || !Minecraft.getMinecraft().isSingleplayer()) {
				try {
					Collection<GrappleControllerBase> controllers = GrappleHandler.controllers.values();
					for (GrappleControllerBase controller : controllers) {
						controller.clientTick();
					}
				} catch (ConcurrentModificationException e) {
					ModLogger.warning("ConcurrentModificationException caught during grapple update");
				}
				
				/*leftclick = (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindAttack) && Minecraft.getMinecraft().currentScreen == null);
				if (prevleftclick != leftclick) {
					if (player != null) {
						ItemStack stack = player.getHeldItemMainhand();
						if (stack != null) {
							Item item = stack.getItem();
							if (item instanceof clickitem) {
								if (leftclick) {
									((clickitem)item).onLeftClick(stack, player);
								} else {
									((clickitem)item).onLeftClickRelease(stack, player);
								}
							}
						}
					}
				}
				
				prevleftclick = leftclick;
				
				if (player.onGround) {
					if (enderlaunchtimer.containsKey(player.getEntityId())) {
						long timer = player.worldObj.getTotalWorldTime() - enderlaunchtimer.get(player.getEntityId());
						if (timer > 10) {
							this.resetlaunchertime(player.getEntityId());
						}
					}
				}*/
			}
		}
	}
    
    @SubscribeEvent
	public void onDrawHighlight(DrawBlockHighlightEvent event){
    	if(event.getTarget() !=null){
    		if(event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK){
    			BlockPos hitPos = event.getTarget().getBlockPos();
    			World world = event.getPlayer().getEntityWorld();
    			TileEntity tile = world.getTileEntity(hitPos);
    			if(tile !=null && tile instanceof TileMaterialCrop){
    				TileMaterialCrop crop = (TileMaterialCrop)tile;
    				if(crop.getCrop() == null)return;
    				int secondsLeft = crop.getTimeRemaining();
					int minutesLeft = secondsLeft / 60;
					int hoursLeft = minutesLeft / 60;
					int daysLeft = hoursLeft / 24;
					secondsLeft = secondsLeft % 60;
					minutesLeft = minutesLeft % 60;
					hoursLeft = hoursLeft % 24;
					String time = "";
					if(daysLeft > 0){
						time = daysLeft+"d "+hoursLeft+"h "+minutesLeft+"m "+secondsLeft+"s";
					}else if(hoursLeft > 0){
						time = hoursLeft+"h "+minutesLeft+"m "+secondsLeft+"s";
					}else if(minutesLeft > 0){
						time = minutesLeft+"m "+secondsLeft+"s";
					}else if(secondsLeft > 0){
						time = secondsLeft+"s";
					}
					List<String> list = new ArrayList<String>();
					list.add(CrystalModAPI.localizeCrop(crop.getCrop()));
					if(crop.isCombo())list.add("Combining");
					if(!time.isEmpty())list.add("Time Left: "+time);
					
					int timeLeft = crop.getGrowthTime();
					
					double percent = (timeLeft * 100) / crop.getCrop().getGrowthTime(tile.getWorld(), tile.getPos());
					
					double logic = percent * 0.01;
					double scale = Math.max(logic, 0.2);
					double y = 1.2 * scale;
					RenderUtil.renderFloatingText(list, hitPos.getX()+0.5, hitPos.getY()+y, hitPos.getZ()+0.5f, 0xffffff, true, event.getPartialTicks(), true);
    			}
    		}
    	}
    }
	
    @SubscribeEvent
    public void handleScroll(MouseEvent event){
    	int scroll = event.getDwheel();
    	EntityPlayer player = CrystalMod.proxy.getClientPlayer();
    	if(player !=null && player.isSneaking()){
    		if(scroll >=30){
    			//Up (Weapons)
    			if(BackpackUtil.canSwapWeapons(player)){
	    			CrystalModNetwork.sendToServer(new PacketToolSwap(0));
	    			event.setCanceled(true);
    			}
    		} else if(scroll <=-30){
    			//Down (Tools)
    			if(BackpackUtil.canSwapTools(player)){
	    			CrystalModNetwork.sendToServer(new PacketToolSwap(1));
	    			event.setCanceled(true);
    			}
    		}
    	}
    }
    
    public static int lastRenderDistance;
    
    //Mega Tool Handling
    @SubscribeEvent
    public void renderExtraBlockBreak(RenderWorldLastEvent event) {
    	
    	if(Minecraft.getMinecraft().gameSettings.renderDistanceChunks !=lastRenderDistance){
    		lastRenderDistance = Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
    		//Update Leaves
    		ModBlocks.crystalLeaves.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
    	}
    	
    	PlayerControllerMP controllerMP = Minecraft.getMinecraft().playerController;
    	EntityPlayer player = Minecraft.getMinecraft().player;
    	World world = player.getEntityWorld();

    	ItemStack tool = player.getHeldItemMainhand();

    	// AOE preview
    	if(ItemStackTools.isValid(tool)) {
    		Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
    		double distance = controllerMP.getBlockReachDistance();
    		RayTraceResult mop = renderEntity.rayTrace(distance, event.getPartialTicks());
    		if(mop != null) {
    			if(tool.getItem() instanceof IMegaTool) {
    				ImmutableList<BlockPos> extraBlocks = ((IMegaTool) tool.getItem()).getAOEBlocks(tool, world, player, mop.getBlockPos());
    				for(BlockPos pos : extraBlocks) {
    					event.getContext().drawSelectionBox(player, new RayTraceResult(new Vec3d(0, 0, 0), null, pos), 0, event.getPartialTicks());
    				}
    			}
    		}
    	}

    	// extra-blockbreak animation
    	if(controllerMP.getIsHittingBlock()) {
    		BlockPos pos = ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, controllerMP, 2);
    		if(ItemStackTools.isValid(tool) && tool.getItem() instanceof IMegaTool) {
    			drawBlockDamageTexture(Tessellator.getInstance(),
    					Tessellator.getInstance().getBuffer(),
    					player,
    					event.getPartialTicks(),
    					world,
    					((IMegaTool) tool.getItem()).getAOEBlocks(tool, world, player, pos));
    		}
    	}
    }

    // RenderGlobal.drawBlockDamageTexture
    public void drawBlockDamageTexture(Tessellator tessellatorIn, VertexBuffer vertexBuffer, Entity entityIn, float partialTicks, World world, List<BlockPos> blocks) {
    	double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
    	double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
    	double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;

    	TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
    	float curBlockDamageMP = ObfuscationReflectionHelper.getPrivateValue(PlayerControllerMP.class, Minecraft.getMinecraft().playerController, 4);
    	int progress = (int) (curBlockDamageMP * 10f) - 1; // 0-10

    	if(progress < 0) {
    		return;
    	}

    	renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	//preRenderDamagedBlocks BEGIN
    	GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
    	GlStateManager.enableBlend();
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
    	GlStateManager.doPolygonOffset(-3.0F, -3.0F);
    	GlStateManager.enablePolygonOffset();
    	GlStateManager.alphaFunc(516, 0.1F);
    	GlStateManager.enableAlpha();
    	GlStateManager.pushMatrix();
    	//preRenderDamagedBlocks END

    	vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    	vertexBuffer.setTranslation(-d0, -d1, -d2);
    	vertexBuffer.noColor();

    	for(BlockPos blockpos : blocks) {
    		double d3 = (double) blockpos.getX() - d0;
    		double d4 = (double) blockpos.getY() - d1;
    		double d5 = (double) blockpos.getZ() - d2;
    		Block block = world.getBlockState(blockpos).getBlock();
    		TileEntity te = world.getTileEntity(blockpos);
    		boolean hasBreak = block instanceof BlockChest || block instanceof BlockEnderChest
    				|| block instanceof BlockSign || block instanceof BlockSkull;
    		if(!hasBreak) {
    			hasBreak = te != null && te.canRenderBreaking();
    		}

    		if(!hasBreak) {
    			IBlockState iblockstate = world.getBlockState(blockpos);

    			if(iblockstate.getBlock().getMaterial(iblockstate) != Material.AIR) {
    				TextureAtlasSprite textureatlassprite = RenderUtil.getSprite("minecraft:blocks/destroy_stage_"+progress)/*ClientProxy.destroyBlockIcons[progress]*/;
    				BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
    				blockrendererdispatcher.renderBlockDamage(iblockstate, blockpos, textureatlassprite, world);
    			}
    		}
    	}

    	tessellatorIn.draw();
    	vertexBuffer.setTranslation(0.0D, 0.0D, 0.0D);
    	// postRenderDamagedBlocks BEGIN
    	GlStateManager.disableAlpha();
    	GlStateManager.doPolygonOffset(0.0F, 0.0F);
    	GlStateManager.disablePolygonOffset();
    	GlStateManager.enableAlpha();
    	GlStateManager.depthMask(true);
    	GlStateManager.popMatrix();
    	// postRenderDamagedBlocks END
    }
    
    @SubscribeEvent
    public void playSound(PlaySoundEvent event){
    	World world = CrystalMod.proxy.getClientWorld();
    	if(world == null)return;
    	ISound sound = event.getSound();
    	if(sound instanceof ITickableSound)return;
    	AxisAlignedBB bb = new AxisAlignedBB((double)sound.getXPosF(), (double)sound.getYPosF(), (double)sound.getZPosF(), (double)sound.getXPosF(), (double)sound.getYPosF(), (double)sound.getZPosF()).expand(16, 16, 16);
    	List<TileSoundMuffler> mufflers = BlockUtil.searchBoxForTiles(world, bb, TileSoundMuffler.class, null);
    	for(TileSoundMuffler muffler : mufflers){
    		if(muffler.isSoundInList(sound.getSoundLocation())){
    			if(muffler.getVolume() <= 0.0f){
    				event.setResultSound(null);
    			} else {
    				event.setResultSound(new WrappedSound(event.getSound(), muffler.getVolume()));
    			}
    			break;
    		}
    	}
    }
    
    public class WrappedSound implements ISound {

    	public ISound sound;
    	public float volume; 
    	
    	public WrappedSound(ISound sound, float volume){
    		this.sound = sound;
    		this.volume = volume;
    	}
    	
		@Override
		public ResourceLocation getSoundLocation() {
			return sound.getSoundLocation();
		}

		@Override
		public SoundEventAccessor createAccessor(SoundHandler handler) {
			return sound.createAccessor(handler);
		}

		@Override
		public Sound getSound() {
			return sound.getSound();
		}

		@Override
		public SoundCategory getCategory() {
			return sound.getCategory();
		}

		@Override
		public boolean canRepeat() {
			return sound.canRepeat();
		}

		@Override
		public int getRepeatDelay() {
			return sound.getRepeatDelay();
		}

		@Override
		public float getVolume() {
			return volume;
		}

		@Override
		public float getPitch() {
			return sound.getPitch();
		}

		@Override
		public float getXPosF() {
			return sound.getXPosF();
		}

		@Override
		public float getYPosF() {
			return sound.getYPosF();
		}

		@Override
		public float getZPosF() {
			return sound.getZPosF();
		}

		@Override
		public AttenuationType getAttenuationType() {
			return sound.getAttenuationType();
		}

	}
}
