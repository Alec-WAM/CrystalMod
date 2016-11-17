package alec_wam.CrystalMod.entities.pet.bombomb;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.model.TRSRTransformation;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.DynamicBaseModel;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;

public class ItemRenderBombomb implements ICustomItemRenderer {
    
    private EntityBombomb renderBombomb;
    
    public EntityBombomb getRenderBombomb(){
    	if(renderBombomb == null){
    		renderBombomb = new EntityBombomb(CrystalMod.proxy.getClientWorld());
    	}
    	return renderBombomb;
    }
	
	@Override
	public void render(ItemStack stack) {
		TransformType type = lastTransform;
		EntityBombomb bombomb = getRenderBombomb();
		if(bombomb == null){
			return;
		}
		EnumDyeColor color = EnumDyeColor.YELLOW;
		UUID owner = null;
		if(stack.hasTagCompound()){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
			if(nbt.hasKey("EntityData")){
				NBTTagCompound compound = nbt.getCompoundTag("EntityData");
				if(compound.hasKey("OwnerUUID")){
					String id = compound.getString("OwnerUUID");
					if(!id.isEmpty() && UUIDUtils.isUUID(id)){
						owner = UUIDUtils.fromString(id);
					}
				}
				if(compound.hasKey("Color", 99)){
					color = EnumDyeColor.byDyeDamage(compound.getByte("Color"));
				}
			}
		}
		bombomb.setColor(color);
		bombomb.setOwnerId(owner);
		bombomb.setTamed(owner !=null);
		
		boolean atrib = true;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			float scale = 2.5f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(bombomb, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
	        GlStateManager.disableRescaleNormal();
	        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
	        GlStateManager.disableTexture2D();
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
		else if (type == TransformType.FIRST_PERSON_RIGHT_HAND || type == TransformType.FIRST_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 1.5f;
			GlStateManager.scale(0.8F*scale, 0.8F*scale, 0.8F*scale);
			GlStateManager.translate(2, 0.5, 0);
			if(type == TransformType.FIRST_PERSON_RIGHT_HAND){
				GlStateManager.rotate(60F, 0F, 1F, 0F);
			}
			if(type == TransformType.FIRST_PERSON_LEFT_HAND){
				GlStateManager.rotate(120F, 0F, 1F, 0F);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(bombomb, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}
		else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
		{
			GlStateManager.pushMatrix();
			float scale = 2.0f;
			GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
			if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(-45, 1, 0, 0);
				GlStateManager.translate(0, -5, 0.5);
			}else{
				GlStateManager.rotate(90, 0, 1, 0);
				GlStateManager.rotate(90-20, 0, 0, 1);
				GlStateManager.rotate(45, 1, 0, 0);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.translate(0, -5, 0.5);
			}
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(bombomb, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND || type == TransformType.FIXED){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			if(type == TransformType.FIXED){
				GlStateManager.translate(0, -0.4, 0);
			}
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(bombomb, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}

		if(atrib)GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}
	


	private TransformType lastTransform;
	
	@Override
	public TRSRTransformation getTransform(TransformType type) {
		lastTransform = type;
		return DynamicBaseModel.DEFAULT_PERSPECTIVE_TRANSFORMS.get(type);
	}

}
