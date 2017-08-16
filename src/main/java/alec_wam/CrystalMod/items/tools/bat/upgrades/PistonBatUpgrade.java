package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PistonBatUpgrade extends BatUpgrade {

	public PistonBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("piston"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		attackData.knockback+=getValue(upgradeData);
	}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.DARK_GRAY;
		String itemName = PISTON.getDisplayName();
		//Shift
		if(infoType == 0){
			list.add(formatting+(BatHelper.localizeName(this))+" "+BatHelper.getRomanString(data)+": "+getLevelInfo(data)+TextFormatting.RESET);
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
		GlStateManager.translate(0.5, 1.9, 0.5);
		GlStateManager.scale(1.8, 1.8, 1.8);
		GlStateManager.rotate(45, 0, 1, 0);
		GlStateManager.rotate(-90, 0, 0, 1);
		GlStateManager.translate(0, -0.2, 0.0);
		RenderUtil.renderItem(PISTON, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	public final ItemStack PISTON = new ItemStack(Blocks.PISTON);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, PISTON)){
			return 1;
		}
		return 0;
	}
}
