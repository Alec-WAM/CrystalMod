package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class FireworkBatUpgrade extends BatUpgrade {

	public static final String NBT_FIREWORK = "FireworkData";
	
	public FireworkBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("firework"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {
		NBTTagCompound batData = BatHelper.getBatData(stack);
		if(batData.hasKey(NBT_FIREWORK)){
			ItemStack fireW = new ItemStack(Items.FIREWORKS);
    		fireW.setTagCompound(batData.getCompoundTag(NBT_FIREWORK));
    		for(Entity entity : entities){
	    		EntityFireworkRocket rocket = new EntityFireworkRocket(attacker.getEntityWorld(), (entity.posX), (entity.posY)+entity.height/2, (entity.posZ), fireW);
	    		
	    		if(!attacker.getEntityWorld().isRemote){
	    			attacker.getEntityWorld().spawnEntity(rocket);
	    			attacker.getEntityWorld().setEntityState(rocket, (byte)17);
	    			rocket.setDead();
	    		}
    		}
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.BOLD+""+TextFormatting.RED;
		String itemName = FIREWORK.getDisplayName();
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
		ItemStack firework = FIREWORK;
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.51, 1.3, 0.35);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(firework, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.49, 1.3, 0.65);
		GlStateManager.rotate(180, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(firework, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.65, 1.3, 0.51);
		GlStateManager.rotate(90*3, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(firework, TransformType.GROUND);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.35, 1.3, 0.49);
		GlStateManager.rotate(90, 0, 1, 0);
		GlStateManager.scale(0.6, 0.6, 0.6);
		GlStateManager.rotate(180, 0, 1, 0);
		RenderUtil.renderItem(firework, TransformType.GROUND);
		GlStateManager.popMatrix();
	}

    public final ItemStack FIREWORK = new ItemStack(Items.FIREWORKS);
    
    @Override
    public UpgradeData getCreativeListData(){
    	return null;
    }
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && stack.getItem() == FIREWORK.getItem() && stack.hasTagCompound()){
			return 1;
		}
		return 0;
	}
	
	@Override
	public void afterUpgradeAdded(ItemStack bat, ItemStack[] items,	UpgradeData data) {
		NBTTagCompound fireworkNBT = null;
		for(ItemStack stack : items){
			if(stack !=null && stack.getItem() == Items.FIREWORKS && stack.hasTagCompound()){
				fireworkNBT = stack.getTagCompound();
				break;
			}
		}
		if(fireworkNBT !=null){
			BatHelper.getBatData(bat).setTag(NBT_FIREWORK, fireworkNBT);
		}
	}

}
