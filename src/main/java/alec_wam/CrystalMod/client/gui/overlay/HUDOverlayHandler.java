package alec_wam.CrystalMod.client.gui.overlay;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.gui.overlay.IOvelayTile.InfoProvider;
import alec_wam.CrystalMod.client.gui.overlay.ProjectileArcHelper.ProjectileArcData;
import alec_wam.CrystalMod.client.gui.overlay.ProjectileArcHelper.TridentProjectileArc;
import alec_wam.CrystalMod.init.FixedFluidRegistry;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.RenderUtil;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CrystalMod.MODID, value = Dist.CLIENT, bus = Bus.MOD)
public class HUDOverlayHandler {

	@SubscribeEvent
    public void hudOverlay(RenderGameOverlayEvent.Post event){
		//System.out.println("ALL");
		Minecraft mc = Minecraft.getInstance();
		MainWindow window = mc.mainWindow;
		ClientWorld world = mc.world;
    	ClientPlayerEntity player = mc.player;
    	if(event.getType() == ElementType.ALL && mc.currentScreen == null){
    		if(mc.objectMouseOver != null){
    			RayTraceResult ray = mc.objectMouseOver;
    			if(ray.getType() == RayTraceResult.Type.BLOCK){
    				BlockRayTraceResult blockRay = (BlockRayTraceResult)ray;
    				BlockPos pos = blockRay.getPos();
    				TileEntity tile = world.getTileEntity(pos);
    				if(tile !=null && tile instanceof IOvelayTile){
    					IOvelayTile overlay = (IOvelayTile) tile;
    					InfoProvider info = overlay.getInfo();
    					mc.getProfiler().startSection("crystalmod-hud-tile");
    					if(info !=null)info.render(world, tile, pos, blockRay.getFace());
    					mc.getProfiler().endSection();
    				}
    			}
    		}
    	}
	}
	
	public static int getOverlayX(){
		return 10;
	}
	
	public static int getOverlayY(){
		MainWindow window = Minecraft.getInstance().mainWindow;
		return window.getScaledHeight() - 5;
	}
	
	private ProjectileArcData currentBowArc;
	private final Random bowArcRand = new Random();
	
