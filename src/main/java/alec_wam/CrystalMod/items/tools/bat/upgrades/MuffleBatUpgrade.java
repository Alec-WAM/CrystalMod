package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class MuffleBatUpgrade extends BatUpgrade {

	public MuffleBatUpgrade() {
		super(CrystalMod.resourceL("muffler"), 1, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.WHITE;
		String itemName = WOOL.getDisplayName();
		//Allways
		if(infoType == -1){
			list.add(formatting+(BatHelper.localizeName(this))+TextFormatting.RESET);
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
		GlStateManager.translate(0.5, 0.1, 0.501);
		GlStateManager.scale(1.21, 1.21, 1.21);
		RenderUtil.renderItem(WOOL, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity,	ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		attackData.cancelDamage = true;
	}

	@Override
	public void afterAttack(EntityLivingBase attacker,	List<EntityLivingBase> entites, float damage, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	public static final ItemStack WOOL = new ItemStack(Blocks.WOOL, 1, EnumDyeColor.WHITE.getMetadata());
	
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, WOOL)){
			return 1;
		}
		return 0;
	}

}
