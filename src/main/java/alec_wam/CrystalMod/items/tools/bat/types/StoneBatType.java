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

public class StoneBatType extends BatType {

	public StoneBatType() {
		super(new ResourceLocation("minecraft:stone"), ToolMaterial.STONE);
	}

	@Override
	public TextureAtlasSprite getBatTexture() {
		return RenderUtil.getTexture(Blocks.STONE.getDefaultState());
	}

	@Override
	public void addCraftingRecipe() {
		ModCrafting.addShapedOreRecipe(BatHelper.getBasicBat(ModItems.bat, this), new Object[] {" X ", " X ", "LSL", 'X', "stone", 'S', "stickWood", 'L', "leather"});
	}

}