	@SubscribeEvent
	public void worldRender(RenderWorldLastEvent event){
		Minecraft.getInstance().getProfiler().startSection("crystalmod-hud-bowarc");
		ClientPlayerEntity player = Minecraft.getInstance().player;
		ItemStack activeheld = player.getActiveItemStack();
		ItemStack heldMain = player.getHeldItemMainhand();
		ItemStack heldOff = player.getHeldItemOffhand();
		Hand itemHand = Hand.MAIN_HAND;
		//0 = Bow, 1 = Trident, 2 = Crossbow
		int arcType = 0;
		boolean active = false;
		if(activeheld.getItem() instanceof BowItem){
			int pull = (activeheld.getUseDuration() - player.getItemInUseCount());
			if(pull > 0){
				active = true;
				itemHand = player.getActiveHand();
				float velocity = BowItem.getArrowVelocity(pull) * 3.0F;
				currentBowArc = ProjectileArcData.shoot(player, player.rotationPitch, player.rotationYaw, velocity, 0.0f, bowArcRand);
			}
		}
		else if(heldMain.getItem() instanceof TridentItem){
			arcType = 1;
			if(ItemUtil.canCombine(activeheld, heldMain)){
				active = true;
			}
			int rip = EnchantmentHelper.getRiptideModifier(heldMain);
			currentBowArc = TridentProjectileArc.shoot(player, player.rotationPitch, player.rotationYaw, 2.5F + (float)rip * 0.5F, 0.0f, bowArcRand);
		}
		else if(heldOff.getItem() instanceof TridentItem){
			arcType = 1;
			itemHand = Hand.OFF_HAND;
			if(ItemUtil.canCombine(activeheld, heldOff)){
				active = true;
			}
			int rip = EnchantmentHelper.getRiptideModifier(heldOff);
			currentBowArc = TridentProjectileArc.shoot(player, player.rotationPitch, player.rotationYaw, 2.5F + (float)rip * 0.5F, 0.0f, bowArcRand);
		}
		else if(heldMain.getItem() instanceof CrossbowItem){
			boolean charged = CrossbowItem.isCharged(heldMain);
			if(charged){
				arcType = 2;
				float power = ProjectileArcHelper.isCrossbowLoadedWith(heldMain, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
				//TODO Handle triple-shot
				currentBowArc = ProjectileArcData.shoot(player, player.rotationPitch, player.rotationYaw, power, 0.0f, bowArcRand);
			} else {
				currentBowArc = null;
			}
		}
		else if(heldOff.getItem() instanceof CrossbowItem){
			boolean charged = CrossbowItem.isCharged(heldOff);
			if(charged){
				arcType = 2;
				itemHand = Hand.OFF_HAND;
				float power = ProjectileArcHelper.isCrossbowLoadedWith(heldOff, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
				//TODO Handle triple-shot
				currentBowArc = ProjectileArcData.shoot(player, player.rotationPitch, player.rotationYaw, power, 0.0f, bowArcRand);
			} else {
				currentBowArc = null;
			}
		} else {
			currentBowArc = null;
		}
		
		if(currentBowArc !=null){
			GlStateManager.pushMatrix();
			double offsetX = 0;
			double offsetY = 0;
			double offsetZ = 0;
			GlStateManager.translated(offsetX-TileEntityRendererDispatcher.staticPlayerX, offsetY-TileEntityRendererDispatcher.staticPlayerY, offsetZ-TileEntityRendererDispatcher.staticPlayerZ);
			
			Vec3d finalHit = currentBowArc.finalHit;
			if(finalHit !=null){
				int i = player.getPrimaryHand() == HandSide.RIGHT ? 1 : -1;
				float partialTicks = event.getPartialTicks();
				float f3 = player.getSwingProgress(partialTicks);
				float f4 = MathHelper.sin(MathHelper.sqrt(f3) * (float)Math.PI);
				double d4 = 0;
				double d5 = 0;
				double d6 = 0;
				double d8 = 70/*this.field_76990_c.options.fovSetting*/;
				d8 = d8 / 100.0D;
				
				Vec3d startVec = null;
				if(arcType == 0){
					if(itemHand == Hand.OFF_HAND){
						i = -1;
					}
					startVec =  new Vec3d((double)i * -0.36D * d8, -0.045D * d8, 0.4D);
				}
				if(arcType == 1){
					if(itemHand == Hand.OFF_HAND){
						i = -1;
					}
					if(active){
						startVec =  new Vec3d((double)i * -0.36D * d8, 0.25 * d8, 0.4D);
					} else {
						startVec =  new Vec3d((double)i * -0.36D * d8, 0.045D * d8, 0.2D);
					}
				}
				if(arcType == 2){
					startVec = new Vec3d(0.0d, -0.045D * 2 * d8, 0.2D);
				}
				
				if(startVec !=null){
					Vec3d vec3d = startVec;
					vec3d = vec3d.rotatePitch(-MathHelper.lerp(partialTicks, player.prevRotationPitch, player.rotationPitch) * ((float)Math.PI / 180F));
					vec3d = vec3d.rotateYaw(-MathHelper.lerp(partialTicks, player.prevRotationYaw, player.rotationYaw) * ((float)Math.PI / 180F));
					vec3d = vec3d.rotateYaw(f4 * 0.5F);
					vec3d = vec3d.rotatePitch(-f4 * 0.7F);
					d4 = vec3d.x;
					d5 = vec3d.y;
					d6 = vec3d.z;
				}

				GlStateManager.pushMatrix();
				GlStateManager.disableCull();
				GlStateManager.disableTexture();
				GlStateManager.color3f(0, 0, 0);
				GL11.glBegin(GL11.GL_LINE_STRIP);
				int j = currentBowArc.arcPoints.size();
				for(int k = 0; k < j; ++k) {
					Vec3d lineVec = currentBowArc.arcPoints.get(k);
					double lineX = lineVec.x + d4;
					double lineY = lineVec.y + d5 + 0.05;
					double lineZ = lineVec.z + d6;
					GL11.glVertex3d(lineX, lineY, lineZ);
				}
				GL11.glEnd();
				GlStateManager.enableCull();
				GlStateManager.enableTexture();
				GlStateManager.popMatrix();
				
				
				if(currentBowArc.finalHitType == RayTraceResult.Type.BLOCK){
					double blockOffset = 0.5;
					RenderUtil.renderFluidCuboid(FixedFluidRegistry.LAVA, finalHit.x - blockOffset, finalHit.y - blockOffset, finalHit.z - blockOffset, 0, 0, 0, 1, 1, 1, false, 15);
				} else {
					RenderUtil.renderFluidCuboid(FixedFluidRegistry.LAVA, finalHit.x - 0.5, finalHit.y - 0.5, finalHit.z - 0.5, 0.2, 0.2, 0.2, 0.8, 0.8, 0.8, false, 15);
				}
			}
			GlStateManager.popMatrix();
		}
		Minecraft.getInstance().getProfiler().endSection();
	}
	
}
