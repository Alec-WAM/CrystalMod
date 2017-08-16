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

public class WoodBatType extends BatType {

	public WoodBatType() {
		super(new ResourceLocation("minecraft:wood"), ToolMaterial.WOOD);
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getBatTexture() {
		return RenderUtil.getTexture(Blocks.PLANKS.getStateFromMeta(0));
	}

	@Override
	public void addCraftingRecipe() {
		ModCrafting.addShapedOreRecipe(BatHelper.getBasicBat(ModItems.bat, this), new Object[] {" X ", " X ", "LSL", 'X', "plankWood", 'S', "stickWood", 'L', "leather"});
	}

}
