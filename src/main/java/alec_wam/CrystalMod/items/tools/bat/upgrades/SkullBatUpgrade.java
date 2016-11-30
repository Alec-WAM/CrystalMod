package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.ModLogger;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class SkullBatUpgrade extends BatUpgrade {

	public SkullBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("skull"), IPL, MIPL, true);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		EntityLivingBase entity = entities.get(0);
		if (entity !=null && (entity instanceof IMob))
        {
			float drain = getValue(upgradeData);
			
            if (drain > 0 && damage > 0.0f){
            	float heal = Math.min(drain, damage);
            	attacker.heal(heal);
            }
        }
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.BLACK;
		String itemName = SKULL.getDisplayName();
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
		GlStateManager.translate(0.5, 0.2, 0.501);
		GlStateManager.scale(1.21, 1.21, 1.21);
		RenderUtil.renderItem(SKULL, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

    public final ItemStack SKULL = new ItemStack(Items.SKULL, 1, 1);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(!ItemStackTools.isNullStack(stack) && ItemUtil.canCombine(stack, SKULL)){
			return 1;
		}
		return 0;
	}

}
