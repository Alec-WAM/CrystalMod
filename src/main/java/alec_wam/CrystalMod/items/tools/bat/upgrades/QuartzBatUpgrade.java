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
import alec_wam.CrystalMod.items.tools.bat.ItemBatRenderer;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.Lang;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class QuartzBatUpgrade extends BatUpgrade {

	public QuartzBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("quartz"), IPL, MIPL, true);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {
		attackData.earlyAttackDamage+=getValue(upgradeData);
	}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.WHITE;
		String itemName = QUARTZ.getDisplayName();
		//Shift
		if(infoType == 0){
			list.add(formatting+(BatHelper.localizeName(this))+": "+getLevelInfo(data)+" ("+ (getValue(data) * 0.5f) +"h)");
		}
		//Ctrl
		if(infoType == 1){
			list.add(formatting+(itemName)+": "+getBasicLevelInfo(amount)+TextFormatting.RESET);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void render(ItemStack bat, UpgradeData data) {
		double min = 0.39;
		double max = 0.61;
		double minTop = 0.35;
		double maxTop = 0.65;
		double minY = 0.85;
		double maxY = 2.5;
		
		TextureAtlasSprite quartz = RenderUtil.getTexture(Blocks.QUARTZ_BLOCK.getDefaultState());
		
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		ItemBatRenderer.startDrawing(buffer);
		ItemBatRenderer.addVertexWithUV(buffer, min-0.03, minY+0.9, min-0.03, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, minTop, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, minTop, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, max+0.03, minY+0.9, min-0.03, quartz.getMaxU(), quartz.getMinV());
		
		ItemBatRenderer.addVertexWithUV(buffer, max+0.03, minY+0.9, max+0.03, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, maxTop, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, maxTop, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, min-0.03, minY+0.9, max+0.03, quartz.getMaxU(), quartz.getMinV());
		tess.draw();
		
		ItemBatRenderer.startDrawing(buffer);
		ItemBatRenderer.addVertexWithUV(buffer, max+0.03, minY+0.9, min-0.03, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, minTop, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, maxTop, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, max+0.03, minY+0.9, max+0.03, quartz.getMaxU(), quartz.getMinV());
		
		ItemBatRenderer.addVertexWithUV(buffer, min-0.03, minY+0.9, max+0.03, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, maxTop, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, minTop, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, min-0.03, minY+0.9, min-0.03, quartz.getMaxU(), quartz.getMinV());
		tess.draw();
		
		ItemBatRenderer.startDrawing(buffer);
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, maxTop, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, maxTop, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop, maxY, minTop, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop, maxY, minTop, quartz.getMaxU(), quartz.getMinV());
		tess.draw();
		
		double spikeOff = 0.0001;
		
		ItemBatRenderer.startDrawing(buffer);
		ItemBatRenderer.addVertexWithUV(buffer, min-spikeOff, maxY, min-spikeOff, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop+((max-min)/2)-spikeOff, maxY+0.3, 0.5, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop-((max-min)/2)+spikeOff, maxY+0.3, 0.5, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, max+spikeOff, maxY, min-spikeOff, quartz.getMaxU(), quartz.getMinV());
		
		ItemBatRenderer.addVertexWithUV(buffer, max+spikeOff, maxY, max+spikeOff, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, maxTop-((max-min)/2)+spikeOff, maxY+0.3, 0.5, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, minTop+((max-min)/2)-spikeOff, maxY+0.3, 0.5, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, min-spikeOff, maxY, max+spikeOff, quartz.getMaxU(), quartz.getMinV());
		tess.draw();
		
		ItemBatRenderer.startDrawing(buffer);
		ItemBatRenderer.addVertexWithUV(buffer, max+spikeOff, maxY, min-spikeOff, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, 0.5, maxY+0.3, minTop+((max-min)/2)-spikeOff, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, 0.5, maxY+0.3, maxTop-((max-min)/2)+spikeOff, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, max+spikeOff, maxY, max+spikeOff, quartz.getMaxU(), quartz.getMinV());
		
		ItemBatRenderer.addVertexWithUV(buffer, min-spikeOff, maxY, max+spikeOff, quartz.getMinU(), quartz.getMinV());
		ItemBatRenderer.addVertexWithUV(buffer, 0.5, maxY+0.3, maxTop-((max-min)/2)+spikeOff, quartz.getMinU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, 0.5, maxY+0.3, minTop+((max-min)/2)-spikeOff, quartz.getMaxU(), quartz.getMaxV());
		ItemBatRenderer.addVertexWithUV(buffer, min-spikeOff, maxY, min-spikeOff, quartz.getMaxU(), quartz.getMinV());
		tess.draw();
	}

    public final ItemStack QUARTZ = new ItemStack(Items.QUARTZ);
    public final ItemStack QUARTZ_BLOCK = new ItemStack(Blocks.QUARTZ_BLOCK);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, QUARTZ)){
			return 1;
		}
		if(stack !=null && ItemUtil.stackMatchUseOre(stack, QUARTZ_BLOCK)){
			return 4;
		}
		return 0;
	}
	
}
