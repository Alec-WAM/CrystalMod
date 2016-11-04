package alec_wam.CrystalMod.items.tools.bat.types;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatType;
import alec_wam.CrystalMod.util.client.RenderUtil;

public class IronBatType extends BatType {

	public IronBatType() {
		super(new ResourceLocation("minecraft:iron"), ToolMaterial.IRON);
	}

	@Override
	public TextureAtlasSprite getBatTexture() {
		return RenderUtil.getTexture(Blocks.IRON_BLOCK.getDefaultState());
	}

	@Override
	public void addCraftingRecipe() {
		ModCrafting.addShapedOreRecipe(BatHelper.getBasicBat(ModItems.bat, this), new Object[] {"X", "X", "S", 'X', "ingotIron", 'S', "stickWood"});
	}

}
