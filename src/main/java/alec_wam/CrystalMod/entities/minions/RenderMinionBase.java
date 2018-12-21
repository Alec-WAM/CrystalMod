package alec_wam.CrystalMod.entities.minions;

import java.util.UUID;

import alec_wam.CrystalMod.util.client.DownloadedTextures;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerArrow;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderMinionBase extends RenderBiped<EntityMinionBase> {

	public RenderMinionBase(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelMinionBase(0.0F, false), 0.5F);
		this.addLayer(new LayerBipedArmor(this));
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerArrow(this));
        this.addLayer(new LayerCustomHead(this.getMainModel().bipedHead));
        this.addLayer(new LayerBackItem(this));
	}
	
    @Override
	public ModelMinionBase getMainModel()
    {
        return (ModelMinionBase)super.getMainModel();
    }
    
    @Override
	protected void preRenderCallback(EntityMinionBase entitylivingbaseIn, float partialTickTime)
    {
    	super.preRenderCallback(entitylivingbaseIn, partialTickTime);
    	this.mainModel.isChild = false;
    	
        float f = 0.9375F/2;
        GlStateManager.scale(f, f, f);
    }
    
    @Override
	public void doRender(EntityMinionBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        double d0 = y;

        if (entity.isSneaking())
        {
            d0 = y - 0.125D;
        }
        
        if(entity.isSitting()){
        	d0-=0.25D;
        }

        this.setModelVisibilities(entity);
        super.doRender(entity, x, d0, z, entityYaw, partialTicks);
        //TODO Make Workers render through blocks
        /*if(entity instanceof EntityMinionWorker){
        	EntityMinionWorker worker = (EntityMinionWorker)entity;
	        boolean overlay = false;
	        EntityPlayer player = CrystalMod.proxy.getClientPlayer();
	        ItemStack staff = ItemStackTools.getEmptyStack();
	        if(player.getHeldItemMainhand().getItem() == ModItems.minionStaff){
	        	NBTTagCompound nbt = ItemNBTHelper.getCompound(staff).getCompoundTag("WorksitePos");
	        	if(!nbt.hasNoTags()){
	        		staff = player.getHeldItemMainhand();
	        	}
	        }
	        if(player.getHeldItemOffhand().getItem() == ModItems.minionStaff){
	        	NBTTagCompound nbt = ItemNBTHelper.getCompound(staff);
	        	if(nbt.hasKey("WorksitePos")){
	        		staff = player.getHeldItemOffhand();
	        	}
	        }
	        if(ItemStackTools.isValid(staff)){
	        	//NBTTagCompound nbt = ItemNBTHelper.getCompound(staff);
	        	//if(nbt.hasKey("WorksitePos")){
	        		overlay = /*worker.isWorkingAtWorksite(NBTUtil.getPosFromTag(nbt.getCompoundTag("WorksitePos"))) && !CrystalMod.proxy.getClientPlayer().canEntityBeSeen(entity);
	        	//}
	        }
	        
	        boolean depth = true;
	        if(overlay){
	        	GlStateManager.pushMatrix();
	        	GlStateManager.pushAttrib();
	        	
	        	GlStateManager.enableAlpha();
	        	GlStateManager.enableBlend();
	
	        	GlStateManager.color(0.0f, 1.0f, 0.0f, 1F);
	            
	        	if(depth){
	        		GlStateManager.disableDepth();
	        	}
	        	
	        	GlStateManager.depthMask(false);
	            GlStateManager.depthFunc(514);
	            GlStateManager.disableLighting();
	            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
	            
	        	
	        	GlStateManager.pushMatrix();
		        float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
		        float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
		        float f2 = f1 - f;
		        boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
		        if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase)
		        {
		            EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
		            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
		            f2 = f1 - f;
		            float f3 = MathHelper.wrapDegrees(f2);
		
		            if (f3 < -85.0F)
		            {
		                f3 = -85.0F;
		            }
		
		            if (f3 >= 85.0F)
		            {
		                f3 = 85.0F;
		            }
		
		            f = f1 - f3;
		
		            if (f3 * f3 > 2500.0F)
		            {
		                f += f3 * 0.2F;
		            }
		        }
		
		        float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
		        this.renderLivingAt(entity, x, y, z);
		        
		        float f8 = this.handleRotationFloat(entity, partialTicks);
		        this.applyRotations(entity, f8, f, partialTicks);
		        float f4 = this.prepareScale(entity, partialTicks);
		        float f5 = 0.0F;
		        float f6 = 0.0F;
		
		        if (!entity.isRiding())
		        {
		            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
		            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
		
		            if (entity.isChild())
		            {
		                f6 *= 3.0F;
		            }
		
		            if (f5 > 1.0F)
		            {
		                f5 = 1.0F;
		            }
		        }
		        if (!this.renderMarker)
		        {
		        	if (this.bindEntityTexture(entity))
		            {
		        		this.mainModel.render(entity, f6, f5, f8, f2, f7, f4);
		            }
		        	
		        }
		        //this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
		        GlStateManager.popMatrix();     
		        
		        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		        GlStateManager.enableLighting();
		        GlStateManager.depthFunc(515);
		        GlStateManager.depthMask(true);
		        
		        if(depth){
	        		GlStateManager.enableDepth();
	        	}
	        	
		        GlStateManager.popAttrib();
		        GlStateManager.popMatrix();     	
	        }
        }*/
    }
    
    private void setModelVisibilities(EntityMinionBase clientPlayer)
    {
    	ModelMinionBase modelplayer = this.getMainModel();
    	ItemStack itemstack = clientPlayer.getHeldItemMainhand();
        ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
        modelplayer.setInvisible(true);
        modelplayer.bipedHeadwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.HAT);
        modelplayer.bipedBodyWear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
        modelplayer.bipedLeftLegwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
        modelplayer.bipedRightLegwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
        modelplayer.bipedLeftArmwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
        modelplayer.bipedRightArmwear.showModel = true;//clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
        modelplayer.leftArmPose = ModelBiped.ArmPose.EMPTY;
        modelplayer.isSneak = clientPlayer.isSneaking();

        ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
        ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

        if (itemstack != null)
        {
            modelbiped$armpose = ModelBiped.ArmPose.ITEM;

            if (clientPlayer.getItemInUseCount() > 0)
            {
                EnumAction enumaction = itemstack.getItemUseAction();

                if (enumaction == EnumAction.BLOCK)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                }
                else if (enumaction == EnumAction.BOW)
                {
                    modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                }
            }
        }

        if (itemstack1 != null)
        {
            modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

            if (clientPlayer.getItemInUseCount() > 0)
            {
                EnumAction enumaction1 = itemstack1.getItemUseAction();

                if (enumaction1 == EnumAction.BLOCK)
                {
                    modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                }
            }
        }

        if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
        {
            modelplayer.rightArmPose = modelbiped$armpose;
            modelplayer.leftArmPose = modelbiped$armpose1;
        }
        else
        {
            modelplayer.rightArmPose = modelbiped$armpose1;
            modelplayer.leftArmPose = modelbiped$armpose;
        }
    }
	
	@Override
	protected ResourceLocation getEntityTexture(EntityMinionBase entity) {
		try{
			UUID owner = entity.getOwnerId();
			if(owner !=null){
				ResourceLocation skin = DownloadedTextures.getSkin(owner);
				if(skin !=null){
					return skin;
				}
			}
		}catch(Exception e){
		}
		return super.getEntityTexture(entity);
	}
	
	public static final Factory FACTORY = new Factory();
    public static class Factory implements IRenderFactory<EntityMinionBase> {

        @Override
        public Render<? super EntityMinionBase> createRenderFor(RenderManager manager) {
          return new RenderMinionBase(manager);
        }
    }

}
