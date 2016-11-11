package alec_wam.CrystalMod.entities.minions;

import java.util.Map;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import alec_wam.CrystalMod.CrystalMod;
import alec_wam.CrystalMod.client.model.dynamic.ICustomItemRenderer;
import alec_wam.CrystalMod.items.ModItems;
import alec_wam.CrystalMod.util.ItemNBTHelper;
import alec_wam.CrystalMod.util.UUIDUtils;

public class ItemRenderMinion implements ICustomItemRenderer {

	private static Map<MinionType, EntityMinionBase> entityCache = Maps.newHashMap();
    
    public static EntityMinionBase getRenderMinion(MinionType type){
    	EntityMinionBase entity = entityCache.get(type);
    	if(entity == null){
    		World world = CrystalMod.proxy.getClientWorld();
    		try
            {
            	Class<? extends EntityMinionBase> minionClass = type.getEntityClass();
                if (minionClass != null)
                {
                    entity = (EntityMinionBase)minionClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
    		if(entity !=null)entityCache.put(type, entity);
    	}
    	return entity;
    }
	
	@Override
	public void render(ItemStack stack, TransformType type) {

		MinionType mType = ItemMinion.getType(stack);
		EntityMinionBase minion = getRenderMinion(mType);
		if(minion == null){
			return;
		}
		
		ItemStack handStack = null;
		
		if(mType == MinionType.WORKER){
			ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
			handStack = pick;
		}
		
		if(mType == MinionType.WARRIOR){
			ItemStack sword = new ItemStack(ModItems.crystalSword);
			ItemNBTHelper.setString(sword, "Color", "blue");
			handStack = sword;
		}
		UUID owner = null;
		if(stack.hasTagCompound()){
			NBTTagCompound nbt = ItemNBTHelper.getCompound(stack);
			if(nbt.hasKey("EntityData")){
				NBTTagCompound compound = nbt.getCompoundTag("EntityData");
				if (compound.hasKey("HandItems", 9))
		        {
		            NBTTagList nbttaglist1 = compound.getTagList("HandItems", 10);

		            if (nbttaglist1.tagCount() > 0)
		            {
		                handStack = ItemStack.loadItemStackFromNBT(nbttaglist1.getCompoundTagAt(0));
		            }
		        }
				if(compound.hasKey("OwnerUUID")){
					String id = compound.getString("OwnerUUID");
					if(!id.isEmpty() && UUIDUtils.isUUID(id)){
						owner = UUIDUtils.fromString(id);
					}
				}
			}
		}
		minion.setOwnerId(owner);
		minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, handStack);
		
		boolean atrib = true;
		GlStateManager.pushMatrix();
		if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
		GlStateManager.scale(0.5F, 0.5F, 0.5F);

		if (type == TransformType.GUI)
		{
			GlStateManager.pushMatrix();
			float scale = 2.5f;
			//Vec3d offset = essence.getRenderOffset();
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -0.5, 0);
			
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minion, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
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
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minion, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
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
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minion, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.popMatrix();
		}
		else if(type == TransformType.GROUND){
			GlStateManager.pushMatrix();
			float scale = 3.0f;
			GlStateManager.scale(scale, scale, scale);
			GlStateManager.translate(0, -3, 0);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			Minecraft.getMinecraft().getRenderManager().doRenderEntity(minion, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
			GlStateManager.popMatrix();
		}

		if(atrib)GlStateManager.popAttrib();
		GlStateManager.popMatrix();
	}

}
