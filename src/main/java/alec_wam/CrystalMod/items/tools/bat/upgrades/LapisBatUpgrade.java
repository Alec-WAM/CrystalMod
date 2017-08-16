package alec_wam.CrystalMod.items.tools.bat.upgrades;

import java.util.List;

import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.api.tools.AttackData;
import alec_wam.CrystalMod.api.tools.UpgradeData;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatUpgrade;
import alec_wam.CrystalMod.util.ItemStackTools;
import alec_wam.CrystalMod.util.ItemUtil;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LapisBatUpgrade extends BatUpgrade {

	public LapisBatUpgrade(int IPL, int MIPL) {
		super(CrystalMod.resourceL("lapis"), IPL, MIPL);
	}

	@Override
	public void update(EntityPlayer player, ItemStack bat, UpgradeData data, int heldSlot, EnumHand hand) {}

	@Override
	public void addAttackData(EntityLivingBase attacker, Entity entity, ItemStack stack, AttackData attackData, UpgradeData upgradeData) {}

	@Override
	public void afterAttack(EntityLivingBase attacker, List<EntityLivingBase> entities, float damage, ItemStack stack, AttackData attackData, UpgradeData value) {}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInfo(List<String> list, EntityPlayer player, ItemStack bat, UpgradeData data, boolean detailed, int infoType) {
		int amount = data.getAmount();
		String formatting = ""+TextFormatting.DARK_BLUE;
		String itemName = LAPIS.getDisplayName();
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
		double min = 0.39;
		double max = 0.61;
		double minTop = 0.35;
		double maxTop = 0.65;
		double minY = 0.85;
		double maxY = 2.5;
		TextureAtlasSprite lapis = RenderUtil.getTexture(Blocks.LAPIS_BLOCK.getDefaultState());
		Tessellator tess = Tessellator.getInstance();
		VertexBuffer buffer = tess.getBuffer();
		
		RenderUtil.startDrawing(buffer);
		RenderUtil.addVertexWithUV(buffer, min, minY, min, lapis.getMinU(), lapis.getMinV());
		RenderUtil.addVertexWithUV(buffer, minTop, maxY-(maxY/3), minTop+(0.02), lapis.getMinU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, maxTop, maxY-(maxY/3), minTop+(0.02), lapis.getMaxU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, minY, min, lapis.getMaxU(), lapis.getMinV());
		
		RenderUtil.addVertexWithUV(buffer, max, minY, max, lapis.getMinU(), lapis.getMinV());
		RenderUtil.addVertexWithUV(buffer, maxTop, maxY-(maxY/3), maxTop-(0.02), lapis.getMinU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, minTop, maxY-(maxY/3), maxTop-(0.02), lapis.getMaxU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, minY, max, lapis.getMaxU(), lapis.getMinV());
		tess.draw();
			
		RenderUtil.startDrawing(buffer);
		RenderUtil.addVertexWithUV(buffer, max, minY, min, lapis.getMinU(), lapis.getMinV());
		RenderUtil.addVertexWithUV(buffer, maxTop, maxY-(maxY/3), minTop+(0.02), lapis.getMinU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, maxTop, maxY-(maxY/3), maxTop-(0.02), lapis.getMaxU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, max, minY, max, lapis.getMaxU(), lapis.getMinV());
		
		RenderUtil.addVertexWithUV(buffer, min, minY, max, lapis.getMinU(), lapis.getMinV());
		RenderUtil.addVertexWithUV(buffer, minTop, maxY-(maxY/3), maxTop-(0.02), lapis.getMinU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, minTop, maxY-(maxY/3), minTop+(0.02), lapis.getMaxU(), lapis.getMaxV());
		RenderUtil.addVertexWithUV(buffer, min, minY, min, lapis.getMaxU(), lapis.getMinV());
		tess.draw();
	}

	public final ItemStack LAPIS = new ItemStack(Items.DYE, 1, EnumDyeColor.BLUE.getDyeDamage());
    public final ItemStack LAPIS_BLOCK = new ItemStack(Blocks.LAPIS_BLOCK);
    
	//Crafting
	@Override
	public int getUpgradeValue(ItemStack stack) {
		if(ItemStackTools.isValid(stack) && ItemUtil.isOreMatch(stack, LAPIS)){
			return 1;
		}
		if(ItemStackTools.isValid(stack) && stack.getItem() == Item.getItemFromBlock(Blocks.LAPIS_BLOCK)){
			return 1;
		}
		return 0;
	}
}
