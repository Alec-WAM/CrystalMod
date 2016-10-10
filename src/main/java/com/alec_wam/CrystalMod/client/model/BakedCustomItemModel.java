package com.alec_wam.CrystalMod.client.model;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.vecmath.Matrix4f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.alec_wam.CrystalMod.CrystalMod;
import com.alec_wam.CrystalMod.client.model.dynamic.DynamicItemAndBlockModel;
import com.alec_wam.CrystalMod.entities.minions.EntityMinionBase;
import com.alec_wam.CrystalMod.entities.minions.ItemMinion;
import com.alec_wam.CrystalMod.entities.minions.MinionType;
import com.alec_wam.CrystalMod.items.ItemDragonWings;
import com.alec_wam.CrystalMod.items.ModItems;
import com.alec_wam.CrystalMod.items.game.ItemFlag;
import com.alec_wam.CrystalMod.tiles.spawner.EntityEssenceInstance;
import com.alec_wam.CrystalMod.tiles.spawner.ItemMobEssence;
import com.alec_wam.CrystalMod.util.ItemNBTHelper;
import com.alec_wam.CrystalMod.util.ProfileUtil;
import com.alec_wam.CrystalMod.util.client.RenderUtil;
import com.alec_wam.CrystalMod.world.game.tag.TagManager;

public class BakedCustomItemModel extends DynamicItemAndBlockModel
{
	private IBakedModel baseModel;
	private ItemStack stack;
	
	private Minecraft mc = Minecraft.getMinecraft();
	
	private TransformType prevTransform;
	
	public BakedCustomItemModel(IBakedModel model)
	{
		super(false, true);
		this.baseModel = model;
	}
	
	public BakedCustomItemModel(IBakedModel model, ItemStack s)
	{
		super(false, true);
		baseModel = model;
		stack = s;
	}
	
	private ModelDragonWings dragonModel = new ModelDragonWings(0.0F);
    private static final ResourceLocation enderDragonTextures = new ResourceLocation("textures/entity/enderdragon/dragon.png");
	
    public static String ESSENCE_CACHE_ID = "MobEssence.";
    public static String MINION_CACHE_ID = "MobEssence.";
    public static final ConcurrentMap<String, EntityLivingBase> entityCache = ProfileUtil.buildCache(3 * 60 * 60, 0);
    public static EntityPig defaultPig;
    
    public static EntityLivingBase getRenderEntity(String name){
    	EntityLivingBase entity = entityCache.get(ESSENCE_CACHE_ID+name);
    	if(entity == null){
    		@SuppressWarnings("rawtypes")
			EntityEssenceInstance essence = ItemMobEssence.getEssence(name);
    		if(essence == null){
    			if(defaultPig == null)defaultPig = ItemMobEssence.DEFAULT_PIG.createRenderEntity(CrystalMod.proxy.getClientWorld());
    			return defaultPig;
    		}else{
	    		entity = essence.createRenderEntity(CrystalMod.proxy.getClientWorld());
	    		if(entity !=null)entityCache.put(ESSENCE_CACHE_ID+name, entity);
    		}
    	}
    	return entity;
    }
    
    public static EntityLivingBase getRenderEntityNullable(String name){
    	EntityLivingBase entity = entityCache.get(name);
    	if(entity == null){
    		EntityEssenceInstance<?> essence = ItemMobEssence.getEssence(name);
    		if(essence == null){
    			return null;
    		}else{
	    		entity = essence.createRenderEntity(CrystalMod.proxy.getClientWorld());
	    		if(entity !=null)entityCache.put(name, entity);
    		}
    	}
    	return entity;
    }
    
