package alec_wam.CrystalMod.api.tools;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBatUpgrade {

	public ResourceLocation getID();
	
	public int getItemsPerLevel();
	
	public int getMaxLevel();
	
	public float getValue(UpgradeData data);
	
	public UpgradeData getCreativeListData();
	
	@SideOnly(Side.CLIENT)
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType);
	
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand);

	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData);

	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entites, float damage, ItemStack stack, AttackData attackData, UpgradeData upgradeData);
	
	public boolean blocksDamage(ItemStack stack, UpgradeData value);

	public void render(ItemStack bat, UpgradeData data);
	
	public int getUpgradeValue(ItemStack stack);
	
	public UpgradeData handleUpgrade(ItemStack bat, ItemStack[] ingred);

	public boolean canBeAdded(ItemStack bat, List<IBatUpgrade> upgradeList, UpgradeData dataToBeAdded);

	public void afterUpgradeAdded(ItemStack bat, ItemStack[] items, UpgradeData data);
	
}
