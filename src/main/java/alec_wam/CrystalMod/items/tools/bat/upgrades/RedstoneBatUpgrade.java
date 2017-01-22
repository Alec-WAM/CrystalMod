package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class RedstoneBatUpgrade extends BatUpgrade {

	public RedstoneBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("redstone"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {
		if(player.getEntityWorld().isRemote)return;
		if(hand !=null && hand == EnumHand.MAIN_HAND){
			int lvl = (int)getValue(data);
			if(lvl > 0 && player.isSprinting()){
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 10, (int) (0.5D*lvl), false, false)); 
			}
		}
	}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData data, UpgradeData value) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData data) {
		int lvl = (int)getValue(data);
		if(lvl > 0 && damage > 0.0f){
			for(EntityLivingBase living : entities){
       	 		living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, lvl * 10, 5)); 
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.RED;
		String itemName = REDSTONE.getDisplayName();
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
		double min = 0.4;
		double max = 0.6;
		double minY = 0.35;
		double maxY = 0.85;
		
		TextureAtlasSprite redstone = RenderUtil.getTexture(Blocks.REDSTONE_BLOCK.getDefaultState());
		
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		
		RenderUtil.startDrawing(buffer);
		RenderUtil.addVertexWithUV(buffer, min, minY, min, redstone.getMinU(), redstone.getMinV());
		RenderUtil.addVertexWithUV(buffer, min, maxY, min, redstone.getMinU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, maxY, min, redstone.getMaxU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, minY, min, redstone.getMaxU(), redstone.getMinV());
		
		RenderUtil.addVertexWithUV(buffer, max, minY, max, redstone.getMinU(), redstone.getMinV());
		RenderUtil.addVertexWithUV(buffer, max, maxY, max, redstone.getMinU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, maxY, max, redstone.getMaxU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, minY, max, redstone.getMaxU(), redstone.getMinV());
		tess.draw();
		
		RenderUtil.startDrawing(buffer);
		RenderUtil.addVertexWithUV(buffer, max, minY, min, redstone.getMinU(), redstone.getMinV());
		RenderUtil.addVertexWithUV(buffer, max, maxY, min, redstone.getMinU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, maxY, max, redstone.getMaxU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, minY, max, redstone.getMaxU(), redstone.getMinV());
		
		RenderUtil.addVertexWithUV(buffer, min, minY, max, redstone.getMinU(), redstone.getMinV());
		RenderUtil.addVertexWithUV(buffer, min, maxY, max, redstone.getMinU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, maxY, min, redstone.getMaxU(), redstone.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, minY, min, redstone.getMaxU(), redstone.getMinV());
		tess.draw();
	}

    public final ItemStack REDSTONE = new ItemStack(Items.REDSTONE);
    public final ItemStack REDSTONE_BLOCK = new ItemStack(Blocks.REDSTONE_BLOCK);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, REDSTONE)){
			return 1;
		}
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, REDSTONE_BLOCK)){
			return 9;
		}
		return 0;
	}

}
