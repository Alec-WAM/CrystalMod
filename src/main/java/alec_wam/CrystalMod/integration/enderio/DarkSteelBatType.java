package alec_wam.CrystalMod.integration.enderio;

import alec_wam.CrystalMod.crafting.ModCrafting;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.items.tools.bat.BatHelper;
import alec_wam.CrystalMod.items.tools.bat.BatType;
import alec_wam.CrystalMod.util.client.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

public class DarkSteelBatType extends BatType {

	public DarkSteelBatType() {
		super(new ResourceLocation("EnderIO:darksteel"), 4.0F + 7.0F, 2000);
	}

	@SuppressWarnings("deprecation")
	@Override
	public TextureAtlasSprite getBatTexture() {
		Block block = net.minecraft.block.Block.getBlockFromName("EnderIO:blockIngotStorage");
		return RenderUtil.getTexture(block.getStateFromMeta(6));
	}

	@Override
	public void addCraftingRecipe() {
		ModCrafting.addShapedOreRecipe(BatHelper.getBasicBat(ModItems.bat, this), new Object[] {"X", "X", "S", 'X', "ingotDarkSteel", 'S', "stickWood"});
	}

}
