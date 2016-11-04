package alec_wam.CrystalMod.items.tools.bat;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alec_wam.CrystalMod.api.tools.IBatType;
import alec_wam.CrystalMod.util.client.RenderUtil;

public abstract class BatType implements IBatType {

	private ResourceLocation id;
	private float damage;
	private int durability;
	
	public BatType(ResourceLocation id, ToolMaterial material){
		this(id, 4.0F + material.getDamageVsEntity(), material.getMaxUses());
	}
	
	public BatType(ResourceLocation id, float damage, int durability){
		this.id = id;
		this.damage = damage;
		this.durability = durability;
	}
	
	@Override
	public ResourceLocation getID() {
		return id;
	}

	@Override
	public int getMaxDamage() {
		return durability;
	}

	@Override
	public float getBaseDamage() {
		return damage;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public TextureAtlasSprite getHandleTexture() {
		return RenderUtil.getTexture(Blocks.LOG.getDefaultState());
	}

}
