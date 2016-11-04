package alec_wam.CrystalMod.api.tools;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IBatType {

	public ResourceLocation getID();
	
	public int getMaxDamage();
	
	public float getBaseDamage();
	
	public void addCraftingRecipe();

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getHandleTexture();

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getBatTexture();
	
}
