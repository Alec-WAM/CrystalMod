package alec_wam.CrystalMod.items.tools.bat.types;

import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatType;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;

public class GoldBatType extends BatType {

	public GoldBatType() {
		super(new ResourceLocation("minecraft:gold"), ToolMaterial.GOLD);
	}

	@Override
	public TextureAtlasSprite getBatTexture() {
		return RenderUtil.getTexture(Blocks.GOLD_BLOCK.getDefaultState());
	}

	@Override
	public void addCraftingRecipe() {
		ModCrafting.addShapedOreRecipe(BatHelper.getBasicBat(ModItems.bat, this), new Object[] {" X ", " X ", "LSL", 'X', "ingotGold", 'S', "stickWood", 'L', "leather"});
	}

}