    public static EntityMinionBase getRenderMinion(MinionType type){
    	String name = type.name().toLowerCase();
    	EntityLivingBase entity = entityCache.get(MINION_CACHE_ID+name);
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
    		if(entity !=null)entityCache.put(MINION_CACHE_ID+name, entity);
    	}
    	return (entity == null || !(entity instanceof EntityMinionBase)) ? null : (EntityMinionBase)entity;
    }
    
	private void doRender(TransformType type)
	{
		if(stack != null)
		{
			Item item = stack.getItem();
			if(item instanceof ItemFlag)
			{
				renderFlag(type, stack);
			}
			
			if(item instanceof ItemDragonWings){
				renderWings(stack);
			}
			
			if(item instanceof ItemMinion){
				MinionType mType = ItemMinion.getType(stack);
				EntityMinionBase minion = getRenderMinion(mType);
				if(minion == null){
					return;
				}
				
				if(mType == MinionType.WORKER){
					ItemStack pick = new ItemStack(Items.IRON_PICKAXE);
					minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, pick);
				}
				
				if(mType == MinionType.WARRIOR){
					ItemStack sword = new ItemStack(ModItems.crystalSword);
					ItemNBTHelper.setString(sword, "Color", "blue");
					minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword);
				}
				
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
			
			if(item instanceof ItemMobEssence){
				String name = ItemNBTHelper.getString(stack, ItemMobEssence.NBT_ENTITYNAME, "Pig");
				EntityLivingBase entity = getRenderEntity(name);
				if(entity == null){
					return;
				}
				@SuppressWarnings("rawtypes")
				EntityEssenceInstance essence = ItemMobEssence.getEssence(name);
	    		if(essence == null){
	    			essence = ItemMobEssence.DEFAULT_PIG;
	    		}
	    		boolean atrib = true;
				GlStateManager.pushMatrix();
				if(atrib)GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);

				if (type == TransformType.GUI)
				{
					GlStateManager.pushMatrix();
					float scale = essence.getRenderScale(type);
					Vec3d offset = essence.getRenderOffset();
					GlStateManager.scale(scale, scale, scale);
					GlStateManager.translate(offset.xCoord, offset.yCoord, offset.zCoord);
					
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
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
					float scale = essence.getRenderScale(type);
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
					Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
					GlStateManager.disableBlend();
					
					GlStateManager.enableLighting();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
					GlStateManager.popMatrix();
				}
				else if (type == TransformType.THIRD_PERSON_RIGHT_HAND || type == TransformType.THIRD_PERSON_LEFT_HAND)
				{
					GlStateManager.pushMatrix();
					float scale = essence.getRenderScale(type);
					GlStateManager.scale(1.5F*scale, 1.5F*scale, 1.5F*scale);
					if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
						GlStateManager.rotate(90, 0, 1, 0);
						GlStateManager.rotate(90-20, 0, 0, 1);
						GlStateManager.rotate(-45, 1, 0, 0);
						GlStateManager.translate(0, -1, 0.5);
					}else{
						GlStateManager.rotate(90, 0, 1, 0);
						GlStateManager.rotate(90-20, 0, 0, 1);
						GlStateManager.rotate(45, 1, 0, 0);
						GlStateManager.rotate(180, 0, 1, 0);
						GlStateManager.translate(0, -1, 0.5);
					}
					Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
					GlStateManager.popMatrix();
				}
				else if(type == TransformType.GROUND){
					GlStateManager.pushMatrix();
					float scale = essence.getRenderScale(type);
					GlStateManager.scale(scale, scale, scale);
					GlStateManager.translate(0, -1, 0);
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					Minecraft.getMinecraft().getRenderManager().doRenderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, true);
					GlStateManager.disableBlend();
					GlStateManager.enableLighting();
                    GlStateManager.enableBlend();
                    GlStateManager.enableColorMaterial();
					GlStateManager.popMatrix();
				}

				if(atrib)GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
			
			return;
		}
		
		if(type == TransformType.GUI)
		{
			GlStateManager.scale(0.625, 0.625, 0.625);
			GlStateManager.rotate(30.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-45.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	public void renderFlag(TransformType type, ItemStack stack){
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5, 0.5, 0.5);
		GlStateManager.rotate(180, 1, 0, 0);
		if(type == TransformType.GUI){
			GlStateManager.translate(-1, -0.1, 0);
			GlStateManager.scale(0.8, 0.8, 0.8);
			GlStateManager.rotate(90, 0, 1, 0);
		}else if(type == TransformType.THIRD_PERSON_RIGHT_HAND){
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.rotate(80, -1, 0, 0);
			GlStateManager.rotate(-50, 0, 0, 1);
			GlStateManager.translate(-0.8, 0, 0.4);
		}else if(type == TransformType.GROUND || type == TransformType.FIRST_PERSON_RIGHT_HAND){
			GlStateManager.translate(-0.5, 0, 0.5);
			GlStateManager.rotate(-45, 0, 1, 0);
		}
		GlStateManager.translate(-0.5, -0.5, -0.5);
		int color = ItemNBTHelper.getInteger(stack, "FlagColor", Color.WHITE.getRGB());
		TagManager.getInstance().renderFlag(color, 90);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.popMatrix();
	}
	
	public void renderWings(ItemStack stack){
		GlStateManager.pushMatrix();
    	Minecraft.getMinecraft().getTextureManager().bindTexture(enderDragonTextures);
    	GlStateManager.pushMatrix();
    	
    	float yaw = 90;
    	
    	GlStateManager.rotate(-yaw, 0, 1, 0);
    	GlStateManager.rotate(90, 1, 0, 0);
    	GlStateManager.translate(0, 0.3f, -0.6F);
    	double scale = 0.18;
    	GlStateManager.scale(scale, scale, scale);
    	float f = 0.5f;
    	for (int j = 0; j < 2; ++j)
        {
    		GlStateManager.enableCull();
            float f11 = f * (float)Math.PI * 2.0F;
            dragonModel.wing.rotateAngleX = 0.125F - (float)Math.cos((double)f11) * 0.2F;
            dragonModel.wing.rotateAngleY = 0.25F;
            
            dragonModel.wing.rotateAngleZ = 0.25f+(float)(Math.sin((double)f11) + 0.125D) * 0.4F;
            
            dragonModel.wingTip.rotateAngleZ = -((float)(Math.sin((double)(f11 + 2.0F)) + 0.5D)) * 0.75F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.4, 0, 0);
            dragonModel.wing.render(0.0625F);
            GlStateManager.popMatrix();
            GlStateManager.scale(-1.0F, 1.0F, 1.0F);

            if (j == 0)
            {
                GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
            }
        }
    	
    	GlStateManager.popMatrix();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	GlStateManager.popMatrix();
	}
	
	@Override
	public List<BakedQuad> getGeneralQuads()
	{
        
		Tessellator tessellator = Tessellator.getInstance();
		VertexFormat prevFormat = null;
		
		if(RenderUtil.isDrawing(tessellator))
		{
			prevFormat = tessellator.getBuffer().getVertexFormat();
			tessellator.draw();
		}
		
		List<BakedQuad> generalQuads = new LinkedList<BakedQuad>();
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		GlStateManager.rotate(180, 0.0F, 1.0F, 0.0F);
    	doRender(prevTransform);
        GlStateManager.enableLighting();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        GlStateManager.enableColorMaterial();
        GlStateManager.colorMaterial(1032, 5634); 
        GlStateManager.enableCull();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    	GlStateManager.popMatrix();
    	
    	if(prevFormat != null)
    	{
    		net.minecraft.client.renderer.VertexBuffer worldrenderer = tessellator.getBuffer();
	    	worldrenderer.begin(7, prevFormat);
    	}
		
		return generalQuads;
	}

	@Override
	public boolean isAmbientOcclusion()
	{
		return baseModel == null ? true : baseModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d()
	{
		return baseModel == null ? true : baseModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer()
	{
		return baseModel == null ? false : baseModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture()
	{
		return baseModel == null ? RenderUtil.getTexture(Blocks.STONE.getDefaultState()) : baseModel.getParticleTexture();
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemCameraTransforms getItemCameraTransforms()
	{
		return baseModel == null ? ItemCameraTransforms.DEFAULT : baseModel.getItemCameraTransforms();
	}
    
	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) 
    {    	
        prevTransform = cameraTransformType;
    	
        return super.handlePerspective(cameraTransformType);
    }

	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleBlockState(IBlockState state, EnumFacing side, long rand) {
		return null;
	}

	@Override
	public net.minecraft.client.renderer.block.model.IBakedModel handleItemState(ItemStack stack, World world, EntityLivingBase entity) {
		return new BakedCustomItemModel(baseModel, stack);
	}
}