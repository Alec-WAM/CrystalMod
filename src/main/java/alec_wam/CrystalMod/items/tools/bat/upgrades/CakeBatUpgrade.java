package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.blocks.ModBlocks;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CakeBatUpgrade extends BatUpgrade {

	public CakeBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("cake"), IPL, MIPL, true);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		attackData.rangeBoost+=getValue(upgradeData);
	}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entites, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.BOLD;
		String itemName = CAKE.getDisplayName();
		//Allways
		if(infoType == -1){
			list.add(formatting+(BatHelper.localizeName(this))+" "+BatHelper.getRomanString(data));
		}
		//Shift
		if(infoType == 0){
			list.add(formatting+(BatHelper.localizeName(this))+": "+getLevelInfo(data)+TextFormatting.RESET);
		}
		//Ctrl
		if(infoType == 1){
			list.add(formatting+(itemName)+": "+getBasicLevelInfo(amount)+TextFormatting.RESET);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(ItemStack bat, UpgradeData data) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.2, 1, 0.8);
		GlStateManager.scale(0.6, 0.6, 0.6);
		Minecraft.getMinecraft().getBlockRendererDispatcher().renderBlockBrightness(Blocks.CAKE.getDefaultState(), 1.0F);
		GlStateManager.popMatrix();
	}

	public static final ItemStack CAKE = new ItemStack(Items.CAKE);
	
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.canCombine(stack, CAKE)){
			return 1;
		}
		return 0;
	}

}
