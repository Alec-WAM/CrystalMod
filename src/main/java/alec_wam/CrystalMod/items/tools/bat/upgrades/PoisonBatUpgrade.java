package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;
import java.util.Set;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.IBatUpgrade;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class PoisonBatUpgrade extends BatUpgrade {

	public PoisonBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("poison"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		int lvl = (int)getValue(upgradeData);
		if(lvl > 0 && damage > 0.0f){
			for(EntityLivingBase living : entities){
	       	 	living.addPotionEffect(new PotionEffect(MobEffects.POISON, lvl * 12, 5)); 
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.GREEN;
		String itemName = SPIDER_EYE.getDisplayName();
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
		ItemStack spiderEye = SPIDER_EYE;
		double height = 2.29;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.51, height, 0.35);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(spiderEye, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.49, height, 0.65);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(spiderEye, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.65, height, 0.51);
		GlStateManager.rotate(90*3, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(spiderEye, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.35, height, 0.49);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(spiderEye, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

    public final ItemStack SPIDER_EYE = new ItemStack(Items.SPIDER_EYE);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, SPIDER_EYE)){
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean canBeAdded(ItemStack bat, List<IBatUpgrade> upgrades, UpgradeData dataToBeAdded){
		IBatUpgrade upgrade = BatHelper.getUpgrade(CrystalMod.resourceL("ender"));
		return upgrade == null ? true : !upgrades.contains(upgrade);
	}

}
