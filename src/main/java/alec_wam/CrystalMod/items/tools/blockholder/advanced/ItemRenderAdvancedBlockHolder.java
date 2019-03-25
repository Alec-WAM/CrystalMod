package alec_wam.CrystalMod.items.tools.blockholder.advanced;

import alec_wam.CrystalMod.items.tools.blockholder.ItemRenderBlockHolder;
import alec_wam.CrystalMod.items.tools.blockholder.advanced.ItemAdvancedBlockHolder.BlockStackData;
import alec_wam.CrystalMod.util.ItemStackTools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public class ItemRenderAdvancedBlockHolder extends ItemRenderBlockHolder {
	
	public static final ItemRenderAdvancedBlockHolder INSTANCE = new ItemRenderAdvancedBlockHolder();
	
	@Override
	public String getModelName(){
		return "advblockholder";
	}
	
	@Override
	public void renderSelectedBlock(ItemStack stack){
		if(stack.getItem() instanceof ItemAdvancedBlockHolder){
			BlockStackData data = ItemAdvancedBlockHolder.getSelectedData(stack);
			ItemStack blockStack = data.stack;
			if(ItemStackTools.isValid(blockStack)){
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.5, 0.5, 0.5);			
				if(lastTransform == TransformType.GUI){
					GlStateManager.rotate(90.0F, 0, 1, 0);
				}		
				if(lastTransform == TransformType.FIXED){
					GlStateManager.rotate(180.0F, 0, 1, 0);
				}
				if(lastTransform == TransformType.FIRST_PERSON_LEFT_HAND || lastTransform == TransformType.FIRST_PERSON_RIGHT_HAND || lastTransform == TransformType.THIRD_PERSON_RIGHT_HAND){
					GlStateManager.rotate(270.0F, 0, 1, 0);
				}
				if(lastTransform == TransformType.THIRD_PERSON_LEFT_HAND){
					GlStateManager.rotate(90.0F, 0, 1, 0);
				}
				int count = data.count;
				Minecraft.getMinecraft().getRenderItem().renderItem(blockStack, TransformType.FIXED);
				if(count <= 0){	        	
					IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(blockStack, (World)null, (EntityLivingBase)null);
			        model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.FIXED, false);
			        GlStateManager.translate(-0.5, -0.5, -0.5);	
					renderItemModel(stack, model, TransformType.NONE, 2147418112, false);				
				}
				GlStateManager.popMatrix();
			}
		}
	}
	
}
