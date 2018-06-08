package alec_wam.CrystalMod.handler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
import alec_wam.CrystalMod.fluids.ModFluids;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.enchancements.ModEnhancements;
import alec_wam.CrystalMod.items.tools.backpack.BackpackUtil;
import alec_wam.CrystalMod.items.tools.backpack.network.PacketToolSwap;
import alec_wam.CrystalMod.items.tools.grapple.GrappleControllerBase;
import alec_wam.CrystalMod.items.tools.grapple.GrappleHandler;
import alec_wam.CrystalMod.network.CrystalModNetwork;
import alec_wam.CrystalMod.network.packets.PacketGuiMessage;
import alec_wam.CrystalMod.tiles.cluster.TileCrystalCluster;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerCU;
import alec_wam.CrystalMod.tiles.machine.inventory.charger.TileEntityInventoryChargerRF;
import alec_wam.CrystalMod.tiles.machine.mobGrinder.TileEntityMobGrinder;
import alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterCUtoRF;
import alec_wam.CrystalMod.tiles.machine.power.converter.TileEnergyConverterRFtoCU;
import alec_wam.CrystalMod.tiles.machine.specialengines.TileFiniteEngine;
import alec_wam.CrystalMod.tiles.machine.specialengines.TileInfiniteEngine;
import alec_wam.CrystalMod.tiles.pipes.TileEntityPipe;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverCutter;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverRender;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil;
import alec_wam.CrystalMod.tiles.pipes.covers.CoverUtil.CoverData;
import alec_wam.CrystalMod.tiles.pipes.covers.ItemPipeCover;
import alec_wam.CrystalMod.tiles.soundmuffler.TileSoundMuffler;
import alec_wam.CrystalMod.tiles.spawner.EntityEssenceInstance;
import alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import alec_wam.CrystalMod.tiles.spawner.TileEntityCustomSpawner;
import alec_wam.CrystalMod.tiles.xp.TileEntityXPVacuum;
import alec_wam.CrystalMod.util.BlockUtil;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.ProfileUtil;
import alec_wam.CrystalMod.util.ReflectionUtils;
import alec_wam.CrystalMod.util.TimeUtil;
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
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
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
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.item.EntityMinecartFurnace;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
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
						if(ItemStackTools.isValid(helmet) && ModEnhancements.INVIS_ARMOR.isApplied(helmet)){
							modelHelmet.bipedHead.isHidden = true;
							modelHelmet.bipedHeadwear.isHidden = true;
						}
					}
					if(modelChest !=null){
						if(ItemStackTools.isValid(chest) && ModEnhancements.INVIS_ARMOR.isApplied(chest)){
							modelChest.bipedBody.isHidden = true;
							modelChest.bipedRightArm.isHidden = true;
							modelChest.bipedLeftArm.isHidden = true;
						}
					}
					if(modelLegs !=null){
						if(ItemStackTools.isValid(legs) && ModEnhancements.INVIS_ARMOR.isApplied(legs)){
							modelLegs.bipedBody.isHidden = true;
							modelLegs.bipedLeftLeg.isHidden = true;
							modelLegs.bipedRightLeg.isHidden = true;
						}
					}
					if(modelBoots !=null){
						if(ItemStackTools.isValid(boots) && ModEnhancements.INVIS_ARMOR.isApplied(boots)){
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
						if(ItemStackTools.isValid(helmet) && ModEnhancements.INVIS_ARMOR.isApplied(helmet)){
							modelHelmet.bipedHead.isHidden = false;
							modelHelmet.bipedHeadwear.isHidden = false;
						}
					}
					if(modelChest !=null){
						if(ItemStackTools.isValid(chest) && ModEnhancements.INVIS_ARMOR.isApplied(chest)){
							modelChest.bipedBody.isHidden = false;
							modelChest.bipedRightArm.isHidden = false;
							modelChest.bipedLeftArm.isHidden = false;
						}
					}
					if(modelLegs !=null){
						if(ItemStackTools.isValid(legs) && ModEnhancements.INVIS_ARMOR.isApplied(legs)){
							modelLegs.bipedBody.isHidden = false;
							modelLegs.bipedLeftLeg.isHidden = false;
							modelLegs.bipedRightLeg.isHidden = false;
						}
					}
					if(modelBoots !=null){
						if(ItemStackTools.isValid(boots) && ModEnhancements.INVIS_ARMOR.isApplied(boots)){
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
    		AbstractHorse horse = (AbstractHorse)ReflectionUtils.getPrivateValue(horseGui, GuiScreenHorseInventory.class, ObfuscatedNames.GuiScreenHorseInventory_horseEntity);
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
    
    private final Color DUAL_BAR_COLOR = new Color(0x9700B5);
	@SubscribeEvent
    public void screenInfo(RenderGameOverlayEvent event){
    	Minecraft mc = Minecraft.getMinecraft();
    	if(event.getType() == ElementType.ALL && mc.currentScreen == null){
    		ScaledResolution sr = new ScaledResolution(mc);
    		if(mc.objectMouseOver == null)return;
    		BlockPos pos = mc.objectMouseOver.getBlockPos();
    		if(pos == null)return;
    		TileEntity tile = CrystalMod.proxy.getClientWorld().getTileEntity(pos);
    		EntityPlayer player = CrystalMod.proxy.getClientPlayer();
    		if(tile !=null){
	    		int rf = 0;
	    		int maxRF = 0;
	    		boolean renderRF = false;
	    		
	    		int cu = 0;
	    		int maxCU = 0;
	    		boolean renderCU = false;
	    		
	    		int dualPower = 0;
	    		int maxDualPower = 0;
	    		boolean renderDualPower = false;
	    		
	    		FluidStack fluid = null;
	    		int capacity = 0;
	    		boolean renderFluid = false;
	    		
	    		List<String> infolines = Lists.newArrayList(); 
				List<String> list = Lists.newArrayList();
	    		int barHeight = 58;
	    		int offsetY = 0;	    
	    		
	    		if(tile instanceof TileEnergyConverterCUtoRF){
	    			TileEnergyConverterCUtoRF con = (TileEnergyConverterCUtoRF)tile;
	    			rf = con.getEnergyStored();
	    			maxRF = con.getMaxEnergyStored();
	    			renderRF = true;
	    		}
	    		if(tile instanceof TileEntityInventoryChargerRF){
	    			TileEntityInventoryChargerRF con = (TileEntityInventoryChargerRF)tile;
	    			rf = con.energyStorage.getEnergyStored();
	    			maxRF = con.energyStorage.getMaxEnergyStored();
	    			renderRF = true;
	    		}
	    		if(tile instanceof TileEnergyConverterRFtoCU){
	    			TileEnergyConverterRFtoCU con = (TileEnergyConverterRFtoCU)tile;
	    			cu = con.getEnergyStored();
	    			maxCU = con.getMaxEnergyStored();
	    			renderCU = true;
	    		}
	    		if(tile instanceof TileEntityInventoryChargerCU){
	    			TileEntityInventoryChargerCU con = (TileEntityInventoryChargerCU)tile;
	    			cu = con.energyStorage.getCEnergyStored();
	    			maxCU = con.energyStorage.getMaxCEnergyStored();
	    			renderCU = true;
	    		}
	    		
	    		if(tile instanceof TileFiniteEngine){
	    			TileFiniteEngine engine = (TileFiniteEngine)tile;
	    			dualPower = engine.energyStorage.getEnergyStored();
	    			maxDualPower = engine.energyStorage.getMaxEnergyStored();
	    			renderDualPower = true;
	    		}
	    		if(tile instanceof TileInfiniteEngine){
	    			TileInfiniteEngine engine = (TileInfiniteEngine)tile;
	    			dualPower = engine.energyStorage.getEnergyStored();
	    			maxDualPower = engine.energyStorage.getMaxEnergyStored();
	    			renderDualPower = true;
	    		}
	    		
	    		if(tile instanceof TileEntityXPVacuum){
	    			TileEntityXPVacuum con = (TileEntityXPVacuum)tile;
	    			fluid = con.xpCon.getFluid();
	    			capacity = con.xpCon.getCapacity();
	    			renderFluid = true;
	    		}	    		
	    		if(tile instanceof TileEntityMobGrinder){
	    			TileEntityMobGrinder con = (TileEntityMobGrinder)tile;
	    			fluid = con.xpCon.getFluid();
	    			capacity = con.xpCon.getCapacity();
	    			renderFluid = true;
	    			cu = con.energyStorage.getCEnergyStored();
	    			maxCU = con.energyStorage.getMaxCEnergyStored();
	    			renderCU = true;
	    			offsetY = 12;
	    		}
	    		
	    		if(tile instanceof TileCrystalCluster){
	    			TileCrystalCluster cluster = (TileCrystalCluster)tile;
	    			String power = "Power Output: (Max) "+cluster.getClusterData().getPowerOutput()+" / (Current) "+cluster.getPowerOutput();
					String health = "Health: (Current) "+cluster.getHealth() + " / (Max) " + TimeUtil.MINECRAFT_DAY_TICKS;
					String speed = "Regen: "+cluster.getClusterData().getRegenSpeed();
					list.add(power);
					list.add(health);
					list.add(speed);
	    		}	    		
	    		if(tile instanceof TileEntityCustomSpawner){
	    			TileEntityCustomSpawner spawner = (TileEntityCustomSpawner)tile;
	    			if(!player.isSneaking()){
						list.add(Lang.localize("msg.spawnerInfo1.txt") + ": ");
						EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(spawner.getBaseLogic().getEntityNameToSpawn());
						if(essence !=null){
							List<String> info = Lists.newArrayList();
							essence.addInfo(info);
							for(String line : info){
								list.add(" -"+line);
							}
						}
						list.add(Lang.localize("msg.spawnerInfo2.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().requiresPlayer);
						list.add(Lang.localize("msg.spawnerInfo3.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().ignoreSpawnRequirements);
						list.add(Lang.localize("msg.spawnerInfo4.txt") + ": " + TextFormatting.DARK_AQUA + spawner.getBaseLogic().spawnSpeed);
						list.add(Lang.localize("msg.spawnerInfo5.txt"));
					}else{
						list.add(Lang.localize("msg.spawnerInfo6.txt"));
						list.add(Lang.localize("msg.spawnerInfo7.txt"));
						list.add(Lang.localize("msg.spawnerInfo8.txt"));
						list.add(Lang.localize("msg.spawnerInfo9.txt"));
					}
	    		}
	    		
	    		//intelect
	    		ExtendedPlayer ePlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
	    		boolean blockInfo = ePlayer.getIntellectTime() > 0;
	    		if(blockInfo && event.getType() == ElementType.ALL && !Minecraft.getMinecraft().gameSettings.showDebugInfo && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)){
	    			RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
	    			if(ray !=null){
	    				if(ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit !=null){
	    					Entity entity = ray.entityHit;
	    					collectEntityData(infolines, ray, entity);
	    				}
	    				if(ray.typeOfHit == RayTraceResult.Type.BLOCK){
	    					World world = CrystalMod.proxy.getClientWorld();
	    					IBlockState state = world.getBlockState(pos);
	    					if(state !=null){
	    						Block block = state.getBlock();
	    						ItemStack blockStack = block.getPickBlock(state, ray, world, pos, player);
	    						if(ItemStackTools.isValid(blockStack)){
	    							infolines.add(blockStack.getDisplayName());
	    							infolines.add(blockStack.getItem().getRegistryName().toString());
	    						}
	    					}
	    				}
	    			}
	    		}
	    		
	    		if(!infolines.isEmpty()){
	    			offsetY+=((infolines.size() * 10)+10);
	    		}
	    		
	    		if(renderCU){
	    			list.add("CU: "+cu+" / "+maxCU);
		    		GlStateManager.pushMatrix();
		    		GlStateManager.translate(10, sr.getScaledHeight()-(barHeight + 10 + offsetY), 0);	    		
		    		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);	    			    		
		    		int colorBorder = Color.GRAY.darker().getRGB();
		    		int colorInside = Color.GRAY.getRGB();
		    		RenderUtil.drawGradientRect(-1, -11, -11, 13, (barHeight - 9), colorBorder, colorBorder);
		    		RenderUtil.drawGradientRect(0, -10, -10, 12, (barHeight - 10), colorInside, colorInside);
		    		RenderUtil.renderPowerBar(0, -10, 0, 12, (barHeight), cu, maxCU, Color.CYAN.getRGB(), Color.CYAN.darker().getRGB());
		    		GlStateManager.popMatrix();
	    		}
	    		
	    		if(renderRF){
	    			list.add("RF: "+rf+" / "+maxRF);
	    			int offsetX = renderCU ? 16 : 0;
		    		GlStateManager.pushMatrix();
		    		GlStateManager.translate(10 + offsetX, sr.getScaledHeight()-(barHeight + 10 + offsetY), 0);	    		
		    		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);	    			    		
		    		int colorBorder = Color.GRAY.darker().getRGB();
		    		int colorInside = Color.GRAY.getRGB();
		    		RenderUtil.drawGradientRect(-1, -11, -11, 13, (barHeight - 9), colorBorder, colorBorder);
		    		RenderUtil.drawGradientRect(0, -10, -10, 12, (barHeight - 10), colorInside, colorInside);
		    		RenderUtil.renderPowerBar(0, -10, 0, 12, (barHeight), rf, maxRF, Color.RED.getRGB(), Color.RED.darker().getRGB());
		    		GlStateManager.popMatrix();
	    		}	    
	    		
	    		if(renderDualPower){
	    			list.add("Energy: "+dualPower+" / "+maxDualPower);
		    		GlStateManager.pushMatrix();
		    		GlStateManager.translate(10, sr.getScaledHeight()-(barHeight + 10 + offsetY), 0);	    		
		    		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);	    			    		
		    		int colorBorder = Color.GRAY.darker().getRGB();
		    		int colorInside = Color.GRAY.getRGB();
		    		RenderUtil.drawGradientRect(-1, -11, -11, 13, (barHeight - 9), colorBorder, colorBorder);
		    		RenderUtil.drawGradientRect(0, -10, -10, 12, (barHeight - 10), colorInside, colorInside);
		    		RenderUtil.renderPowerBar(0, -10, 0, 12, (barHeight), dualPower, maxDualPower, DUAL_BAR_COLOR.getRGB(), DUAL_BAR_COLOR.darker().getRGB());
		    		GlStateManager.popMatrix();
	    		}
	    		
	    		if(renderFluid){
	    			String fluidname = fluid !=null ? fluid.getLocalizedName()+": "+ fluid.amount + " / "+ capacity +"MB": Lang.localize("gui.empty");
	    			list.add(fluidname);
	    			int offsetX = 0;
	    			if(renderCU){
	    				offsetX+=16;
	    			}
	    			if(renderRF){
	    				offsetX+=16;
	    			}
		    		GlStateManager.pushMatrix();
		    		GlStateManager.translate(10 + offsetX, sr.getScaledHeight()-(barHeight + 10 + offsetY), 0);	    		
		    		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);	    			    		
		    		int colorBorder = Color.GRAY.darker().getRGB();
		    		int colorInside = Color.GRAY.getRGB();
		    		RenderUtil.drawGradientRect(-1, -11, -11, 13, (barHeight - 9), colorBorder, colorBorder);
		    		RenderUtil.drawGradientRect(0, -10, -10, 12, (barHeight - 10), colorInside, colorInside);
		    		if(fluid !=null){
		    			RenderUtil.renderGuiTank(fluid, capacity, fluid.amount, 0, -10, 0, 12, barHeight);
		    		}
		    		GlStateManager.popMatrix();
	    		}


    			if(!infolines.isEmpty()){
	    			int x = 0;
	    			int listSize = ((infolines.size() * 10)-5);
	    			int y = sr.getScaledHeight() - listSize;
	    	        GlStateManager.pushMatrix();
	    			RenderUtil.drawHoveringText(infolines, x, y, sr.getScaledWidth(), sr.getScaledHeight(), 300, Minecraft.getMinecraft().fontRendererObj);
	    			GlStateManager.disableLighting();		            
		    		GlStateManager.popMatrix();
		    		offsetY+=listSize;
    			}
	    		if(!list.isEmpty()){
	    			int listSize = infolines.isEmpty() ? 0 : ((infolines.size() * 10)+10);
	    			
	    			GlStateManager.pushMatrix();
	    			GlStateManager.translate(10, sr.getScaledHeight()-(barHeight + 10 + (12 * (list.size() - 1))) - listSize, 0);
		    		RenderUtil.drawHoveringText(list, -10, (barHeight + 8), 300, 100, -1, mc.fontRendererObj);
					RenderHelper.enableGUIStandardItemLighting();
		    		GlStateManager.popMatrix();	
	    		}
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
        
        Project.gluPerspective(fov, (float)Minecraft.getMinecraft().displayWidth / (float)Minecraft.getMinecraft().displayHeight, 0.05F, Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16 * 2.0F);
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
            
            int i = CrystalMod.proxy.getClientWorld().getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
            float f = i & 65535;
            float f1 = i >> 16;
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
            double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * event.getPartialTicks() - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * event.getPartialTicks());
            double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * event.getPartialTicks() - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * event.getPartialTicks());
            float fYaw2 = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * event.getPartialTicks();
            double d3 = MathHelper.sin(fYaw2 * (float)Math.PI / 180.0F);
            double d4 = (-MathHelper.cos(fYaw2 * (float)Math.PI / 180.0F));
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
    	EntityPlayer player = CrystalMod.proxy.getClientPlayer();
    	if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.CLIENT && event.side == Side.CLIENT) {
    		elapsedTicks++;
    		if (player != null) {
    			ExtendedPlayer exPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
				if(exPlayer !=null){
					if(exPlayer.getScreenFlashTime() > 0){
						exPlayer.subtractFlashTime();
					}
				}
    		}
        }
		
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
    			EntityPlayer player = event.getPlayer();
    			EnumFacing side = event.getTarget().sideHit;
    			if(tile !=null && tile instanceof TileEntityPipe){
    				TileEntityPipe pipe = (TileEntityPipe)tile;
    				ItemStack stackMain = player.getHeldItemMainhand();
    				if(ItemStackTools.isEmpty(stackMain) || stackMain.getItem() !=ModItems.pipeCover){
    					stackMain = player.getHeldItemOffhand();
    				}
    				if(ItemStackTools.isValid(stackMain) && stackMain.getItem() == ModItems.pipeCover){
    					if(pipe.getCoverData(side) == null){
    						
    						BlockPos pos = hitPos;
    				    	Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    				    	GlStateManager.pushMatrix();
    				    	
    				    	net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
    						GlStateManager.enableAlpha();
    						GlStateManager.enableColorMaterial();
    						GlStateManager.enableBlend();
    						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    						GlStateManager.enableTexture2D();

    						GlStateManager.color(1, 1, 1, 0.5F);
    						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    						GlStateManager.enableBlend();
    						GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    						
    				    	
    						GlStateManager.pushMatrix();
    						{
	    						CoverData data = ItemPipeCover.getCoverData(stackMain);
	    				    	IBlockState state = data.getBlockState();
	    				    	Tessellator tess = Tessellator.getInstance();
	    				    	VertexBuffer buffer = tess.getBuffer();    			
	    				    	buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
	    				    	AxisAlignedBB bounds = CoverUtil.getCoverBoundingBox(side, false);
	    				    	double x = (double)pos.getX() - TileEntityRendererDispatcher.staticPlayerX, y = (double)pos.getY() - TileEntityRendererDispatcher.staticPlayerY, z = (double)pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ;
	    				    	buffer.setTranslation(x-pos.getX(), y-pos.getY(), z-pos.getZ());
	    				    	CoverCutter.ITransformer[] cutType = null;
	    				    	if(pipe.containsExternalConnection(side))cutType = CoverCutter.hollowPipeTile;
	    				    	if(pipe.containsPipeConnection(side))cutType = CoverCutter.hollowPipeLarge;					
	    						CoverRender.renderBakedCoverQuads(buffer, world, pos, state, side.getIndex(), bounds, cutType);
	    				    	buffer.setTranslation(0, 0, 0);
	    				    	tess.draw();  
    						}
    				        GlStateManager.popMatrix();
    				        

    						net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    						GlStateManager.disableAlpha();
    						GlStateManager.disableColorMaterial();
    						GlStateManager.disableLighting();
    						GlStateManager.disableBlend();
    				        
    				        GlStateManager.popMatrix();
    						event.setCanceled(true);
    					}
    				} 
    			}
    			
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
    		ModBlocks.bambooLeaves.setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
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
    @SuppressWarnings("deprecation")
	public void drawBlockDamageTexture(Tessellator tessellatorIn, VertexBuffer vertexBuffer, Entity entityIn, float partialTicks, World world, List<BlockPos> blocks) {
    	double d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
    	double d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
    	double d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;

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
    		blockpos.getX();
    		blockpos.getY();
    		blockpos.getZ();
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
    	//if(sound instanceof ITickableSound)return;
    	AxisAlignedBB bb = new AxisAlignedBB(sound.getXPosF(), sound.getYPosF(), sound.getZPosF(), sound.getXPosF(), sound.getYPosF(), sound.getZPosF()).expand(16, 16, 16);
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
    
    //OVERLAYS
    
    public static final ResourceLocation OVERLAY_REDSTONERADIATION = CrystalMod.resourceL("textures/gui/overlay/redstone_radiation.png");
    public static final ResourceLocation OVERLAY_SUPREME_INTELLECT = CrystalMod.resourceL("textures/gui/overlay/supreme_intellect.png");
    
    @SubscribeEvent
    public void renderScreen(RenderGameOverlayEvent event){
    	EntityPlayer player = CrystalMod.proxy.getClientPlayer();
    	if(player == null)return;
    	
    	ExtendedPlayer extPlayer = ExtendedPlayerProvider.getExtendedPlayer(player);
    	if(extPlayer == null)return;
    	
    	ScaledResolution sr = event.getResolution();
    	if(event.getType() == ElementType.ALL){
    		//Radiation
			if(extPlayer.getRadiation() > 0){				
				int phase = extPlayer.getRadiation() / 600;
				Minecraft.getMinecraft().renderEngine.bindTexture(OVERLAY_REDSTONERADIATION);
				GlStateManager.pushMatrix();
	    		
	    		int left = 0;
	    		int top = 0;
	    		int right = sr.getScaledWidth();
	    		int bottom = sr.getScaledHeight();

	            double uD = 1.0f / 1280;
	            double vD = 1.0f / 8460;
	            
	            double offset = 0;
	            
	            if(phase > 0 && phase <= 7){
	            	offset = 1055 * phase;
	            }
	            
	            double z = 1000;
	            
	    		double u = 0 * uD;
	            double v = offset * vD;
	            double maxU = 1280 * uD;
	            double maxV = (offset + 1080) * vD;
	            Tessellator tessellator = Tessellator.getInstance();
	            VertexBuffer vertexbuffer = tessellator.getBuffer();
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            float alpha = 0.8f;
	            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	            vertexbuffer.pos(left, bottom, z).tex(u, maxV).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(right, bottom, z).tex(maxU, maxV).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(right, top, z).tex(maxU, v).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(left, top, z).tex(u, v).color(1, 1, 1, alpha).endVertex();
	            tessellator.draw();
	            GlStateManager.disableBlend();
	            
	    		GlStateManager.popMatrix();
			}
			//Intellect
			if(extPlayer.getIntellectTime() > 0){				
				Minecraft.getMinecraft().renderEngine.bindTexture(OVERLAY_SUPREME_INTELLECT);
				GlStateManager.pushMatrix();
	    		
	    		int left = 0;
	    		int top = 0;
	    		int right = Minecraft.getMinecraft().displayWidth;
	    		int bottom = Minecraft.getMinecraft().displayHeight;

	            double uD = 1.0f / 1280;
	            double vD = 1.0f / 1080;
	            
	            double z = 1000;
	            
	    		double u = 0 * uD;
	            double v = 0 * vD;
	            double maxU = 1.0D;
	            double maxV = 1.0D;
	            Tessellator tessellator = Tessellator.getInstance();
	            VertexBuffer vertexbuffer = tessellator.getBuffer();
	            GlStateManager.enableBlend();
	            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
	            float alpha = 0.8f;
	            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	            vertexbuffer.pos(left, bottom, z).tex(u, maxV).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(right, bottom, z).tex(maxU, maxV).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(right, top, z).tex(maxU, v).color(1, 1, 1, alpha).endVertex();
	            vertexbuffer.pos(left, top, z).tex(u, v).color(1, 1, 1, alpha).endVertex();
	            tessellator.draw();
	            GlStateManager.disableBlend();
	            
	    		GlStateManager.popMatrix();
	    		
	    		
			}
		}
    	
    	if(extPlayer.getScreenFlashTime() > 0){
    		
    		if(event.getType() == ElementType.HOTBAR){
    			event.setCanceled(true);
    		}
    		
    		GlStateManager.pushMatrix();
    		
    		int left = 0;
    		int top = 0;
    		int right = sr.getScaledWidth();
    		int bottom = sr.getScaledHeight();
    		
    		float f3 = 0.0f;//(((float)extPlayer.getMaxScreenFlashTime() - extPlayer.getScreenFlashTime()) / (float)extPlayer.getMaxScreenFlashTime());
    		
    		if(extPlayer.getScreenFlashTime() > extPlayer.getMaxScreenFlashTime() / 2){
    			f3 = (((float)extPlayer.getMaxScreenFlashTime() - extPlayer.getScreenFlashTime()) / extPlayer.getMaxScreenFlashTime());
    		} else {
    			f3 = 1.0F - (((float)extPlayer.getMaxScreenFlashTime() - extPlayer.getScreenFlashTime()) / extPlayer.getMaxScreenFlashTime());
    		}
    		
            float f = 255;
            float f1 = 255;
            float f2 = 255;
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer vertexbuffer = tessellator.getBuffer();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color(f, f1, f2, f3);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
            vertexbuffer.pos(left, bottom, 0.0D).endVertex();
            vertexbuffer.pos(right, bottom, 0.0D).endVertex();
            vertexbuffer.pos(right, top, 0.0D).endVertex();
            vertexbuffer.pos(left, top, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            
    		GlStateManager.popMatrix();
    	}
    }
    
    @SubscribeEvent
    public void waterOverlay(RenderBlockOverlayEvent event){
    	if(event.getOverlayType() == OverlayType.WATER){
	    	EntityPlayer player = event.getPlayer();
	    	IBlockState state = player.getEntityWorld().getBlockState(event.getBlockPos());
	    	if(state.getBlock() instanceof IFluidBlock){
	    		Fluid fluid = ((IFluidBlock)state.getBlock()).getFluid();
	    		if(fluid !=null){
	    			ResourceLocation res = ModFluids.getOverlayTexture(fluid);
	    			if(res !=null){
	    				event.setCanceled(true);
	    				renderWaterOverlayTexture(event.getRenderPartialTicks(), res);
	    			}
	    		}
	    	}
    	}
    }
    
    private void renderWaterOverlayTexture(float partialTicks, ResourceLocation res)
    {
    	Minecraft.getMinecraft().getTextureManager().bindTexture(res);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        float f = Minecraft.getMinecraft().player.getBrightness(partialTicks);
        GlStateManager.color(f, f, f, 0.5F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        float f7 = -Minecraft.getMinecraft().player.rotationYaw / 64.0F;
        float f8 = Minecraft.getMinecraft().player.rotationPitch / 64.0F;
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex(4.0F + f7, 4.0F + f8).endVertex();
        vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex(0.0F + f7, 4.0F + f8).endVertex();
        vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex(0.0F + f7, 0.0F + f8).endVertex();
        vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex(4.0F + f7, 0.0F + f8).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }
    
    public void collectEntityData(List<String> lines, RayTraceResult ray, Entity entity){
    	String nameLine = entity.getDisplayName().getFormattedText() + " (#"+entity.getEntityId()+")";
		lines.add(nameLine);		
		
    	boolean handledRider = false;
		if(entity instanceof EntityItemFrame){
			EntityItemFrame frame = (EntityItemFrame)entity;
			ItemStack item = frame.getDisplayedItem();
			if(ItemStackTools.isEmpty(item)){
				lines.add("Item: "+Lang.localize("gui.empty"));
			} else {
				lines.add("Item: "+item.getDisplayName());
			}
		}
		
    	if(entity instanceof EntityLivingBase){
			EntityLivingBase living = (EntityLivingBase)entity;
			String health = "Health: "+(living.getHealth()+" / "+ living.getMaxHealth());
			lines.add(health);
		}
    	
    	if(entity instanceof EntityBoat){
    		EntityBoat boat = (EntityBoat)entity;
    		
    		if(boat.getControllingPassenger() !=null){
    			lines.add("Driver: "+boat.getControllingPassenger().getDisplayName().getFormattedText());
    		} else {
    			lines.add("Driver: "+Lang.localize("gui.empty"));
    		}
    		
    		if(!boat.getPassengers().isEmpty() && boat.getPassengers().size() > 1 && boat.getPassengers().get(1) !=null){
    			lines.add("Passenger: "+boat.getPassengers().get(1).getDisplayName().getFormattedText());
    		} else {
    			lines.add("Passenger: "+Lang.localize("gui.empty"));
    		}
    		handledRider = true;
    	}
    	
    	if(entity instanceof EntityMinecart){
    		EntityMinecart cart = (EntityMinecart)entity;

    		if(cart instanceof EntityMinecartEmpty){
	    		if(cart.getControllingPassenger() !=null){
	    			lines.add("Rider: "+cart.getControllingPassenger().getDisplayName().getFormattedText());
	    		} else {
	    			lines.add("Rider: "+Lang.localize("gui.empty"));
	    		}
    		}
    		if(cart instanceof EntityMinecartFurnace){
    			EntityMinecartFurnace furnace = (EntityMinecartFurnace)cart;
    			int fuel = ObfuscationReflectionHelper.getPrivateValue(EntityMinecartFurnace.class, furnace, 1);
	    		lines.add("Fuel: "+TimeUtil.getTimeFromTicks(fuel));
    		}
    		handledRider = true;
    	}
    	
    	if(entity instanceof IEntityOwnable){
    		IEntityOwnable ownable = (IEntityOwnable)entity;
    		UUID owner = ownable.getOwnerId();
    		if(owner !=null){
    			String name = ProfileUtil.getUsername(owner);
    			if(name != ProfileUtil.ERROR){
    				lines.add("Owner: "+name);
    			}
    		}
    	}
    	
    	if(!handledRider && entity.getControllingPassenger() !=null){
    		lines.add("Rider: "+entity.getControllingPassenger().getDisplayName().getFormattedText());
    	}
    }
}
